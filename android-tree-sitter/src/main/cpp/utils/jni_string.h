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