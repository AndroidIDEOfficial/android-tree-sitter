#include "com_itsaky_androidide_treesitter_TSNode.h"
#include "ts_utils.h"

JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getParent(JNIEnv* env,
                                                       jobject self) {
  return _marshalNode(env, ts_node_parent(_unmarshalNode(env, self)));
}

JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getChildCount(JNIEnv* env,
                                                           jobject self) {
  return (jint)ts_node_child_count(_unmarshalNode(env, self));
}

JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getNamedChildCount(JNIEnv* env,
                                                                jobject self) {
  return (jint)ts_node_named_child_count(_unmarshalNode(env, self));
}

JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getChildAt(JNIEnv* env,
                                                        jobject self,
                                                        jint child) {
  return _marshalNode(
      env, ts_node_child(_unmarshalNode(env, self), (uint32_t)child));
}

JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getNamedChildAt(JNIEnv* env,
                                                             jobject self,
                                                             jint child) {
  return _marshalNode(
      env, ts_node_named_child(_unmarshalNode(env, self), (uint32_t)child));
}

JNIEXPORT jobject JNICALL
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

JNIEXPORT jstring JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getNodeString(JNIEnv* env,
                                                           jobject self) {
  char* nodeString = ts_node_string(_unmarshalNode(env, self));
  jstring result = env->NewStringUTF(nodeString);
  free(nodeString);
  return result;
}

JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSNode_getEndByte(
    JNIEnv* env, jobject self) {
  return (jint)ts_node_end_byte(_unmarshalNode(env, self)) / 2;
}

JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getStartByte(JNIEnv* env,
                                                          jobject self) {
  return (jint)ts_node_start_byte(_unmarshalNode(env, self)) / 2;
}

JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getStartPoint(JNIEnv* env,
                                                           jobject self) {
  return _marshalPoint(env, ts_node_start_point(_unmarshalNode(env, self)));
}

JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_getEndPoint(JNIEnv* env,
                                                         jobject self) {
  return _marshalPoint(env, ts_node_end_point(_unmarshalNode(env, self)));
}

JNIEXPORT jstring JNICALL Java_com_itsaky_androidide_treesitter_TSNode_getType(
    JNIEnv* env, jobject self) {
  const char* type = ts_node_type(_unmarshalNode(env, self));
  jstring result = env->NewStringUTF(type);
  return result;
}

JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSNode_getSymbol
  (JNIEnv* env, jobject self) {
    return (jint) ts_node_symbol(_unmarshalNode(env, self));
  }

JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_isNull(JNIEnv* env, jobject self) {
  return (jboolean)ts_node_is_null(_unmarshalNode(env, self));
}

JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TSNode_isNamed(
    JNIEnv* env, jobject self) {
  return (jboolean)ts_node_is_named(_unmarshalNode(env, self));
}

JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_isMissing(JNIEnv* env,
                                                       jobject self) {
  return (jboolean)ts_node_is_missing(_unmarshalNode(env, self));
}

JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TSNode_isExtra(
    JNIEnv* env, jobject self) {
  return (jboolean)ts_node_is_extra(_unmarshalNode(env, self));
}

JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_hasChanges(JNIEnv* env,
                                                        jobject self) {
  return (jboolean)ts_node_has_changes(_unmarshalNode(env, self));
}

JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSNode_hasErrors(JNIEnv* env,
                                                       jobject self) {
  return (jboolean)ts_node_has_error(_unmarshalNode(env, self));
}