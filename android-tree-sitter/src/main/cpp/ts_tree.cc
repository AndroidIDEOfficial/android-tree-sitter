#include "ts_utils.h"

extern "C" JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSTree_00024Native_edit(
    JNIEnv* env, jclass self, jlong tree, jobject inputEdit) {
  TSInputEdit edit = _unmarshalInputEdit(env, inputEdit);
  ts_tree_edit((TSTree*)tree, &edit);
}

extern "C" JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSTree_00024Native_delete(JNIEnv* env,
                                                                jclass self,
                                                                jlong tree) {
  ts_tree_delete((TSTree*)tree);
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_TSTree_00024Native_copy(JNIEnv* env,
                                                              jclass self,
                                                              jlong tree) {
  return (jlong)ts_tree_copy((TSTree*)tree);
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSTree_00024Native_rootNode(JNIEnv* env,
                                                                  jclass self,
                                                                  jlong tree) {
  return _marshalNode(env, ts_tree_root_node((TSTree*)tree));
}

extern "C" JNIEXPORT jobjectArray JNICALL Java_com_itsaky_androidide_treesitter_TSTree_00024Native_changedRanges
  (JNIEnv *env, jclass self, jlong tree, jlong oldTree) {
    uint32_t count;
    TSRange *ranges = ts_tree_get_changed_ranges((TSTree*) oldTree, (TSTree*) tree, &count);
    if (count == 0) {
      return NULL;
    }

    jclass klass = env->FindClass("com/itsaky/androidide/treesitter/TSRange");
    jobjectArray arr = env->NewObjectArray(count, klass, NULL);
    for (uint32_t i = 0; i < count; i++) {
      TSRange *r = (ranges + i);
      env->SetObjectArrayElement(arr, i, _marshalRange(env, *r));
    }
    return arr;
  }

extern "C" JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_TSTree_00024Native_getLanguage(
    JNIEnv* env, jclass self, jlong tree) {
  return (jlong)ts_tree_language((TSTree*)tree);
}