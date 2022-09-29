#include "com_itsaky_androidide_treesitter_TreeSitter.h"
#include "ts_utils.h"

JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_treeEdit(
  JNIEnv* env, jclass self, jlong tree, jobject inputEdit) {

  TSInputEdit edit = _unmarshalInputEdit(env, inputEdit);
  ts_tree_edit((TSTree*) tree, &edit);
}

JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_treeDelete(
  JNIEnv* env, jclass self, jlong tree) {
  ts_tree_delete((TSTree*)tree);
}

JNIEXPORT jlong JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_treeCopy(
  JNIEnv* env, jclass self, jlong tree) {
  return (jlong) ts_tree_copy((TSTree*)tree);
}

JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_treeRootNode(
  JNIEnv* env, jclass self, jlong tree) {
  return _marshalNode(env, ts_tree_root_node((TSTree*)tree));
}