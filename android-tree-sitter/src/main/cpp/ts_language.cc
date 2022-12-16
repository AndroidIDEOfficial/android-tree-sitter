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

#include "utils/ts_obj_utils.h"

extern "C" JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSLanguage_00024Native_symCount
  (JNIEnv * env, jclass self, jlong ptr) {
    return (jint) ts_language_symbol_count((TSLanguage*) ptr);
  }

extern "C" JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSLanguage_00024Native_fldCount
  (JNIEnv * env, jclass self, jlong ptr) {
    return (jint) ts_language_field_count((TSLanguage*) ptr);
  }


extern "C" JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSLanguage_00024Native_symForName
  (JNIEnv * env, jclass self, jlong ptr, jbyteArray name, jint length, jboolean isNamed) {
    jbyte* nm = env->GetByteArrayElements(name, NULL);
    uint32_t count = ts_language_symbol_for_name((TSLanguage*) ptr, reinterpret_cast<const char*>(nm), length, isNamed);
    env->ReleaseByteArrayElements(name, nm, JNI_ABORT);
    return (jint) count;
  }


extern "C" JNIEXPORT jstring JNICALL Java_com_itsaky_androidide_treesitter_TSLanguage_00024Native_symName
  (JNIEnv * env, jclass self, jlong lngPtr, jint sym) {
    return env->NewStringUTF(ts_language_symbol_name((TSLanguage*) lngPtr, sym));
  }


extern "C" JNIEXPORT jstring JNICALL Java_com_itsaky_androidide_treesitter_TSLanguage_00024Native_fldNameForId
  (JNIEnv * env, jclass self, jlong ptr, jint id) {
    return env->NewStringUTF(ts_language_field_name_for_id((TSLanguage*) ptr, id));
  }


extern "C" JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSLanguage_00024Native_fldIdForName
  (JNIEnv * env, jclass self, jlong ptr, jbyteArray name, jint length) {
    jbyte* nm = env->GetByteArrayElements(name, NULL);
    uint32_t id = ts_language_field_id_for_name((TSLanguage*) ptr, reinterpret_cast<const char*>(nm), length);
    env->ReleaseByteArrayElements(name, nm, JNI_ABORT);
    return (jint) id;
  }


extern "C" JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSLanguage_00024Native_symType
  (JNIEnv * env, jclass self, jlong ptr, jint sym) {
    return (jint) ts_language_symbol_type((TSLanguage*) ptr, sym);
  }


extern "C" JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSLanguage_00024Native_langVer
  (JNIEnv * env, jclass self, jlong ptr) {
    return (jint) ts_language_version((TSLanguage*) ptr);
  }