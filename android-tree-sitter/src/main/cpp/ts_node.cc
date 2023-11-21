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
#include "ts__log.h"

#include "subtree.h"

#include "ts_node.h"

static jboolean TSNode_canAccess(JNIEnv *env, jclass clazz, jlong id) {
  const auto *subtree = (const Subtree *) id;
  if (subtree == nullptr) {
    return (jboolean) false;
  }

  return (jboolean) (subtree->data.is_inline || subtree->ptr != nullptr);
}

static jobject TSNode_getParent(JNIEnv *env, jclass klass, jobject self) {
  return _marshalNode(env, ts_node_parent(_unmarshalNode(env, self)));
}

static jint TSNode_getChildCount(JNIEnv *env, jclass clazz, jobject self) {
  return (jint) ts_node_child_count(_unmarshalNode(env, self));
}

static jint TSNode_getNamedChildCount(JNIEnv *env, jclass clazz, jobject self) {
  return (jint) ts_node_named_child_count(_unmarshalNode(env, self));
}

static jobject
TSNode_getChildAt(JNIEnv *env, jclass clazz, jobject self, jint child) {
  return _marshalNode(env,
                      ts_node_child(_unmarshalNode(env, self),
                                    (uint32_t) child));
}

static jobject
TSNode_getNamedChildAt(JNIEnv *env, jclass clazz, jobject self, jint child) {
  return _marshalNode(env,
                      ts_node_named_child(_unmarshalNode(env, self),
                                          (uint32_t) child));
}

static jobject TSNode_getChildByFieldName(JNIEnv *env,
                                          jclass clazz,
                                          jobject self,
                                          jbyteArray name,
                                          jint length) {
  jbyte *nameStr = env->GetByteArrayElements(name, nullptr);
  jobject found = _marshalNode(env,
                               ts_node_child_by_field_name(_unmarshalNode(env,
                                                                          self),
                                                           reinterpret_cast<const char *>(nameStr),
                                                           (uint32_t) length));
  env->ReleaseByteArrayElements(name, nameStr, JNI_ABORT);
  return found;
}

