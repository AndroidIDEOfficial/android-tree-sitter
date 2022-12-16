//
// Created by itsaky on 12/15/22.
//

#include "jni_utils.h"

jclass ioob = nullptr;

void throw_ex(JNIEnv *env, jclass klass, const char *msg) {
    env->ThrowNew(klass, msg);
}

void throw_ioob(JNIEnv *env, const char *msg) {
    if (!ioob) {
        ioob = env->FindClass("java/lang/IndexOutOfBoundsException");
    }
    throw_ex(env, ioob, msg);
}

void throw_neg_arr_size(JNIEnv *env) {
    throw_ex(env, env->FindClass("java/lang/NegativeArraySizeException"), "Negative array size");
}
