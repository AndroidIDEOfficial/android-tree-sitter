//
// Created by itsaky on 12/15/22.
//

#include <string>
#include <sstream>
#include <cstring>
#include <utility>

#include "UTF16String.h"
#include "../utils/jni_string.h"
#include "../utils/jni_utils.h"
#include "../utils/ts_obj_utils.h"
#include "../utils/utils.h"

#define HI_BYTE_SHIFT 0
#define LO_BYTE_SHIFT 8
#define CODER 1

using namespace std;

jchar get_char(vector<jbyte> bytes, int index) {
    auto idx = index << CODER;
    int hi = (bytes.at(idx++) & 0xff) << HI_BYTE_SHIFT;
    int lo = (bytes.at(idx) & 0xff) << LO_BYTE_SHIFT;
    return (jchar) (hi | lo);
}

UTF16String::UTF16String() {
    _string = vector<jbyte>();
}

UTF16String *UTF16String::append(JNIEnv *env, jstring src) {
    uint32_t len;
    const jchar *chars = FNI_GetStringChars(env, src, &len, nullptr);
    for (int i = 0; i < len; ++i) {
        auto c = *(chars + i);
        _string.emplace_back((jbyte) (c >> HI_BYTE_SHIFT));
        _string.emplace_back((jbyte) (c >> LO_BYTE_SHIFT));
    }
    FNI_ReleaseStringChars(chars);
    return this;
}

jint UTF16String::length_bytes() {
    return vsize(_string);
}

jint UTF16String::length() {
    return length_bytes() >> CODER;
}

jstring UTF16String::to_jstring(JNIEnv *env) {
    jint len = length();
    auto *dst = new jchar[len];
    auto srcBegin = 0;
    auto srcEnd = len;
    auto dstbegin = 0;
    for (int i = srcBegin; i < srcEnd; ++i) {
        dst[dstbegin++] = get_char(_string, i);
    }
    return FNI_NewString(env, dst, len);
}

UTF16String *as_str(jlong pointer) {
    return (UTF16String *) pointer;
}

int vsize(const vector<jbyte>& vc) {
    return static_cast<int>(vc.size());
}

bool UTF16String::operator==(const UTF16String &rhs) const {
    return _string == rhs._string;
}

bool UTF16String::operator!=(const UTF16String &rhs) const {
    return !(rhs == *this);
}
