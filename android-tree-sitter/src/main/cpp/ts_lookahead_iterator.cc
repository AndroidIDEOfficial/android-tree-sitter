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

extern "C"
JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_TSLookaheadIterator_00024Native_newIterator(
    JNIEnv *env,
    jclass clazz,
    jlong pointer,
    jshort state_id) {

  auto *iterator = ts_lookahead_iterator_new((TSLanguage *) pointer, state_id);
  return (jlong) iterator;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSLookaheadIterator_00024Native_delete(
    JNIEnv *env,
    jclass clazz,
    jlong pointer) {

  ts_lookahead_iterator_delete((TSLookaheadIterator *) pointer);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSLookaheadIterator_00024Native_next(
    JNIEnv *env,
    jclass clazz,
    jlong pointer) {

  return (jboolean) ts_lookahead_iterator_next((TSLookaheadIterator *) pointer);
}

extern "C"
JNIEXPORT jshort JNICALL
Java_com_itsaky_androidide_treesitter_TSLookaheadIterator_00024Native_currentSymbol(
    JNIEnv *env,
    jclass clazz,
    jlong pointer) {

  return (jshort) ts_lookahead_iterator_current_symbol((TSLookaheadIterator *) pointer);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_itsaky_androidide_treesitter_TSLookaheadIterator_00024Native_currentSymbolName(
    JNIEnv *env,
    jclass clazz,
    jlong pointer) {

  const char *name =
      ts_lookahead_iterator_current_symbol_name((TSLookaheadIterator *) pointer);
  return env->NewStringUTF(name);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSLookaheadIterator_00024Native_resetState(
    JNIEnv *env,
    jclass clazz,
    jlong pointer,
    jshort state_id) {
  return (jboolean) ts_lookahead_iterator_reset_state((TSLookaheadIterator *) pointer,
                                                      state_id);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSLookaheadIterator_00024Native_reset(
    JNIEnv *env,
    jclass clazz,
    jlong pointer,
    jlong language,
    jshort state_id) {

  return (jboolean) ts_lookahead_iterator_reset((TSLookaheadIterator *) pointer,
                                                (TSLanguage *) language,
                                                state_id);
}