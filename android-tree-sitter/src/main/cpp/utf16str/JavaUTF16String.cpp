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
Java_com_itsaky_androidide_treesitter_UTF16String_00024Native_newUtf16String(JNIEnv *env, jclass clazz,
                                                                             jstring src) {
    return (jlong) cache.create(env, src);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_itsaky_androidide_treesitter_UTF16String_00024Native_toString(JNIEnv *env, jclass clazz,
                                                                       jlong pointer) {
    return as_str(pointer)->to_jstring(env);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_UTF16String_00024Native_append(JNIEnv *env, jclass clazz,
                                                                     jlong pointer, jstring str) {
    as_str(pointer)->append(env, str);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_UTF16String_00024Native_length(JNIEnv *env, jclass clazz,
                                                                     jlong pointer) {
    return as_str(pointer)->length();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_UTF16String_00024Native_erase(JNIEnv *env, jclass clazz,
                                                                    jlong pointer) {
    cache.erase(as_str(pointer));
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_UTF16String_00024Native_byteLength(JNIEnv *env, jclass clazz,
                                                                         jlong pointer) {
    return as_str(pointer)->length_bytes();
}