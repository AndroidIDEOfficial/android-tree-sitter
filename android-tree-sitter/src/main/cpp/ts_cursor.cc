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

extern "C" JNIEXPORT jlong JNICALL Java_com_itsaky_androidide_treesitter_TSTreeCursor_00024Native_newCursor(
  JNIEnv* env, jclass self, jobject node) {
  TSTreeCursor* cursor =
    new TSTreeCursor(ts_tree_cursor_new(_unmarshalNode(env, node)));
  return (jlong)cursor;
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSTreeCursor_00024Native_currentTreeCursorNode(
  JNIEnv* env, jclass self, jlong cursor) {
  TSNode node = ts_tree_cursor_current_node((TSTreeCursor*)cursor);
  return _marshalTreeCursorNode(
           env,
  (TreeCursorNode) {
    ts_node_type(node),
                 ts_tree_cursor_current_field_name((TSTreeCursor*)cursor),
                 ts_node_start_byte(node) / 2, ts_node_end_byte(node) / 2
  });
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_itsaky_androidide_treesitter_TSTreeCursor_00024Native_currentFieldName(
  JNIEnv* env, jclass self, jlong cursor) {
  const char* name = ts_tree_cursor_current_field_name((TSTreeCursor*)cursor);
  jstring result = env->NewStringUTF(name);
  return result;
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSTreeCursor_00024Native_currentNode(JNIEnv* env,
    jclass self,
    jlong cursor) {
  return _marshalNode(env, ts_tree_cursor_current_node((TSTreeCursor*)cursor));
}

extern "C" JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TSTreeCursor_00024Native_delete(
  JNIEnv* env, jclass self, jlong cursor) {
  delete (TSTreeCursor*)cursor;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSTreeCursor_00024Native_gotoFirstChild(JNIEnv* env,
    jclass self,
    jlong cursor) {
  return (jboolean)ts_tree_cursor_goto_first_child((TSTreeCursor*)cursor);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSTreeCursor_00024Native_gotoNextSibling(JNIEnv* env,
    jclass self,
    jlong cursor) {
  return (jboolean)ts_tree_cursor_goto_next_sibling((TSTreeCursor*)cursor);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSTreeCursor_00024Native_gotoParent(JNIEnv* env,
    jclass self,
    jlong cursor) {
  return (jboolean)ts_tree_cursor_goto_parent((TSTreeCursor*)cursor);
}