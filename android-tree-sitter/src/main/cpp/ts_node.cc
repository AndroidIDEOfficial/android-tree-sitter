#include "utils/ts_obj_utils.h"

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getParent(JNIEnv* env,
                                                       jobject self) {
  return _marshalNode(env, ts_node_parent(_unmarshalNode(env, self)));
}

extern "C" JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getChildCount(JNIEnv* env,
                                                           jobject self) {
  return (jint)ts_node_child_count(_unmarshalNode(env, self));
}

extern "C" JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getNamedChildCount(JNIEnv* env,
                                                                jobject self) {
  return (jint)ts_node_named_child_count(_unmarshalNode(env, self));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getChildAt(JNIEnv* env,
                                                        jobject self,
                                                        jint child) {
  return _marshalNode(
      env, ts_node_child(_unmarshalNode(env, self), (uint32_t)child));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getNamedChildAt(JNIEnv* env,
                                                             jobject self,
                                                             jint child) {
  return _marshalNode(
      env, ts_node_named_child(_unmarshalNode(env, self), (uint32_t)child));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getChildByFieldName(
    JNIEnv* env, jobject self, jbyteArray name, jint length) {
  jbyte* nameStr = env->GetByteArrayElements(name, NULL);
  jobject found = _marshalNode(
      env, ts_node_child_by_field_name(_unmarshalNode(env, self),
                                       reinterpret_cast<const char*>(nameStr),
                                       (uint32_t)length));
  env->ReleaseByteArrayElements(name, nameStr, JNI_ABORT);
  return found;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getFieldNameForChild(JNIEnv* env,
                                                                  jobject self,
                                                                  jint index) {
  const char* fieldName =
      ts_node_field_name_for_child(_unmarshalNode(env, self), index);
  if (fieldName == NULL) {
    return NULL;
  }

  jstring result = env->NewStringUTF(fieldName);
  return result;
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getChildByFieldId(JNIEnv* env,
                                                               jobject self,
                                                               jint fieldId) {
  return _marshalNode(
      env, ts_node_child_by_field_id(_unmarshalNode(env, self), fieldId));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getNextSibling(JNIEnv* env,
                                                            jobject self) {
  return _marshalNode(env, ts_node_next_sibling(_unmarshalNode(env, self)));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getPreviousSibling(JNIEnv* env,
                                                                jobject self) {
  return _marshalNode(env, ts_node_prev_sibling(_unmarshalNode(env, self)));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getNextNamedSibling(JNIEnv* env,
                                                                 jobject self) {
  return _marshalNode(env,
                      ts_node_next_named_sibling(_unmarshalNode(env, self)));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getPreviousNamedSibling(
    JNIEnv* env, jobject self) {
  return _marshalNode(env,
                      ts_node_prev_named_sibling(_unmarshalNode(env, self)));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getFirstChildForByte(JNIEnv* env,
                                                                  jobject self,
                                                                  jint offset) {
  return _marshalNode(
      env, ts_node_first_child_for_byte(_unmarshalNode(env, self), offset));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getFirstNamedChildForByte(
    JNIEnv* env, jobject self, jint offset) {
  return _marshalNode(env, ts_node_first_named_child_for_byte(
                               _unmarshalNode(env, self), offset));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getDescendantForByteRange(
    JNIEnv* env, jobject self, jint start, jint end) {
  return _marshalNode(env, ts_node_descendant_for_byte_range(
                               _unmarshalNode(env, self), start, end));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getNamedDescendantForByteRange(
    JNIEnv* env, jobject self, jint start, jint end) {
  return _marshalNode(env, ts_node_named_descendant_for_byte_range(
                               _unmarshalNode(env, self), start, end));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getDescendantForPointRange(
    JNIEnv* env, jobject self, jobject start, jobject end) {
  TSPoint sPoint = _unmarshalPoint(env, start);
  TSPoint ePoint = _unmarshalPoint(env, end);
  return _marshalNode(env, ts_node_descendant_for_point_range(
                               _unmarshalNode(env, self), sPoint, ePoint));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getNamedDescendantForPointRange(
    JNIEnv* env, jobject self, jobject start, jobject end) {
  TSPoint sPoint = _unmarshalPoint(env, start);
  TSPoint ePoint = _unmarshalPoint(env, end);
  return _marshalNode(env, ts_node_named_descendant_for_point_range(
                               _unmarshalNode(env, self), sPoint, ePoint));
}

extern "C" JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TSNode_isEqualTo
  (JNIEnv* env, jobject self, jobject other) {
    return (jboolean) ts_node_eq(_unmarshalNode(env, self), _unmarshalNode(env, other));
  }

extern "C" JNIEXPORT jstring JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getNodeString(JNIEnv* env,
                                                           jobject self) {
  char* nodeString = ts_node_string(_unmarshalNode(env, self));
  jstring result = env->NewStringUTF(nodeString);
  free(nodeString);
  return result;
}

extern "C" JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSNode_getEndByte(
    JNIEnv* env, jobject self) {
  return (jint)ts_node_end_byte(_unmarshalNode(env, self));
}

extern "C" JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getStartByte(JNIEnv* env,
                                                          jobject self) {
  return (jint)ts_node_start_byte(_unmarshalNode(env, self));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getStartPoint(JNIEnv* env,
                                                           jobject self) {
  return _marshalPoint(env, ts_node_start_point(_unmarshalNode(env, self)));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getEndPoint(JNIEnv* env,
                                                         jobject self) {
  return _marshalPoint(env, ts_node_end_point(_unmarshalNode(env, self)));
}

extern "C" JNIEXPORT jstring JNICALL Java_com_itsaky_androidide_treesitter_TSNode_getType(
    JNIEnv* env, jobject self) {
  const char* type = ts_node_type(_unmarshalNode(env, self));
  jstring result = env->NewStringUTF(type);
  return result;
}

extern "C" JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSNode_getSymbol(
    JNIEnv* env, jobject self) {
  return (jint)ts_node_symbol(_unmarshalNode(env, self));
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_isNull(JNIEnv* env, jobject self) {
  return (jboolean)ts_node_is_null(_unmarshalNode(env, self));
}

extern "C" JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TSNode_isNamed(
    JNIEnv* env, jobject self) {
  return (jboolean)ts_node_is_named(_unmarshalNode(env, self));
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_isMissing(JNIEnv* env,
                                                       jobject self) {
  return (jboolean)ts_node_is_missing(_unmarshalNode(env, self));
}

extern "C" JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TSNode_isExtra(
    JNIEnv* env, jobject self) {
  return (jboolean)ts_node_is_extra(_unmarshalNode(env, self));
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_hasChanges(JNIEnv* env,
                                                        jobject self) {
  return (jboolean)ts_node_has_changes(_unmarshalNode(env, self));
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_hasErrors(JNIEnv* env,
                                                       jobject self) {
  return (jboolean)ts_node_has_error(_unmarshalNode(env, self));
}