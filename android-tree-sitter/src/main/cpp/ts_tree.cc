/*
 *  This file is part of android-tree-sitter.
 *
 *  android-tree-sitter library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  android-tree-sitter library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *  along with android-tree-sitter.  If not, see
 * <https://www.gnu.org/licenses/>.
 */

#include "ts_tree.h"

#include "utils/ts_misc.h"
#include "utils/ts_obj_utils.h"
#include "utils/ts_preconditions.h"

static void TSTree_edit(JNIEnv *env, __TS_ATTR_UNUSED jclass self, jlong tree,
                        jobject inputEdit) {
  req_nnp(env, tree);
  req_nnp(env, inputEdit);
  TSInputEdit edit = _unmarshalInputEdit(env, inputEdit);
  ts_tree_edit((TSTree *)tree, &edit);
}

static void TSTree_delete(JNIEnv *env, __TS_ATTR_UNUSED jclass self, jlong tree) {
  req_nnp(env, tree);
  ts_tree_delete((TSTree *)tree);
}

static jlong TSTree_copy(JNIEnv *env, __TS_ATTR_UNUSED jclass self, jlong tree) {
  req_nnp(env, tree);
  return (jlong)ts_tree_copy((TSTree *)tree);
}

static jobject TSTree_rootNode(JNIEnv *env, __TS_ATTR_UNUSED jclass self, jlong tree) {
  req_nnp(env, tree);
  return _marshalNode(env, ts_tree_root_node((TSTree *)tree));
}

static jobject TSTree_rootNodeWithOffset(JNIEnv *env,
                                         __TS_ATTR_UNUSED jclass self,
                                         jlong tree, jint offset_bytes,
                                         jobject offset_extent) {
  req_nnp(env, tree);
  return _marshalNode(
      env, ts_tree_root_node_with_offset((TSTree *)tree, offset_bytes,
                                         _unmarshalPoint(env, offset_extent)));
}

static jobjectArray TSTree_changedRanges(JNIEnv *env,
                                         __TS_ATTR_UNUSED jclass self,
                                         jlong tree, jlong oldTree) {
  req_nnp(env, tree, "thisTree");
  req_nnp(env, oldTree, "oldTree");
  uint32_t count;
  TSRange *ranges =
      ts_tree_get_changed_ranges((TSTree *)oldTree, (TSTree *)tree, &count);
  if (count == 0) {
    if (ranges != nullptr) {
      free(ranges);
    }
    return nullptr;
  }

  jobjectArray arr = createRangeArr(env, (jint)count);
  req_nnp(env, arr, "TSRange[] from factory");

  for (uint32_t i = 0; i < count; i++) {
    TSRange *r = (ranges + i);
    env->SetObjectArrayElement(arr, (jint)i, _marshalRange(env, *r));
  }

  free(ranges);

  return arr;
}

static jobjectArray TSTree_includedRanges(JNIEnv *env,
                                          __TS_ATTR_UNUSED jclass self,
                                          jlong tree) {
  req_nnp(env, tree);

  uint32_t count;
  TSRange *ranges = ts_tree_included_ranges((TSTree *)tree, &count);
  if (count == 0) {
    if (ranges != nullptr) {
      free(ranges);
    }

    return nullptr;
  }

  jobjectArray arr = createRangeArr(env, (jint)count);
  req_nnp(env, arr, "TSRange[] from factory");

  for (uint32_t i = 0; i < count; i++) {
    TSRange *r = (ranges + i);
    env->SetObjectArrayElement(arr, (jint)i, _marshalRange(env, *r));
  }

  free(ranges);

  return arr;
}

static jlong TSTree_getLanguage(JNIEnv *env, __TS_ATTR_UNUSED jclass self,
                                jlong tree) {
  req_nnp(env, tree);
  return (jlong)ts_tree_language((TSTree *)tree);
}

void TSTree_Native__SetJniMethods(JNINativeMethod *methods,
                                  __TS_ATTR_UNUSED int count) {
  SET_JNI_METHOD(methods, TSTree_Native_edit, TSTree_edit)
  SET_JNI_METHOD(methods, TSTree_Native_delete, TSTree_delete)
  SET_JNI_METHOD(methods, TSTree_Native_copy, TSTree_copy)
  SET_JNI_METHOD(methods, TSTree_Native_rootNode, TSTree_rootNode)
  SET_JNI_METHOD(methods, TSTree_Native_rootNodeWithOffset,
                 TSTree_rootNodeWithOffset)
  SET_JNI_METHOD(methods, TSTree_Native_changedRanges, TSTree_changedRanges)
  SET_JNI_METHOD(methods, TSTree_Native_includedRanges, TSTree_includedRanges)
  SET_JNI_METHOD(methods, TSTree_Native_getLanguage, TSTree_getLanguage)
}