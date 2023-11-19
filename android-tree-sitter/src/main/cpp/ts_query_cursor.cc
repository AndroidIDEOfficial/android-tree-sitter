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

#include <iostream>

#include "utils/ts_obj_utils.h"
#include "utils/ts_preconditions.h"

#include "ts_query_cursor_sigs.h"

static jlong TSQueryCursor_newCursor(JNIEnv *env, jclass self) {
  return (jlong) ts_query_cursor_new();
}

static void TSQueryCursor_delete(JNIEnv *env, jclass self, jlong cursor) {
  req_nnp(env, cursor);
  ts_query_cursor_delete((TSQueryCursor *) cursor);
}

static void TSQueryCursor_exec(JNIEnv *env,
                               jclass self,
                               jlong cursor,
                               jlong query,
                               jobject node) {
  req_nnp(env, cursor);
  ts_query_cursor_exec((TSQueryCursor *) cursor,
                       (TSQuery *) query,
                       _unmarshalNode(env, node));
}

static jboolean
TSQueryCursor_exceededMatchLimit(JNIEnv *env, jclass self, jlong cursor) {
  req_nnp(env, cursor);
  return (jboolean) ts_query_cursor_did_exceed_match_limit((TSQueryCursor *) cursor);
}

static void TSQueryCursor_setMatchLimit(JNIEnv *env,
                                        jclass self,
                                        jlong cursor,
                                        jint newLimit) {
  req_nnp(env, cursor);
  ts_query_cursor_set_match_limit((TSQueryCursor *) cursor, newLimit);
}

static jint
TSQueryCursor_getMatchLimit(JNIEnv *env, jclass self, jlong cursor) {
  req_nnp(env, cursor);
  return (jint) ts_query_cursor_match_limit((TSQueryCursor *) cursor);
}

static void TSQueryCursor_setByteRange(JNIEnv *env,
                                       jclass self,
                                       jlong cursor,
                                       jint start,
                                       jint end) {
  req_nnp(env, cursor);
  ts_query_cursor_set_byte_range((TSQueryCursor *) cursor, start, end);
}

static void TSQueryCursor_setPointRange(JNIEnv *env,
                                        jclass self,
                                        jlong cursor,
                                        jobject start,
                                        jobject end) {
  req_nnp(env, cursor);
  ts_query_cursor_set_point_range((TSQueryCursor *) cursor,
                                  _unmarshalPoint(env, start),
                                  _unmarshalPoint(env, end));
}

static jobject TSQueryCursor_nextMatch(JNIEnv *env, jclass self, jlong cursor) {
  req_nnp(env, cursor);
  TSQueryMatch m;
  bool b = ts_query_cursor_next_match((TSQueryCursor *) cursor, &m);
  if (!b) {
    return nullptr;
  }
  return _marshalMatch(env, m);
}

static void
TSQueryCursor_removeMatch(JNIEnv *env, jclass self, jlong cursor, jint id) {
  req_nnp(env, cursor);
  ts_query_cursor_remove_match((TSQueryCursor *) cursor, id);
}

extern "C" JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSQueryCursor_00024Native_registerNatives(
    JNIEnv *env,
    jclass clazz) {

  SET_JNI_METHOD(TSQueryCursor_Native_newCursor, TSQueryCursor_newCursor);
  SET_JNI_METHOD(TSQueryCursor_Native_delete, TSQueryCursor_delete);
  SET_JNI_METHOD(TSQueryCursor_Native_exec, TSQueryCursor_exec);
  SET_JNI_METHOD(TSQueryCursor_Native_exceededMatchLimit,
                 TSQueryCursor_exceededMatchLimit);
  SET_JNI_METHOD(TSQueryCursor_Native_setMatchLimit,
                 TSQueryCursor_setMatchLimit);
  SET_JNI_METHOD(TSQueryCursor_Native_getMatchLimit,
                 TSQueryCursor_getMatchLimit);
  SET_JNI_METHOD(TSQueryCursor_Native_setByteRange, TSQueryCursor_setByteRange);
  SET_JNI_METHOD(TSQueryCursor_Native_setPointRange,
                 TSQueryCursor_setPointRange);
  SET_JNI_METHOD(TSQueryCursor_Native_nextMatch, TSQueryCursor_nextMatch);
  SET_JNI_METHOD(TSQueryCursor_Native_removeMatch, TSQueryCursor_removeMatch);

  TSQueryCursor_Native__RegisterNatives(env, clazz);
}