static jstring TSNode_getFieldNameForChild(JNIEnv *env,
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

static jobject TSNode_getChildByFieldId(JNIEnv *env,
                                        jclass clazz,
                                        jobject self,
                                        jint fieldId) {
  return _marshalNode(env,
                      ts_node_child_by_field_id(_unmarshalNode(env, self),
                                                fieldId));
}

static jobject TSNode_getNextSibling(JNIEnv *env, jclass clazz, jobject self) {
  return _marshalNode(env, ts_node_next_sibling(_unmarshalNode(env, self)));
}

static jobject
TSNode_getPreviousSibling(JNIEnv *env, jclass clazz, jobject self) {
  return _marshalNode(env, ts_node_prev_sibling(_unmarshalNode(env, self)));
}

static jobject
TSNode_getNextNamedSibling(JNIEnv *env, jclass clazz, jobject self) {
  return _marshalNode(env,
                      ts_node_next_named_sibling(_unmarshalNode(env, self)));
}

static jobject
TSNode_getPreviousNamedSibling(JNIEnv *env, jclass clazz, jobject self) {
  return _marshalNode(env,
                      ts_node_prev_named_sibling(_unmarshalNode(env, self)));
}

static jobject TSNode_getFirstChildForByte(JNIEnv *env,
                                           jclass clazz,
                                           jobject self,
                                           jint offset) {
  return _marshalNode(env,
                      ts_node_first_child_for_byte(_unmarshalNode(env, self),
                                                   offset));
}

static jobject TSNode_getFirstNamedChildForByte(JNIEnv *env,
                                                jclass clazz,
                                                jobject self,
                                                jint offset) {
  return _marshalNode(env,
                      ts_node_first_named_child_for_byte(_unmarshalNode(env,
                                                                        self),
                                                         offset));
}

static jobject TSNode_getDescendantForByteRange(JNIEnv *env,
                                                jclass clazz,
                                                jobject self,
                                                jint start,
                                                jint end) {
  return _marshalNode(env,
                      ts_node_descendant_for_byte_range(_unmarshalNode(env,
                                                                       self),
                                                        start,
                                                        end));
}

static jobject TSNode_getNamedDescendantForByteRange(JNIEnv *env,
                                                     jclass clazz,
                                                     jobject self,
                                                     jint start,
                                                     jint end) {
  return _marshalNode(env,
                      ts_node_named_descendant_for_byte_range(_unmarshalNode(env,
                                                                             self),
                                                              start,
                                                              end));
}

static jobject TSNode_getDescendantForPointRange(JNIEnv *env,
                                                 jclass clazz,
                                                 jobject self,
                                                 jobject start,
                                                 jobject end) {
  req_nnp(env, start, "start");
  req_nnp(env, end, "end");
  TSPoint sPoint = _unmarshalPoint(env, start);
  TSPoint ePoint = _unmarshalPoint(env, end);
  return _marshalNode(env,
                      ts_node_descendant_for_point_range(_unmarshalNode(env,
                                                                        self),
                                                         sPoint,
                                                         ePoint));
}

static jobject TSNode_getNamedDescendantForPointRange(JNIEnv *env,
                                                      jclass clazz,
                                                      jobject self,
                                                      jobject start,
                                                      jobject end) {
  req_nnp(env, start, "start");
  req_nnp(env, end, "end");
  TSPoint sPoint = _unmarshalPoint(env, start);
  TSPoint ePoint = _unmarshalPoint(env, end);
  return _marshalNode(env,
                      ts_node_named_descendant_for_point_range(_unmarshalNode(
                          env,
                          self), sPoint, ePoint));
}

static jboolean
TSNode_isEqualTo(JNIEnv *env, jclass clazz, jobject self, jobject other) {
  req_nnp(env, other, "other");
  return (jboolean) ts_node_eq(_unmarshalNode(env, self),
                               _unmarshalNode(env, other));
}

static jstring TSNode_getNodeString(JNIEnv *env, jclass clazz, jobject self) {
  char *nodeString = ts_node_string(_unmarshalNode(env, self));
  jstring result = env->NewStringUTF(nodeString);
  free(nodeString);
  return result;
}

static jint TSNode_getEndByte(JNIEnv *env, jclass clazz, jobject self) {
  return (jint) ts_node_end_byte(_unmarshalNode(env, self));
}

static jint TSNode_getStartByte(JNIEnv *env, jclass clazz, jobject self) {
  return (jint) ts_node_start_byte(_unmarshalNode(env, self));
}

static jobject TSNode_getStartPoint(JNIEnv *env, jclass clazz, jobject self) {
  return _marshalPoint(env, ts_node_start_point(_unmarshalNode(env, self)));
}

static jobject TSNode_getEndPoint(JNIEnv *env, jclass clazz, jobject self) {
  return _marshalPoint(env, ts_node_end_point(_unmarshalNode(env, self)));
}

static jstring TSNode_getType(JNIEnv *env, jclass clazz, jobject self) {
  const char *type = ts_node_type(_unmarshalNode(env, self));
  jstring result = env->NewStringUTF(type);
  return result;
}

static jint TSNode_getSymbol(JNIEnv *env, jclass clazz, jobject self) {
  return (jint) ts_node_symbol(_unmarshalNode(env, self));
}

static jboolean TSNode_isNull(JNIEnv *env, jclass clazz, jobject self) {
  return (jboolean) ts_node_is_null(_unmarshalNode(env, self));
}

static jboolean TSNode_isNamed(JNIEnv *env, jclass clazz, jobject self) {
  return (jboolean) ts_node_is_named(_unmarshalNode(env, self));
}

static jboolean TSNode_isMissing(JNIEnv *env, jclass clazz, jobject self) {
  return (jboolean) ts_node_is_missing(_unmarshalNode(env, self));
}

static jboolean TSNode_isExtra(JNIEnv *env, jclass clazz, jobject self) {
  return (jboolean) ts_node_is_extra(_unmarshalNode(env, self));
}

static jboolean TSNode_hasChanges(JNIEnv *env, jclass clazz, jobject self) {
  return (jboolean) ts_node_has_changes(_unmarshalNode(env, self));
}

static jboolean TSNode_hasErrors(JNIEnv *env, jclass clazz, jobject self) {
  return (jboolean) ts_node_has_error(_unmarshalNode(env, self));
}

static jboolean TSNode_isError(JNIEnv *env, jclass clazz, jobject self) {
  return (jboolean) ts_node_is_error(_unmarshalNode(env, self));
}

static jshort TSNode_getParseState(JNIEnv *env, jclass clazz, jobject self) {
  return (jshort) ts_node_parse_state(_unmarshalNode(env, self));
}

static void TSNode_edit(JNIEnv *env, jclass clazz, jobject self, jobject edit) {
  TSNode node = _unmarshalNode(env, self);
  TSInputEdit inputEdit = _unmarshalInputEdit(env, edit);
  ts_node_edit(&node, &inputEdit);
}

static jshort
TSNode_getNextParseState(JNIEnv *env, jclass clazz, jobject self) {

  TSNode node = _unmarshalNode(env, self);
  return (jshort) ts_node_next_parse_state(node);
}

static jint TSNode_getDescendantCount(JNIEnv *env, jclass clazz, jobject self) {

  TSNode node = _unmarshalNode(env, self);
  return (jint) ts_node_descendant_count(node);
}


static jstring TSNode_getGrammarType(JNIEnv *env, jclass clazz, jobject self) {
  TSNode node = _unmarshalNode(env, self);
  const char *grammar_type = ts_node_grammar_type(node);
  jstring result = env->NewStringUTF(grammar_type);
  return result;
}

static jlong TSNode_getLanguage(JNIEnv *env, jclass clazz, jobject self) {
  TSNode node = _unmarshalNode(env, self);
  return (jlong) ts_node_language(node);
}

void TSNode_Native__SetJniMethods(JNINativeMethod *methods, int count) {
  SET_JNI_METHOD(methods, TSNode_Native_canAccess, TSNode_canAccess);
  SET_JNI_METHOD(methods, TSNode_Native_getParent, TSNode_getParent);
  SET_JNI_METHOD(methods, TSNode_Native_getChildAt, TSNode_getChildAt);
  SET_JNI_METHOD(methods, TSNode_Native_getNamedChildAt, TSNode_getNamedChildAt);
  SET_JNI_METHOD(methods, TSNode_Native_getChildByFieldName, TSNode_getChildByFieldName);
  SET_JNI_METHOD(methods, TSNode_Native_getFieldNameForChild,
                 TSNode_getFieldNameForChild);
  SET_JNI_METHOD(methods, TSNode_Native_getChildByFieldId, TSNode_getChildByFieldId);
  SET_JNI_METHOD(methods, TSNode_Native_getNextSibling, TSNode_getNextSibling);
  SET_JNI_METHOD(methods, TSNode_Native_getPreviousSibling, TSNode_getPreviousSibling);
  SET_JNI_METHOD(methods, TSNode_Native_getNextNamedSibling, TSNode_getNextNamedSibling);
  SET_JNI_METHOD(methods, TSNode_Native_getPreviousNamedSibling,
                 TSNode_getPreviousNamedSibling);
  SET_JNI_METHOD(methods, TSNode_Native_getFirstChildForByte,
                 TSNode_getFirstChildForByte);
  SET_JNI_METHOD(methods, TSNode_Native_getFirstNamedChildForByte,
                 TSNode_getFirstNamedChildForByte);
  SET_JNI_METHOD(methods, TSNode_Native_getDescendantForByteRange,
                 TSNode_getDescendantForByteRange);
  SET_JNI_METHOD(methods, TSNode_Native_getDescendantForPointRange,
                 TSNode_getDescendantForPointRange);
  SET_JNI_METHOD(methods, TSNode_Native_getNamedDescendantForByteRange,
                 TSNode_getNamedDescendantForByteRange);
  SET_JNI_METHOD(methods, TSNode_Native_getNamedDescendantForPointRange,
                 TSNode_getNamedDescendantForPointRange);
  SET_JNI_METHOD(methods, TSNode_Native_isEqualTo, TSNode_isEqualTo);
  SET_JNI_METHOD(methods, TSNode_Native_getChildCount, TSNode_getChildCount);
  SET_JNI_METHOD(methods, TSNode_Native_getNamedChildCount, TSNode_getNamedChildCount);
  SET_JNI_METHOD(methods, TSNode_Native_getNodeString, TSNode_getNodeString);
  SET_JNI_METHOD(methods, TSNode_Native_getStartByte, TSNode_getStartByte);
  SET_JNI_METHOD(methods, TSNode_Native_getEndByte, TSNode_getEndByte);
  SET_JNI_METHOD(methods, TSNode_Native_getStartPoint, TSNode_getStartPoint);
  SET_JNI_METHOD(methods, TSNode_Native_getEndPoint, TSNode_getEndPoint);
  SET_JNI_METHOD(methods, TSNode_Native_getType, TSNode_getType);
  SET_JNI_METHOD(methods, TSNode_Native_getSymbol, TSNode_getSymbol);
  SET_JNI_METHOD(methods, TSNode_Native_isNull, TSNode_isNull);
  SET_JNI_METHOD(methods, TSNode_Native_isNamed, TSNode_isNamed);
  SET_JNI_METHOD(methods, TSNode_Native_isExtra, TSNode_isExtra);
  SET_JNI_METHOD(methods, TSNode_Native_isMissing, TSNode_isMissing);
  SET_JNI_METHOD(methods, TSNode_Native_hasChanges, TSNode_hasChanges);
  SET_JNI_METHOD(methods, TSNode_Native_hasErrors, TSNode_hasErrors);
  SET_JNI_METHOD(methods, TSNode_Native_isError, TSNode_isError);
  SET_JNI_METHOD(methods, TSNode_Native_getParseState, TSNode_getParseState);
  SET_JNI_METHOD(methods, TSNode_Native_edit, TSNode_edit);
  SET_JNI_METHOD(methods, TSNode_Native_getNextParseState, TSNode_getNextParseState);
  SET_JNI_METHOD(methods, TSNode_Native_getDescendantCount, TSNode_getDescendantCount);
  SET_JNI_METHOD(methods, TSNode_Native_getGrammarType, TSNode_getGrammarType);
  SET_JNI_METHOD(methods, TSNode_Native_getLanguage, TSNode_getLanguage);
}