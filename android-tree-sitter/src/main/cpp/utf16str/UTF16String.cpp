/*
 *  This file is part of android-tree-sitter.
 *
 *  android-tree-sitter library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  android-tree-sitter library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *  along with android-tree-sitter.  If not, see <https://www.gnu.org/licenses/>.
 */

#include <string>
#include <sstream>
#include <cstring>
#include <utility>
#include <iostream>

#include "UTF16String.h"
#include "../cache/StrCache.h"
#include "../utils/jni_string.h"
#include "../utils/jni_utils.h"
#include "../utils/ts_obj_utils.h"
#include "../utils/utils.h"

#define HI_BYTE_SHIFT 0
#define LO_BYTE_SHIFT 8
#define CODER 1

using namespace std;

const jbyte *to_bytes(const jchar *chars, jint len);

UTF16String::UTF16String() {
    _string = vector<jbyte>();
}

UTF16String::UTF16String(vector<jbyte> bytes) {
    _string = std::move(bytes);
}

void UTF16String::append(jchar c) {
    _string.emplace_back((jbyte) (c >> HI_BYTE_SHIFT));
    _string.emplace_back((jbyte) (c >> LO_BYTE_SHIFT));
}

jbyte UTF16String::byte_at(jint index) {
    return _string.at(index);
}

UTF16String *UTF16String::set_byte_at(jint index, jbyte byte) {
    _string[index] = byte;
    return this;
}

jchar UTF16String::char_at(jint index) {
    auto idx = index << CODER;
    jint hi = (_string[idx++] & 0xff) << HI_BYTE_SHIFT;
    jint lo = (_string[idx] & 0xff) << LO_BYTE_SHIFT;
    return (jchar) (hi | lo);
}

UTF16String *UTF16String::set_char_at(jint index, jchar c) {
    jint idx = index << CODER;
    _string[idx++] = (jbyte) (c >> HI_BYTE_SHIFT);
    _string[idx] = (jbyte) (c >> LO_BYTE_SHIFT);
    return this;
}

UTF16String *UTF16String::append(JNIEnv *env, jstring src) {
    jint len;
    const jchar *chars = FNI_GetStringChars(env, src, &len);
    _string.reserve(vsize(_string) + len);
    for (jint i = 0; i < len; ++i) {
        auto c = *(chars + i);
        append(c);
    }
    FNI_ReleaseStringChars(chars);
    return this;
}

UTF16String *UTF16String::append(JNIEnv *env, jstring src, jint from, jint len) {
    const jchar *chars = FNI_GetStringChars(env, src, nullptr);
    for (jint i = from; i < from + len; ++i) {
        auto c = *(chars + i);
        append(c);
    }
    FNI_ReleaseStringChars(chars);
    return this;
}

UTF16String *UTF16String::insert(jint index, jbyte byte) {
    _string.insert(_string.begin() + index, byte);
    return this;
}

UTF16String *UTF16String::insert(jint index, jchar c) {
    insert(index << CODER, (jbyte) (c >> HI_BYTE_SHIFT));
    insert((index << CODER) + 1, (jbyte) (c >> LO_BYTE_SHIFT));
    return this;
}

UTF16String *UTF16String::insert(JNIEnv *env, jstring src, jint index) {
    jint len;
    const jchar *chars = FNI_GetStringChars(env, src, &len);
    _string.reserve(byte_length() + len);
    for (jint i = 0; i < len; ++i) {
        auto c = *(chars + i);
        insert(index + i, c);
    }

    FNI_ReleaseStringChars(chars);

    return this;
}

UTF16String *UTF16String::delete_chars(jint start, jint end) {
    return delete_bytes(start << CODER, end << CODER);
}

UTF16String *UTF16String::delete_bytes(jint start, jint end) {
    const auto &iter = _string.begin();
    _string.erase(iter + start, iter + end);
    return this;
}

UTF16String *UTF16String::replace_chars(JNIEnv *env, jint start, jint end, jstring str) {
    return replace_bytes(env, start << CODER, end << CODER, str);
}

UTF16String *UTF16String::replace_bytes(JNIEnv *env, jint start, jint end, jstring str) {
    jint len;
    const jchar *chars = FNI_GetStringChars(env, str, &len);
    jint blen = len << CODER;
    jint last = blen < end ? blen : end;
    const jbyte *bytes = to_bytes(chars, len);

    jint i = start;
    while (i < last) {
        set_byte_at(i, *(bytes + i));
        ++i;
    }

    if (blen > end) {
        while (i < blen) {
            insert(i, *(bytes + i));
            ++i;
        }
    } else if (blen < end) {
        delete_bytes(i, end);
    }

    FNI_ReleaseStringChars(chars);
    delete [] bytes;

    return this;
}

UTF16String *UTF16String::substring_chars(jint start, jint end) {
    return substring_bytes(start << CODER, end << CODER);
}

UTF16String *UTF16String::substring_bytes(jint start, jint end) {
    auto copy = vector<jbyte>();
    for (int i = start; i < end; ++i) {
        copy.emplace_back(_string[i]);
    }
    return cache.create(copy);
}

jstring UTF16String::subjstring_chars(JNIEnv *env, jint start, jint end) {
    return substring_chars(start, end)->to_jstring(env);
}

jstring UTF16String::subjstring_bytes(JNIEnv *env, jint start, jint end) {
    return substring_bytes(start, end)->to_jstring(env);
}

jint UTF16String::byte_length() {
    return vsize(_string);
}

jint UTF16String::length() {
    return byte_length() >> CODER;
}

jstring UTF16String::to_jstring(JNIEnv *env) {
    return FNI_NewString(env, _string.data(), byte_length());
}

const char *UTF16String::to_cstring() {
    char *chars = new char[byte_length()];
    for (jint i = 0; i < byte_length(); ++i) {
        *(chars + i) = _string.at(i);
    }
    return chars;
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

const jbyte *to_bytes(const jchar *chars, jint len) {
    auto *bytes = new jbyte[len << CODER];
    for (int i = 0; i < len; ++i) {
        jint idx = i << CODER;
        jchar c = *(chars + i);
        *(bytes + idx++) = (jbyte) (c >> HI_BYTE_SHIFT);
        *(bytes + idx) = (jbyte) (c >> LO_BYTE_SHIFT);
    }
    return bytes;
}

jint vsize(const vector<jbyte> &vc) {
    return static_cast<jint>(vc.size());
}