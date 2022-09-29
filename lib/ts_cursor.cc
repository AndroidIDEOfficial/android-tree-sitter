#include "com_itsaky_androidide_treesitter_TreeSitter.h"
#include "ts_utils.h"

JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_nodeParent(
  JNIEnv* env, jclass self, jobject node) {
  return _marshalNode(env, ts_node_parent(_unmarshalNode(env, node)));
}

JNIEXPORT jlong JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_treeCursorNew(
  JNIEnv* env, jclass self, jobject node) {
  TSTreeCursor* cursor =
    new TSTreeCursor(ts_tree_cursor_new(_unmarshalNode(env, node)));
  return (jlong)cursor;
}

JNIEXPORT jstring JNICALL
Java_com_itsaky_androidide_treesitter_TreeSitter_treeCursorCurrentFieldName(
  JNIEnv* env, jclass self, jlong cursor) {
  const char* name = ts_tree_cursor_current_field_name((TSTreeCursor*)cursor);
  jstring result = env->NewStringUTF(name);
  return result;
}

JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TreeSitter_treeCursorCurrentNode(JNIEnv* env,
    jclass self,
    jlong cursor) {
  return _marshalNode(env, ts_tree_cursor_current_node((TSTreeCursor*)cursor));
}

JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TreeSitter_treeCursorCurrentTreeCursorNode(
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

JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_treeCursorDelete(
  JNIEnv* env, jclass self, jlong cursor) {
  delete (TSTreeCursor*)cursor;
}

JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TreeSitter_treeCursorGotoFirstChild(JNIEnv* env,
    jclass self,
    jlong cursor) {
  return (jboolean)ts_tree_cursor_goto_first_child((TSTreeCursor*)cursor);
}

JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TreeSitter_treeCursorGotoNextSibling(JNIEnv* env,
    jclass self,
    jlong cursor) {
  return (jboolean)ts_tree_cursor_goto_next_sibling((TSTreeCursor*)cursor);
}

JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TreeSitter_treeCursorGotoParent(JNIEnv* env,
    jclass self,
    jlong cursor) {
  return (jboolean)ts_tree_cursor_goto_parent((TSTreeCursor*)cursor);
}