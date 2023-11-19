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
#include "utils/ts_preconditions.h"

#include "ts_tree_cursor_sigs.h"

static jlong TreeCursor_newCursor(JNIEnv *env, jclass self, jobject node) {
  auto
      *cursor = new TSTreeCursor(ts_tree_cursor_new(_unmarshalNode(env, node)));
  return (jlong) cursor;
}

static jobject TreeCursor_currentTreeCursorNode(JNIEnv *env, jclass self, jlong cursor) {
  req_nnp(env, cursor);
  TSNode node = ts_tree_cursor_current_node((TSTreeCursor *) cursor);
  return _marshalTreeCursorNode(env,
                                (TreeCursorNode) {ts_node_type(node),
                                                  ts_tree_cursor_current_field_name(
                                                      (TSTreeCursor *) cursor),
                                                  ts_node_start_byte(node) / 2,
                                                  ts_node_end_byte(node) / 2});
}


static jstring TreeCursor_currentFieldName(JNIEnv *env, jclass self, jlong cursor) {
  req_nnp(env, cursor);
  const char *name = ts_tree_cursor_current_field_name((TSTreeCursor *) cursor);
  jstring result = env->NewStringUTF(name);
  return result;
}

static jobject TreeCursor_currentNode(JNIEnv *env, jclass self, jlong cursor) {
  req_nnp(env, cursor);
  return _marshalNode(env,
                      ts_tree_cursor_current_node((TSTreeCursor *) cursor));
}

static void TreeCursor_delete(JNIEnv *env, jclass self, jlong cursor) {
  req_nnp(env, cursor);
  ts_tree_cursor_delete((TSTreeCursor *) cursor);
  delete (TSTreeCursor *) cursor;
}

static jboolean TreeCursor_gotoFirstChild(JNIEnv *env, jclass self, jlong cursor) {
  req_nnp(env, cursor);
  return (jboolean) ts_tree_cursor_goto_first_child((TSTreeCursor *) cursor);
}

static jboolean TreeCursor_gotoNextSibling(JNIEnv *env, jclass self, jlong cursor) {
  req_nnp(env, cursor);
  return (jboolean) ts_tree_cursor_goto_next_sibling((TSTreeCursor *) cursor);
}

static jboolean TreeCursor_gotoParent(JNIEnv *env, jclass self, jlong cursor) {
  req_nnp(env, cursor);
  return (jboolean) ts_tree_cursor_goto_parent((TSTreeCursor *) cursor);
}


static jshort TreeCursor_currentFieldId(JNIEnv *env, jclass clazz, jlong cursor) {
  req_nnp(env, cursor);
  return (jshort) ts_tree_cursor_current_field_id((TSTreeCursor *) cursor);
}


static jlong TreeCursor_gotoFirstChildForByte(JNIEnv *env,
                                   jclass clazz,
                                   jlong cursor,
                                   jint byte_index) {
  req_nnp(env, cursor);
  return (jlong) ts_tree_cursor_goto_first_child_for_byte((TSTreeCursor *) cursor,
                                                          byte_index);
}


static jboolean TreeCursor_gotoFirstChildForPoint(JNIEnv *env,
                                       jclass clazz,
                                       jlong pointer,
                                       jobject point) {
  req_nnp(env, pointer);
  return (jboolean) ts_tree_cursor_goto_first_child_for_point((TSTreeCursor *) pointer,
                                                              _unmarshalPoint(
                                                                  env,
                                                                  point));
}


static jboolean TreeCursor_gotoLastChild(JNIEnv *env, jclass clazz, jlong pointer) {
  req_nnp(env, pointer);
  return (jboolean) ts_tree_cursor_goto_last_child((TSTreeCursor *) pointer);
}


static jboolean TreeCursor_gotoPreviousSibling(JNIEnv *env, jclass clazz, jlong pointer) {
  req_nnp(env, pointer);
  return (jboolean) ts_tree_cursor_goto_previous_sibling((TSTreeCursor *) pointer);
}


