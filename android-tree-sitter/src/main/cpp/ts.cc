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

#include <array>

#include "utils/jni_macros.h"
#include "utils/ts_obj_utils.h"
#include "utils/ts_log.h"
#include "ts_meta.h"
#include "ts_meta_sigs.h"

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


JNIEXPORT jint JNICALL ats_language_version(JNIEnv *env, jclass self) {
  return (jint) TREE_SITTER_LANGUAGE_VERSION;
}

JNIEXPORT jint JNICALL
ats_min_compatible_language_version(JNIEnv *env, jclass self) {
  return (jint) TREE_SITTER_MIN_COMPATIBLE_LANGUAGE_VERSION;
}

static JNINativeMethod gMethods[] = {
    MAKE_JNI_METHOD(TS_TREESITTER_GETLANGUAGEVERSION_NAME,
                    TS_TREESITTER_GETLANGUAGEVERSION_SIG,
                    ats_language_version),
    MAKE_JNI_METHOD(TS_TREESITTER_GETMINIMUMCOMPATIBLELANGUAGEVERSION_NAME,
                    TS_TREESITTER_GETMINIMUMCOMPATIBLELANGUAGEVERSION_SIG,
                    ats_min_compatible_language_version)
};

extern "C"
JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TreeSitter_00024Native_registerNatives(
    JNIEnv *env,
    jclass clazz) {

  auto result = env->RegisterNatives(clazz, gMethods, 2);
  LOGD("TreeSitter", "RegisterNatives: %d", result);
}