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

#include <jni.h>
#include <algorithm>
#include <list>
#include <iostream>

#include "UTF16String.h"
#include "../cache/StrCache.h"
#include "../utils/utils.h"

StrCache cache;

extern "C"
JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_string_UTF16String_00024Native_newUtf16String(JNIEnv *env, jclass clazz,
                                                                             jstring src) {
    return (jlong) cache.create(env, src);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_itsaky_androidide_treesitter_string_UTF16String_00024Native_toString(JNIEnv *env, jclass clazz,
                                                                       jlong pointer) {
    return as_str(pointer)->to_jstring(env);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_string_UTF16String_00024Native_append(JNIEnv *env, jclass clazz,
                                                                     jlong pointer, jstring str) {
    as_str(pointer)->append(env, str);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_string_UTF16String_00024Native_appendPart(JNIEnv *env, jclass clazz,
                                                                     jlong pointer, jstring str, jint from_index, jint length) {
    as_str(pointer)->append(env, str, from_index, length);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_string_UTF16String_00024Native_length(JNIEnv *env, jclass clazz,
                                                                     jlong pointer) {
    return as_str(pointer)->length();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_string_UTF16String_00024Native_erase(JNIEnv *env, jclass clazz,
                                                                    jlong pointer) {
    cache.erase(as_str(pointer));
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_string_UTF16String_00024Native_byteLength(JNIEnv *env, jclass clazz,
                                                                         jlong pointer) {
    return as_str(pointer)->length_bytes();
}
extern "C"
JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_string_UTF16String_00024Native_insert(JNIEnv *env, jclass clazz,
                                                                     jlong pointer, jstring str,
                                                                     jint index) {
    as_str(pointer)->insert(env, str, index);
}