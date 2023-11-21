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

#include <jni.h>
#include <algorithm>
#include <list>
#include <iostream>

#include "UTF16String.h"
#include "../cache/StrCache.h"

#include "ts_utf16string.h"

static void
UTF16String_append(JNIEnv *env, jclass clazz, jlong pointer, jstring str) {
  as_str(env, pointer)->append(env, str);
}

static void UTF16String_appendPart(JNIEnv *env,
                                   jclass clazz,
                                   jlong pointer,
                                   jstring str,
                                   jint from_index,
                                   jint length) {
  as_str(env, pointer)->append(env, str, from_index, length);
}

static jint UTF16String_length(JNIEnv *env, jclass clazz, jlong pointer) {
  return as_str(env, pointer)->length();
}

static void UTF16String_erase(JNIEnv *env, jclass clazz, jlong pointer) {
  StrCache::getInstance().erase(as_str(env, pointer));
}

static jint UTF16String_byteLength(JNIEnv *env, jclass clazz, jlong pointer) {
  return as_str(env, pointer)->byte_length();
}

static void UTF16String_insert(JNIEnv *env,
                               jclass clazz,
                               jlong pointer,
                               jstring str,
                               jint index) {
  as_str(env, pointer)->insert(env, str, index);
}

static void UTF16String_deleteChars(JNIEnv *env,
                                    jclass clazz,
                                    jlong pointer,
                                    jint start,
                                    jint end) {
  as_str(env, pointer)->delete_chars(start, end);
}

static void UTF16String_deleteBytes(JNIEnv *env,
                                    jclass clazz,
                                    jlong pointer,
                                    jint start,
                                    jint end) {
  as_str(env, pointer)->delete_bytes(start, end);
}

static void UTF16String_replaceChars(JNIEnv *env,
                                     jclass clazz,
                                     jlong pointer,
                                     jint start,
                                     jint end,
                                     jstring str) {
  as_str(env, pointer)->replace_chars(env, start, end, str);
}

static void UTF16String_replaceBytes(JNIEnv *env,
                                     jclass clazz,
                                     jlong pointer,
                                     jint start,
                                     jint end,
                                     jstring str) {
  as_str(env, pointer)->replace_bytes(env, start, end, str);
}

static jlong UTF16String_substring_chars(JNIEnv *env,
                                         jclass clazz,
                                         jlong pointer,
                                         jint start,
                                         jint end) {
  return (jlong) as_str(env, pointer)->substring_chars(start, end);
}

static jlong UTF16String_substring_bytes(JNIEnv *env,
                                         jclass clazz,
                                         jlong pointer,
                                         jint start,
                                         jint end) {
  return (jlong) as_str(env, pointer)->substring_bytes(start, end);
}

static jstring UTF16String_subjstring_chars(JNIEnv *env,
                                            jclass clazz,
                                            jlong pointer,
                                            jint start,
                                            jint end) {
  return as_str(env, pointer)->subjstring_chars(env, start, end);
}

static jstring UTF16String_subjstring_bytes(JNIEnv *env,
                                            jclass clazz,
                                            jlong pointer,
                                            jint start,
                                            jint end) {
  return as_str(env, pointer)->subjstring_bytes(env, start, end);
}

static jbyte
UTF16String_byteAt(JNIEnv *env, jclass clazz, jlong pointer, jint index) {
  return as_str(env, pointer)->byte_at(index);
}

static void UTF16String_setByteAt(JNIEnv *env,
                                  jclass clazz,
                                  jlong pointer,
                                  jint index,
                                  jbyte b) {
  as_str(env, pointer)->set_byte_at(index, b);
}

static jchar
UTF16String_chatAt(JNIEnv *env, jclass clazz, jlong pointer, jint index) {
  return as_str(env, pointer)->char_at(index);
}

static void UTF16String_setCharAt(JNIEnv *env,
                                  jclass clazz,
                                  jlong pointer,
                                  jint index,
                                  jchar c) {
  as_str(env, pointer)->set_char_at(index, c);
}

static jstring UTF16String_toString(JNIEnv *env, jclass clazz, jlong pointer) {
  return as_str(env, pointer)->to_jstring(env);
}

void UTF16String_Native__SetJniMethods(JNINativeMethod *methods, int count) {
  SET_JNI_METHOD(methods, UTF16String_Native_byteAt, UTF16String_byteAt);
  SET_JNI_METHOD(methods, UTF16String_Native_setByteAt, UTF16String_setByteAt);
  SET_JNI_METHOD(methods, UTF16String_Native_chatAt, UTF16String_chatAt);
  SET_JNI_METHOD(methods, UTF16String_Native_setCharAt, UTF16String_setCharAt);
  SET_JNI_METHOD(methods, UTF16String_Native_append, UTF16String_append);
  SET_JNI_METHOD(methods, UTF16String_Native_appendPart, UTF16String_appendPart);
  SET_JNI_METHOD(methods, UTF16String_Native_insert, UTF16String_insert);
  SET_JNI_METHOD(methods, UTF16String_Native_deleteChars, UTF16String_deleteChars);
  SET_JNI_METHOD(methods, UTF16String_Native_deleteBytes, UTF16String_deleteBytes);
  SET_JNI_METHOD(methods, UTF16String_Native_replaceChars, UTF16String_replaceChars);
  SET_JNI_METHOD(methods, UTF16String_Native_replaceBytes, UTF16String_replaceBytes);
  SET_JNI_METHOD(methods, UTF16String_Native_substring_chars,
                 UTF16String_substring_chars);
  SET_JNI_METHOD(methods, UTF16String_Native_substring_bytes,
                 UTF16String_substring_bytes);
  SET_JNI_METHOD(methods, UTF16String_Native_subjstring_chars,
                 UTF16String_subjstring_chars);
  SET_JNI_METHOD(methods, UTF16String_Native_subjstring_bytes,
                 UTF16String_subjstring_bytes);
  SET_JNI_METHOD(methods, UTF16String_Native_toString, UTF16String_toString);
  SET_JNI_METHOD(methods, UTF16String_Native_length, UTF16String_length);
  SET_JNI_METHOD(methods, UTF16String_Native_byteLength, UTF16String_byteLength);
  SET_JNI_METHOD(methods, UTF16String_Native_erase, UTF16String_erase);
}