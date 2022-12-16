//
// Created by itsaky on 12/15/22.
//

#ifndef ANDROIDTREESITTER_JNI_STRING_H
#define ANDROIDTREESITTER_JNI_STRING_H

#include <jni.h>
#include <assert.h>
#include <stdlib.h>
#include <string>
#include <cstring>

jstring FNI_NewString(JNIEnv *env, const jchar *unicodeChars, jsize len);

jsize FNI_GetStringLength(JNIEnv *env, jstring string);

const jchar *FNI_GetStringChars(JNIEnv *env, jstring string, uint32_t *length, jboolean *isCopy);

void FNI_ReleaseStringChars(const jchar *chars);

#endif //ANDROIDTREESITTER_JNI_STRING_H
