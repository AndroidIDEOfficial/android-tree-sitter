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

#ifndef ANDROIDTREESITTER_JNI_STRING_H
#define ANDROIDTREESITTER_JNI_STRING_H

#include <jni.h>
#include <assert.h>
#include <stdlib.h>
#include <string>
#include <cstring>

jsize FNI_GetStringLength(JNIEnv *env, jstring string);

const jchar *FNI_GetStringChars(JNIEnv *env, jstring string, int32_t *length);

void FNI_ReleaseStringChars(const jchar *chars);

jstring FNI_NewString(JNIEnv *env, const jbyte *bytes, int len);

#endif //ANDROIDTREESITTER_JNI_STRING_H