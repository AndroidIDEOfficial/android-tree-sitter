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

#include <cstring>
#include <jni.h>

#include "tree_sitter/api.h"
#include "utils/jni_string.h"
#include "utils/ts_preconditions.h"

#include "ts_lookahead_iterator.h"

static jlong TSLookaheadIterator_newIterator(JNIEnv *env,
                                             jclass clazz,
                                             jlong pointer,
                                             jshort state_id) {

  auto *iterator = ts_lookahead_iterator_new((TSLanguage *) pointer, state_id);
  return (jlong) iterator;
}

static void
TSLookaheadIterator_delete(JNIEnv *env, jclass clazz, jlong pointer) {
  req_nnp(env, pointer);
  ts_lookahead_iterator_delete((TSLookaheadIterator *) pointer);
}

static jboolean
TSLookaheadIterator_next(JNIEnv *env, jclass clazz, jlong pointer) {
  req_nnp(env, pointer);
  return (jboolean) ts_lookahead_iterator_next((TSLookaheadIterator *) pointer);
}

static jshort
TSLookaheadIterator_currentSymbol(JNIEnv *env, jclass clazz, jlong pointer) {
  req_nnp(env, pointer);
  return (jshort) ts_lookahead_iterator_current_symbol((TSLookaheadIterator *) pointer);
}

static jstring TSLookaheadIterator_currentSymbolName(JNIEnv *env,
                                                     jclass clazz,
                                                     jlong pointer) {
  req_nnp(env, pointer);
  const char *name =
      ts_lookahead_iterator_current_symbol_name((TSLookaheadIterator *) pointer);
  return env->NewStringUTF(name);
}

static jboolean TSLookaheadIterator_resetState(JNIEnv *env,
                                               jclass clazz,
                                               jlong pointer,
                                               jshort state_id) {
  req_nnp(env, pointer);
  return (jboolean) ts_lookahead_iterator_reset_state((TSLookaheadIterator *) pointer,
                                                      state_id);
}

static jboolean TSLookaheadIterator_reset(JNIEnv *env,
                                          jclass clazz,
                                          jlong pointer,
                                          jlong language,
                                          jshort state_id) {
  req_nnp(env, pointer);
  req_nnp(env, language);
  return (jboolean) ts_lookahead_iterator_reset((TSLookaheadIterator *) pointer,
                                                (TSLanguage *) language,
                                                state_id);
}

static jlong
TSLookaheadIterator_language(JNIEnv *env, jclass clazz, jlong pointer) {
  req_nnp(env, pointer);
  return (jlong) ts_lookahead_iterator_language((TSLookaheadIterator *) pointer);
}

void TSLookaheadIterator_Native__SetJniMethods(JNINativeMethod *methods, int count) {
  SET_JNI_METHOD(methods, TSLookaheadIterator_Native_newIterator,
                 TSLookaheadIterator_newIterator);
  SET_JNI_METHOD(methods, TSLookaheadIterator_Native_delete, TSLookaheadIterator_delete);
  SET_JNI_METHOD(methods, TSLookaheadIterator_Native_next, TSLookaheadIterator_next);
  SET_JNI_METHOD(methods, TSLookaheadIterator_Native_currentSymbol,
                 TSLookaheadIterator_currentSymbol);
  SET_JNI_METHOD(methods, TSLookaheadIterator_Native_currentSymbolName,
                 TSLookaheadIterator_currentSymbolName);
  SET_JNI_METHOD(methods, TSLookaheadIterator_Native_resetState,
                 TSLookaheadIterator_resetState);
  SET_JNI_METHOD(methods, TSLookaheadIterator_Native_reset, TSLookaheadIterator_reset);
  SET_JNI_METHOD(methods, TSLookaheadIterator_Native_language,
                 TSLookaheadIterator_language);
}