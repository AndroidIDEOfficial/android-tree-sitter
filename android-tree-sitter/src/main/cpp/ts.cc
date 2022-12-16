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

#include <string.h>
#include "utils/ts_obj_utils.h"

#if defined(__ANDROID__)
static jint JNI_VERSION = JNI_VERSION_1_6;
#else
static jint JNI_VERSION = JNI_VERSION_10;
#endif

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
  JNIEnv* env;
  if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION) != JNI_OK) {
    return JNI_ERR;
  }

  onLoad(env);

  return JNI_VERSION;
}

void JNI_OnUnload(JavaVM* vm, void* reserved) {
  JNIEnv* env;
  vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION);
  onUnload(env);
}


extern "C" JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_getLanguageVersion
  (JNIEnv *env, jclass self) {
    return (jint) TREE_SITTER_LANGUAGE_VERSION;
  }

extern "C" JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_getMinimumCompatibleLanguageVersion
  (JNIEnv *env, jclass self) {
    return (jint) TREE_SITTER_MIN_COMPATIBLE_LANGUAGE_VERSION;
  }