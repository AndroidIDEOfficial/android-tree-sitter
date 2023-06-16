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

#include "utils/ts_obj_utils.h"

#if defined(__ANDROID__)
static jint JNI_VERSION = JNI_VERSION_1_6;
#else
static jint JNI_VERSION = JNI_VERSION_10;
#endif

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
  JNIEnv *env;
  if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION) != JNI_OK) {
    return JNI_ERR;
  }

  onLoad(env);

  return JNI_VERSION;
}

void JNI_OnUnload(JavaVM *vm, void *reserved) {
  JNIEnv *env;
  vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION);
  onUnload(env);
}


extern "C" JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_TreeSitter_getLanguageVersion
    (JNIEnv *env, jclass self) {
  return (jint) TREE_SITTER_LANGUAGE_VERSION;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_TreeSitter_getMinimumCompatibleLanguageVersion
    (JNIEnv *env, jclass self) {
  return (jint) TREE_SITTER_MIN_COMPATIBLE_LANGUAGE_VERSION;
}