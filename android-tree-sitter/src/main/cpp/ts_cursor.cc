#include "com_itsaky_androidide_treesitter_TSTreeCursor_Native.h"
#include "ts_utils.h"

JNIEXPORT jlong JNICALL Java_com_itsaky_androidide_treesitter_TSTreeCursor_00024Native_newCursor(
  JNIEnv* env, jclass self, jobject node) {
  TSTreeCursor* cursor =
    new TSTreeCursor(ts_tree_cursor_new(_unmarshalNode(env, node)));
  return (jlong)cursor;
}

JNIEXPORT jobject JNICALL
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


JNIEXPORT jstring JNICALL
Java_com_itsaky_androidide_treesitter_TSTreeCursor_00024Native_currentFieldName(
  JNIEnv* env, jclass self, jlong cursor) {
  const char* name = ts_tree_cursor_current_field_name((TSTreeCursor*)cursor);
  jstring result = env->NewStringUTF(name);
  return result;
}

JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSTreeCursor_00024Native_currentNode(JNIEnv* env,
    jclass self,
    jlong cursor) {
  return _marshalNode(env, ts_tree_cursor_current_node((TSTreeCursor*)cursor));
}

JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TSTreeCursor_00024Native_delete(
  JNIEnv* env, jclass self, jlong cursor) {
  delete (TSTreeCursor*)cursor;
}

JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSTreeCursor_00024Native_gotoFirstChild(JNIEnv* env,
    jclass self,
    jlong cursor) {
  return (jboolean)ts_tree_cursor_goto_first_child((TSTreeCursor*)cursor);
}

JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSTreeCursor_00024Native_gotoNextSibling(JNIEnv* env,
    jclass self,
    jlong cursor) {
  return (jboolean)ts_tree_cursor_goto_next_sibling((TSTreeCursor*)cursor);
}

JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSTreeCursor_00024Native_gotoParent(JNIEnv* env,
    jclass self,
    jlong cursor) {
  return (jboolean)ts_tree_cursor_goto_parent((TSTreeCursor*)cursor);
}