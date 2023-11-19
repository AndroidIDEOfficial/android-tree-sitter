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
#include "utils/ts_preconditions.h"

#include "ts_language_sigs.h"

typedef const TSLanguage *(*TsLangFunc)();

static jint TSLanguage_symCount(JNIEnv *env, jclass self, jlong ptr) {
  req_nnp(env, ptr);
  return (jint) ts_language_symbol_count((TSLanguage *) ptr);
}

static jint TSLanguage_fldCount(JNIEnv *env, jclass self, jlong ptr) {
  req_nnp(env, ptr);
  return (jint) ts_language_field_count((TSLanguage *) ptr);
}


static jint TSLanguage_symForName(JNIEnv *env,
                                  jclass self,
                                  jlong ptr,
                                  jbyteArray name,
                                  jint length,
                                  jboolean isNamed) {
  req_nnp(env, ptr);
  jbyte *nm = env->GetByteArrayElements(name, NULL);
  uint32_t count = ts_language_symbol_for_name((TSLanguage *) ptr,
                                               reinterpret_cast<const char *>(nm),
                                               length,
                                               isNamed);
  env->ReleaseByteArrayElements(name, nm, JNI_ABORT);
  return (jint) count;
}


static jstring
TSLanguage_symName(JNIEnv *env, jclass self, jlong lngPtr, jint sym) {
  req_nnp(env, lngPtr);
  return env->NewStringUTF(ts_language_symbol_name((TSLanguage *) lngPtr, sym));
}


static jstring
TSLanguage_fldNameForId(JNIEnv *env, jclass self, jlong ptr, jint id) {
  req_nnp(env, ptr);
  return env->NewStringUTF(ts_language_field_name_for_id((TSLanguage *) ptr,
                                                         id));
}


static jint TSLanguage_fldIdForName(JNIEnv *env,
                                    jclass self,
                                    jlong ptr,
                                    jbyteArray name,
                                    jint length) {
  jbyte *nm = env->GetByteArrayElements(name, nullptr);
  req_nnp(env, ptr);
  uint32_t id = ts_language_field_id_for_name((TSLanguage *) ptr,
                                              reinterpret_cast<const char *>(nm),
                                              length);
  env->ReleaseByteArrayElements(name, nm, JNI_ABORT);
  return (jint) id;
}


static jint TSLanguage_symType(JNIEnv *env, jclass self, jlong ptr, jint sym) {
  req_nnp(env, ptr);
  return (jint) ts_language_symbol_type((TSLanguage *) ptr, sym);
}


static jint TSLanguage_langVer(JNIEnv *env, jclass self, jlong ptr) {
  req_nnp(env, ptr);
  return (jint) ts_language_version((TSLanguage *) ptr);
}

static jlongArray TSLanguage_loadLanguage(JNIEnv *env,
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
    LOGE(LOG_TAG, "Cannot reinterpreset_cast to TsLangFunc");
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

static void TSLanguage_dlclose(JNIEnv *env, jclass clazz, jlong libhandle) {
  if (libhandle == 0) return;
  dlclose((void *) libhandle);
}

static jint TSLanguage_stateCount(JNIEnv *env, jclass clazz, jlong pointer) {
  req_nnp(env, pointer);
  return (jint) ts_language_state_count((TSLanguage *) pointer);
}

static jshort TSLanguage_nextState(JNIEnv *env,
                                   jclass clazz,
                                   jlong pointer,
                                   jshort state_id,
                                   jshort symbol) {
  req_nnp(env, pointer);
  return (jshort) ts_language_next_state((TSLanguage *) pointer,
                                         state_id,
                                         symbol);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSLanguage_00024Native_registerNatives(
    JNIEnv *env,
    jclass clazz) {

  SET_JNI_METHOD(TSLanguage_Native_symCount, TSLanguage_symCount);
  SET_JNI_METHOD(TSLanguage_Native_fldCount, TSLanguage_fldCount);
  SET_JNI_METHOD(TSLanguage_Native_symForName, TSLanguage_symForName);
  SET_JNI_METHOD(TSLanguage_Native_symName, TSLanguage_symName);
  SET_JNI_METHOD(TSLanguage_Native_fldNameForId, TSLanguage_fldNameForId);
  SET_JNI_METHOD(TSLanguage_Native_fldIdForName, TSLanguage_fldIdForName);
  SET_JNI_METHOD(TSLanguage_Native_symType, TSLanguage_symType);
  SET_JNI_METHOD(TSLanguage_Native_langVer, TSLanguage_langVer);
  SET_JNI_METHOD(TSLanguage_Native_loadLanguage, TSLanguage_loadLanguage);
  SET_JNI_METHOD(TSLanguage_Native_dlclose, TSLanguage_dlclose);
  SET_JNI_METHOD(TSLanguage_Native_stateCount, TSLanguage_stateCount);
  SET_JNI_METHOD(TSLanguage_Native_nextState, TSLanguage_nextState);

  TSLanguage_Native__RegisterNatives(env, clazz);
}