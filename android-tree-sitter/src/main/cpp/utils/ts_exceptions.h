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

#ifndef ATS_TS_EXCEPTIONS_H
#define ATS_TS_EXCEPTIONS_H

#include <jni.h>

/**
 * Throws an exception in the JVM with the given class name and message.
 * @param env The JNI environment.
 * @param klass The class name of the exception.
 * @param message The message for the exception.
 */
int throw_exception(JNIEnv *env, const char *klass, const char *message);

/**
 * Throws an `IllegalArgumentException` in the JVM with the given message.
 * @param env The JNI environment.
 * @param message The message for the exception.
 */
int throw_illegal_args(JNIEnv *env, const char *message);

/**
 * Throws an `NullPointerException` in the JVM with the given message.
 * @param env The JNI environment.
 * @param message The message for the exception.
 */
int throw_npe(JNIEnv *env, const char *message);

#endif //ATS_TS_EXCEPTIONS_H
