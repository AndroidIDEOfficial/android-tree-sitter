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