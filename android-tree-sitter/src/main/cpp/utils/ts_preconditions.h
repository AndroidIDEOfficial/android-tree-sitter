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

#ifndef ATS_TS_PRECONDITIONS_H
#define ATS_TS_PRECONDITIONS_H

#include <string>
#include <jni.h>

/**
 * Throws `IllegalArgumentException` in the JVM if the given object is `nullptr`.
 *
 * @param ref The object to check.
 */
void req_nnp(JNIEnv *env, jobject& obj, std::string& objName);

/**
 * Throws `IllegalArgumentException` in the JVM if the given object is `nullptr`.
 *
 * @param ref The object to check.
 */
void req_nnp(JNIEnv *env, jobject& obj, const char* objName);

/**
 * Throws `IllegalArgumentException` in the JVM if the pointer represented with
 * the given `jlong` is `nullptr`.
 *
 * @param ref The pointer reference to check. This is cast to `void*` before checking.
 */
void req_nnp(JNIEnv *env, jlong ref, const std::string& refName = "ref");

/**
 * Throws `IllegalArgumentException` in the JVM if the given pointer is `nullptr`.
 * @param p The pointer to check.
 */
void req_nnp(JNIEnv *env, void *p, const std::string& name = "p"); // Require non-null pointer

#endif //ATS_TS_PRECONDITIONS_H
