//
// Created by itsaky on 12/15/22.
//

#include <jni.h>

#ifndef ANDROIDTREESITTER_JNI_UTILS_H
#define ANDROIDTREESITTER_JNI_UTILS_H

void throw_ioob(JNIEnv *env, const char *msg);
void throw_neg_arr_size(JNIEnv *env);
void throw_ex(JNIEnv *env, jclass klass, const char *msg);

#endif //ANDROIDTREESITTER_JNI_UTILS_H
