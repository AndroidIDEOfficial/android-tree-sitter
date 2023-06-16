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

#include <dlfcn.h>

#include "tree_sitter/api.h"
#include "utils/ts_obj_utils.h"
#include "utils/ts_log.h"

typedef const TSLanguage *(*TsLangFunc)();

extern "C" JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_TSLanguage_00024Native_symCount
    (JNIEnv *env, jclass self, jlong ptr) {
  return (jint) ts_language_symbol_count((TSLanguage *) ptr);
}

extern "C" JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_TSLanguage_00024Native_fldCount
    (JNIEnv *env, jclass self, jlong ptr) {
  return (jint) ts_language_field_count((TSLanguage *) ptr);
}


extern "C" JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_TSLanguage_00024Native_symForName
    (JNIEnv *env,
     jclass self,
     jlong ptr,
     jbyteArray name,
     jint length,
     jboolean isNamed) {
  jbyte *nm = env->GetByteArrayElements(name, NULL);
  uint32_t count = ts_language_symbol_for_name((TSLanguage *) ptr,
                                               reinterpret_cast<const char *>(nm),
                                               length,
                                               isNamed);
  env->ReleaseByteArrayElements(name, nm, JNI_ABORT);
  return (jint) count;
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_itsaky_androidide_treesitter_TSLanguage_00024Native_symName
    (JNIEnv *env, jclass self, jlong lngPtr, jint sym) {
  return env->NewStringUTF(ts_language_symbol_name((TSLanguage *) lngPtr, sym));
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_itsaky_androidide_treesitter_TSLanguage_00024Native_fldNameForId
    (JNIEnv *env, jclass self, jlong ptr, jint id) {
  return env->NewStringUTF(ts_language_field_name_for_id((TSLanguage *) ptr,
                                                         id));
}


extern "C" JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_TSLanguage_00024Native_fldIdForName
    (JNIEnv *env, jclass self, jlong ptr, jbyteArray name, jint length) {
  jbyte *nm = env->GetByteArrayElements(name, NULL);
  uint32_t id = ts_language_field_id_for_name((TSLanguage *) ptr,
                                              reinterpret_cast<const char *>(nm),
                                              length);
  env->ReleaseByteArrayElements(name, nm, JNI_ABORT);
  return (jint) id;
}


extern "C" JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_TSLanguage_00024Native_symType
    (JNIEnv *env, jclass self, jlong ptr, jint sym) {
  return (jint) ts_language_symbol_type((TSLanguage *) ptr, sym);
}


extern "C" JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_TSLanguage_00024Native_langVer
    (JNIEnv *env, jclass self, jlong ptr) {
  return (jint) ts_language_version((TSLanguage *) ptr);
}

extern "C"
JNIEXPORT jlongArray JNICALL
Java_com_itsaky_androidide_treesitter_TSLanguage_00024Native_loadLanguage(JNIEnv *env,
                                                                          jclass clazz,
                                                                          jstring libpath,
                                                                          jstring func) {
  auto lib_path = env->GetStringUTFChars(libpath, nullptr);
  auto func_name = env->GetStringUTFChars(func, nullptr);

  auto handle = dlopen(lib_path, RTLD_LAZY);
  if (handle == nullptr) {
    LOGE(LOG_TAG, "Failed to dlopen library '%s': %s", lib_path, dlerror());
    env->ReleaseStringUTFChars(libpath, lib_path);
    env->ReleaseStringUTFChars(func, func_name);
    return nullptr;
  }

  void *func_addr = dlsym(handle, func_name);
  if (func_addr == nullptr) {
    LOGE(LOG_TAG,
         "Cannot find function '%s' to create language instance: %s",
         func_name,
         dlerror());
    env->ReleaseStringUTFChars(libpath, lib_path);
    env->ReleaseStringUTFChars(func, func_name);
    return nullptr;
  }

  auto lang_func = reinterpret_cast<TsLangFunc>(func_addr);
  if (lang_func == nullptr) {
    LOGE(LOG_TAG, "Cannot reinterpreset_cat to TsLangFunc");
    env->ReleaseStringUTFChars(libpath, lib_path);
    env->ReleaseStringUTFChars(func, func_name);
    return nullptr;
  }

  auto language = lang_func();
  if (language == nullptr) {
    LOGE(LOG_TAG, "Function '%s' returned nullptr", func_name);
    return nullptr;
  }

  LOGD(LOG_TAG, "Loaded tree sitter language with function '%s'", func_name);

  env->ReleaseStringUTFChars(libpath, lib_path);
  env->ReleaseStringUTFChars(func, func_name);

  jlong ptrs[2] = {(jlong) language, (jlong) handle};

  auto result = env->NewLongArray(2);
  env->SetLongArrayRegion(result, 0, 2, ptrs);
  return result;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSLanguage_00024Native_dlclose(JNIEnv *env,
                                                                     jclass clazz,
                                                                     jlong libhandle) {
  if (libhandle == 0) return;
  dlclose((void *) libhandle);
}