static void TreeCursor_gotoDescendant(JNIEnv *env,
                           jclass clazz,
                           jlong pointer,
                           jint descendant_index) {
  req_nnp(env, pointer);
  ts_tree_cursor_goto_descendant((TSTreeCursor *) pointer, descendant_index);
}


static jint TreeCursor_currentDescendantIndex(JNIEnv *env, jclass clazz, jlong pointer) {
  req_nnp(env, pointer);
  return (jint) ts_tree_cursor_current_descendant_index((TSTreeCursor *) pointer);
}


static jint TreeCursor_depth(JNIEnv *env, jclass clazz, jlong pointer) {
  req_nnp(env, pointer);
  return (jint) ts_tree_cursor_current_depth((TSTreeCursor *) pointer);
}


static void TreeCursor_reset(JNIEnv *env, jclass clazz, jlong pointer, jobject node) {
  req_nnp(env, pointer);
  ts_tree_cursor_reset((TSTreeCursor *) pointer, _unmarshalNode(env, node));
}


static void TreeCursor_resetTo(JNIEnv *env, jclass clazz, jlong pointer, jlong another) {
  req_nnp(env, pointer, "src");
  req_nnp(env, another, "another");
  ts_tree_cursor_reset_to((TSTreeCursor *) pointer, (TSTreeCursor *) another);
}


static jlong TreeCursor_copy(JNIEnv *env, jclass clazz, jlong pointer) {
  req_nnp(env, pointer);
  auto
      *copied = new TSTreeCursor(ts_tree_cursor_copy((TSTreeCursor *) pointer));
  return (jlong) copied;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSTreeCursor_00024Native_registerNatives(
    JNIEnv *env,
    jclass clazz) {

  SET_JNI_METHOD(TSTreeCursor_Native_copy, TreeCursor_copy);
  SET_JNI_METHOD(TSTreeCursor_Native_currentDescendantIndex, TreeCursor_currentDescendantIndex);
  SET_JNI_METHOD(TSTreeCursor_Native_currentFieldId, TreeCursor_currentFieldId);
  SET_JNI_METHOD(TSTreeCursor_Native_currentFieldName, TreeCursor_currentFieldName);
  SET_JNI_METHOD(TSTreeCursor_Native_currentNode, TreeCursor_currentNode);
  SET_JNI_METHOD(TSTreeCursor_Native_currentTreeCursorNode, TreeCursor_currentTreeCursorNode);
  SET_JNI_METHOD(TSTreeCursor_Native_delete, TreeCursor_delete);
  SET_JNI_METHOD(TSTreeCursor_Native_depth, TreeCursor_depth);
  SET_JNI_METHOD(TSTreeCursor_Native_gotoDescendant, TreeCursor_gotoDescendant);
  SET_JNI_METHOD(TSTreeCursor_Native_gotoFirstChild, TreeCursor_gotoFirstChild);
  SET_JNI_METHOD(TSTreeCursor_Native_gotoFirstChildForByte, TreeCursor_gotoFirstChildForByte);
  SET_JNI_METHOD(TSTreeCursor_Native_gotoFirstChildForPoint, TreeCursor_gotoFirstChildForPoint);
  SET_JNI_METHOD(TSTreeCursor_Native_gotoLastChild, TreeCursor_gotoLastChild);
  SET_JNI_METHOD(TSTreeCursor_Native_gotoNextSibling, TreeCursor_gotoNextSibling);
  SET_JNI_METHOD(TSTreeCursor_Native_gotoParent, TreeCursor_gotoParent);
  SET_JNI_METHOD(TSTreeCursor_Native_gotoPreviousSibling, TreeCursor_gotoPreviousSibling);
  SET_JNI_METHOD(TSTreeCursor_Native_newCursor, TreeCursor_newCursor);
  SET_JNI_METHOD(TSTreeCursor_Native_reset, TreeCursor_reset);
  SET_JNI_METHOD(TSTreeCursor_Native_resetTo, TreeCursor_resetTo);

  TSTreeCursor_Native__RegisterNatives(env, clazz);
}