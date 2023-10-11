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

extern "C" JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSTree_00024Native_edit(
    JNIEnv *env, jclass self, jlong tree, jobject inputEdit) {
  req_nnp(env, tree);
  req_nnp(env, inputEdit);
  TSInputEdit edit = _unmarshalInputEdit(env, inputEdit);
  ts_tree_edit((TSTree *) tree, &edit);
}

extern "C" JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSTree_00024Native_delete(JNIEnv *env,
                                                                jclass self,
                                                                jlong tree) {
  req_nnp(env, tree);
  ts_tree_delete((TSTree *) tree);
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_TSTree_00024Native_copy(JNIEnv *env,
                                                              jclass self,
                                                              jlong tree) {
  req_nnp(env, tree);
  return (jlong) ts_tree_copy((TSTree *) tree);
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSTree_00024Native_rootNode(JNIEnv *env,
                                                                  jclass self,
                                                                  jlong tree) {
  req_nnp(env, tree);
  return _marshalNode(env, ts_tree_root_node((TSTree *) tree));
}

extern "C" JNIEXPORT jobjectArray JNICALL
Java_com_itsaky_androidide_treesitter_TSTree_00024Native_changedRanges
    (JNIEnv *env, jclass self, jlong tree, jlong oldTree) {
  req_nnp(env, tree, "thisTree");
  req_nnp(env, oldTree, "oldTree");
  uint32_t count;
  TSRange *ranges =
      ts_tree_get_changed_ranges((TSTree *) oldTree, (TSTree *) tree, &count);
  if (count == 0) {
    return nullptr;
  }

  jobjectArray arr = createRangeArr(env, (jint) count);
  req_nnp(env, arr, "TSRange[] from factory");

  for (uint32_t i = 0; i < count; i++) {
    TSRange *r = (ranges + i);
    env->SetObjectArrayElement(arr, (jint) i, _marshalRange(env, *r));
  }
  return arr;
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_TSTree_00024Native_getLanguage(
    JNIEnv *env, jclass self, jlong tree) {
  req_nnp(env, tree);
  return (jlong) ts_tree_language((TSTree *) tree);
}