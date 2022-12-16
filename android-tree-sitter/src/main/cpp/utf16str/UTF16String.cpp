/*
 *  This file is part of AndroidIDE.
 *
 *  AndroidIDE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  AndroidIDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/\>.
 */

#include <string>
#include <sstream>
#include <cstring>
#include <utility>
#include <iostream>

#include "UTF16String.h"
#include "../utils/jni_string.h"
#include "../utils/jni_utils.h"
#include "../utils/ts_obj_utils.h"
#include "../utils/utils.h"

#define HI_BYTE_SHIFT 0
#define LO_BYTE_SHIFT 8
#define CODER 1

using namespace std;

UTF16String::UTF16String() {
    _string = vector<jbyte>();
}

void UTF16String::append(jchar c) {
    _string.emplace_back((jbyte) (c >> HI_BYTE_SHIFT));
    _string.emplace_back((jbyte) (c >> LO_BYTE_SHIFT));
}

jchar UTF16String::char_at(int index) {
    auto idx = index << CODER;
    int hi = (_string[idx++] & 0xff) << HI_BYTE_SHIFT;
    int lo = (_string[idx] & 0xff) << LO_BYTE_SHIFT;
    return (jchar) (hi | lo);
}

UTF16String *UTF16String::append(JNIEnv *env, jstring src) {
    uint32_t len;
    const jchar *chars = FNI_GetStringChars(env, src, &len);
    _string.reserve(vsize(_string) + len);
    for (int i = 0; i < len; ++i) {
        auto c = *(chars + i);
        append(c);
    }
    FNI_ReleaseStringChars(chars);
    return this;
}

UTF16String *UTF16String::append(JNIEnv *env, jstring src, jint from, jint len) {
    const jchar *chars = FNI_GetStringChars(env, src, nullptr);
    for (int i = from; i < from + len; ++i) {
        auto c = *(chars + i);
        append(c);
    }
    return this;
}

UTF16String *UTF16String::insert(jchar c, int index) {
    _string.insert(_string.begin() + (index << CODER), (jbyte) (c >> HI_BYTE_SHIFT));
    _string.insert(_string.begin() + (index << CODER) + 1, (jbyte) (c >> LO_BYTE_SHIFT));
    return this;
}

UTF16String *UTF16String::insert(JNIEnv *env, jstring src, jint index) {
    uint32_t len;
    const jchar *chars = FNI_GetStringChars(env, src, &len);
    _string.reserve(length_bytes() + len);
    for (int i = 0; i < len; ++i) {
        auto c = *(chars + i);
        insert(c, index  + i);
    }

    cout << endl;
    return this;
}

jint UTF16String::length_bytes() {
    return vsize(_string);
}

jint UTF16String::length() {
    return length_bytes() >> CODER;
}

jstring UTF16String::to_jstring(JNIEnv *env) {
    return FNI_NewString(env, _string.data(), length_bytes());
}

UTF16String *as_str(jlong pointer) {
    return (UTF16String *) pointer;
}

bool UTF16String::operator==(const UTF16String &rhs) const {
    return _string == rhs._string;
}

bool UTF16String::operator!=(const UTF16String &rhs) const {
    return !(rhs == *this);
}

int vsize(const vector<jbyte> &vc) {
    return static_cast<int>(vc.size());
}