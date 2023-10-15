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

#include "subtree.h"

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_canAccess(JNIEnv *env,
                                                                   jclass clazz,
                                                                   jlong id) {
  const auto *subtree = (const Subtree *)id;
  if (subtree == nullptr) {
    return (jboolean) false;
  }

  return (jboolean) (subtree->data.is_inline || subtree->ptr != nullptr);
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_getParent(JNIEnv *env,
                                                                   jclass klass,
                                                                   jobject self) {
  return _marshalNode(env, ts_node_parent(_unmarshalNode(env, self)));
}

extern "C" JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_getChildCount(JNIEnv *env,
                                                                       jclass clazz,
                                                                       jobject self) {
  return (jint) ts_node_child_count(_unmarshalNode(env, self));
}

extern "C" JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_getNamedChildCount(
    JNIEnv *env,
    jclass clazz,
    jobject self) {
  return (jint) ts_node_named_child_count(_unmarshalNode(env, self));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_getChildAt(JNIEnv *env,
                                                                    jclass clazz,
                                                                    jobject self,
                                                                    jint child) {
  return _marshalNode(
      env, ts_node_child(_unmarshalNode(env, self), (uint32_t) child));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_getNamedChildAt(JNIEnv *env,
                                                                         jclass clazz,
                                                                         jobject self,
                                                                         jint child) {
  return _marshalNode(
      env, ts_node_named_child(_unmarshalNode(env, self), (uint32_t) child));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_getChildByFieldName(
    JNIEnv *env,
    jclass clazz,
    jobject self,
    jbyteArray name,
    jint length) {
  jbyte *nameStr = env->GetByteArrayElements(name, nullptr);
  jobject found = _marshalNode(
      env, ts_node_child_by_field_name(_unmarshalNode(env, self),
                                       reinterpret_cast<const char *>(nameStr),
                                       (uint32_t) length));
  env->ReleaseByteArrayElements(name, nameStr, JNI_ABORT);
  return found;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_getFieldNameForChild(
    JNIEnv *env,
    jclass clazz,
    jobject self,
    jint index) {
  const char *fieldName =
      ts_node_field_name_for_child(_unmarshalNode(env, self), index);
  if (fieldName == nullptr) {
    return nullptr;
  }

  jstring result = env->NewStringUTF(fieldName);
  return result;
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_getChildByFieldId(
    JNIEnv *env,
    jclass clazz,
    jobject self,
    jint fieldId) {
  return _marshalNode(
      env, ts_node_child_by_field_id(_unmarshalNode(env, self), fieldId));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_getNextSibling(JNIEnv *env,
                                                                        jclass clazz,
                                                                        jobject self) {
  return _marshalNode(env, ts_node_next_sibling(_unmarshalNode(env, self)));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_getPreviousSibling(
    JNIEnv *env,
    jclass clazz,
    jobject self) {
  return _marshalNode(env, ts_node_prev_sibling(_unmarshalNode(env, self)));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_getNextNamedSibling(
    JNIEnv *env,
    jclass clazz,
    jobject self) {
  return _marshalNode(env,
                      ts_node_next_named_sibling(_unmarshalNode(env, self)));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_getPreviousNamedSibling(
    JNIEnv *env,
    jclass clazz,
    jobject self) {
  return _marshalNode(env,
                      ts_node_prev_named_sibling(_unmarshalNode(env, self)));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_getFirstChildForByte(
    JNIEnv *env,
    jclass clazz,
    jobject self,
    jint offset) {
  return _marshalNode(
      env, ts_node_first_child_for_byte(_unmarshalNode(env, self), offset));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_getFirstNamedChildForByte(
    JNIEnv *env,
    jclass clazz,
    jobject self,
    jint offset) {
  return _marshalNode(env, ts_node_first_named_child_for_byte(
      _unmarshalNode(env, self), offset));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_getDescendantForByteRange(
    JNIEnv *env,
    jclass clazz,
    jobject self,
    jint start,
    jint end) {
  return _marshalNode(env, ts_node_descendant_for_byte_range(
      _unmarshalNode(env, self), start, end));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_getNamedDescendantForByteRange(
    JNIEnv *env,
    jclass clazz,
    jobject self,
    jint start,
    jint end) {
  return _marshalNode(env, ts_node_named_descendant_for_byte_range(
      _unmarshalNode(env, self), start, end));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_getDescendantForPointRange(
    JNIEnv *env,
    jclass clazz,
    jobject self,
    jobject start,
    jobject end) {
  req_nnp(env, start, "start");
  req_nnp(env, end, "end");
  TSPoint sPoint = _unmarshalPoint(env, start);
  TSPoint ePoint = _unmarshalPoint(env, end);
  return _marshalNode(env, ts_node_descendant_for_point_range(
      _unmarshalNode(env, self), sPoint, ePoint));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_getNamedDescendantForPointRange(
    JNIEnv *env,
    jclass clazz,
    jobject self,
    jobject start,
    jobject end) {
  req_nnp(env, start, "start");
  req_nnp(env, end, "end");
  TSPoint sPoint = _unmarshalPoint(env, start);
  TSPoint ePoint = _unmarshalPoint(env, end);
  return _marshalNode(env, ts_node_named_descendant_for_point_range(
      _unmarshalNode(env, self), sPoint, ePoint));
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_isEqualTo
    (JNIEnv *env, jclass clazz, jobject self, jobject other) {
  req_nnp(env, other, "other");
  return (jboolean) ts_node_eq(_unmarshalNode(env, self),
                               _unmarshalNode(env, other));
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_getNodeString(JNIEnv *env,
                                                                       jclass clazz,
                                                                       jobject self) {
  char *nodeString = ts_node_string(_unmarshalNode(env, self));
  jstring result = env->NewStringUTF(nodeString);
  free(nodeString);
  return result;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_getEndByte(JNIEnv *env,
                                                                    jclass clazz,
                                                                    jobject self) {
  return (jint) ts_node_end_byte(_unmarshalNode(env, self));
}

extern "C" JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_getStartByte(JNIEnv *env,
                                                                      jclass clazz,
                                                                      jobject self) {
  return (jint) ts_node_start_byte(_unmarshalNode(env, self));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_getStartPoint(JNIEnv *env,
                                                                       jclass clazz,
                                                                       jobject self) {
  return _marshalPoint(env, ts_node_start_point(_unmarshalNode(env, self)));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_getEndPoint(JNIEnv *env,
                                                                     jclass clazz,
                                                                     jobject self) {
  return _marshalPoint(env, ts_node_end_point(_unmarshalNode(env, self)));
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_getType(JNIEnv *env,
                                                                 jclass clazz,
                                                                 jobject self) {
  const char *type = ts_node_type(_unmarshalNode(env, self));
  jstring result = env->NewStringUTF(type);
  return result;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_getSymbol(JNIEnv *env,
                                                                   jclass clazz,
                                                                   jobject self) {
  return (jint) ts_node_symbol(_unmarshalNode(env, self));
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_isNull(JNIEnv *env,
                                                                jclass clazz,
                                                                jobject self) {
  return (jboolean) ts_node_is_null(_unmarshalNode(env, self));
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_isNamed(JNIEnv *env,
                                                                 jclass clazz,
                                                                 jobject self) {
  return (jboolean) ts_node_is_named(_unmarshalNode(env, self));
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_isMissing(JNIEnv *env,
                                                                   jclass clazz,
                                                                   jobject self) {
  return (jboolean) ts_node_is_missing(_unmarshalNode(env, self));
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_isExtra(JNIEnv *env,
                                                                 jclass clazz,
                                                                 jobject self) {
  return (jboolean) ts_node_is_extra(_unmarshalNode(env, self));
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_hasChanges(JNIEnv *env,
                                                                    jclass clazz,
                                                                    jobject self) {
  return (jboolean) ts_node_has_changes(_unmarshalNode(env, self));
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_hasErrors(JNIEnv *env,
                                                                   jclass clazz,
                                                                   jobject self) {
  return (jboolean) ts_node_has_error(_unmarshalNode(env, self));
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_isError(JNIEnv *env,
                                                                 jclass clazz,
                                                                 jobject self) {
  return (jboolean) ts_node_is_error(_unmarshalNode(env, self));
}

extern "C"
JNIEXPORT jshort JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_00024Native_getParseState(JNIEnv *env,
                                                                       jclass clazz,
                                                                       jobject self) {
  return (jshort) ts_node_parse_state(_unmarshalNode(env, self));
}