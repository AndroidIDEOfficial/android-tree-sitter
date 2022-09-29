#include "com_itsaky_androidide_treesitter_TreeSitter.h"
#include "ts_utils.h"

JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_nodeChildCount(
  JNIEnv* env, jclass self, jobject node) {
  return (jint)ts_node_child_count(_unmarshalNode(env, node));
}

JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_nodeNamedChildCount(
  JNIEnv* env, jclass self, jobject node) {
  return (jint)ts_node_named_child_count(_unmarshalNode(env, node));
}

JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_nodeChild(
  JNIEnv* env, jclass self, jobject node, jint child) {
  return _marshalNode(
           env, ts_node_child(_unmarshalNode(env, node), (uint32_t)child));
}

JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_nodeNamedChild(
  JNIEnv* env, jclass self, jobject node, jint child) {
  return _marshalNode(
           env, ts_node_named_child(_unmarshalNode(env, node), (uint32_t)child));
}

JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_getChildByFieldName(
  JNIEnv* env, jclass self, jobject node, jbyteArray name, jint length) {
  jbyte* nameStr = env->GetByteArrayElements(name, NULL);
  jobject found = _marshalNode(
           env, ts_node_child_by_field_name(_unmarshalNode(env, node), reinterpret_cast<const char*>(nameStr), (uint32_t) length));
  env->ReleaseByteArrayElements(name, nameStr, JNI_ABORT);
  return found;
}

JNIEXPORT jstring JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_nodeString(
  JNIEnv* env, jclass self, jobject node) {
  char* nodeString = ts_node_string(_unmarshalNode(env, node));
  jstring result = env->NewStringUTF(nodeString);
  free(nodeString);
  return result;
}

JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_nodeEndByte(
  JNIEnv* env, jclass self, jobject node) {
  return (jint)ts_node_end_byte(_unmarshalNode(env, node)) / 2;
}

JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_nodeStartByte(
  JNIEnv* env, jclass self, jobject node) {
  return (jint)ts_node_start_byte(_unmarshalNode(env, node)) / 2;
}

JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_nodeStartPoint(
  JNIEnv* env, jclass self, jobject node) {
  return _marshalPoint(env, ts_node_start_point(_unmarshalNode(env, node)));
}

JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_nodeEndPoint(
  JNIEnv* env, jclass self, jobject node) {
  return _marshalPoint(env, ts_node_end_point(_unmarshalNode(env, node)));
}

JNIEXPORT jstring JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_nodeType(
  JNIEnv* env, jclass self, jobject node) {
  const char* type = ts_node_type(_unmarshalNode(env, node));
  jstring result = env->NewStringUTF(type);
  return result;
}

JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_nodeIsNull
  (JNIEnv* env, jclass self, jobject node) {
  return (jboolean) ts_node_is_null(_unmarshalNode(env, node));
}

JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_nodeIsNamed
  (JNIEnv* env, jclass self, jobject node) {
  return (jboolean) ts_node_is_named(_unmarshalNode(env, node));
}

JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_nodeIsMissing
  (JNIEnv* env, jclass self, jobject node) {
  return (jboolean) ts_node_is_missing(_unmarshalNode(env, node));
}

JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_nodeIsExtra
  (JNIEnv* env, jclass self, jobject node) {
  return (jboolean) ts_node_is_extra(_unmarshalNode(env, node));
}

JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_nodeHasChanges
  (JNIEnv* env, jclass self, jobject node) {
  return (jboolean) ts_node_has_changes(_unmarshalNode(env, node));
}

JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_nodeHasError
  (JNIEnv* env, jclass self, jobject node) {
  return (jboolean) ts_node_has_error(_unmarshalNode(env, node));
}