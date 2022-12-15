#ifndef TS_UTILS
#define TS_UTILS

#include "ts_utils.h"

jint getPredicateTypeId(TSQueryPredicateStepType type);

// Node
static jclass _nodeClass;
static jfieldID _nodeContext0Field;
static jfieldID _nodeContext1Field;
static jfieldID _nodeContext2Field;
static jfieldID _nodeContext3Field;
static jfieldID _nodeIdField;
static jfieldID _nodeTreeField;

// TreeCursorNode
static jclass _treeCursorNodeClass;
static jfieldID _treeCursorNodeTypeField;
static jfieldID _treeCursorNodeNameField;
static jfieldID _treeCursorNodeStartByteField;
static jfieldID _treeCursorNodeEndByteField;

// TSPoint
static jclass _pointClass;
static jfieldID _pointRowField;
static jfieldID _pointColumnField;

// TSRange
static jclass _rangeClass;
static jfieldID _rangeClassStartByteField;
static jfieldID _rangeClassEndByteField;
static jfieldID _rangeClassStartPointField;
static jfieldID _rangeClassEndPointField;

// TSInputEdit
static jclass _inputEditClass;
static jfieldID _inputEditStartByteField;
static jfieldID _inputEditOldEndByteField;
static jfieldID _inputEditNewEndByteField;
static jfieldID _inputEditStartPointField;
static jfieldID _inputEditOldEndPointField;
static jfieldID _inputEditNewEndPointField;

// TSQueryMatch
static jclass _matchClass;
static jfieldID _matchClassIdField;
static jfieldID _matchClassPatternIndexField;
static jfieldID _matchClassCapturesField;

// TSQueryCapture
static jclass _captureClass;
static jfieldID _captureClassIndexField;
static jfieldID _captureClassNodeField;

// TSQueryPredicateStep
static jclass _queryPredicateStepClass;
static jfieldID _queryPredicateStepTypeField;
static jfieldID _queryPredicateStepValueIdField;

void onLoad(JNIEnv* env) {
  // Node
  _loadClass(_nodeClass, "com/itsaky/androidide/treesitter/TSNode");
  _loadField(_nodeContext0Field, _nodeClass, "context0", "I");
  _loadField(_nodeContext1Field, _nodeClass, "context1", "I");
  _loadField(_nodeContext2Field, _nodeClass, "context2", "I");
  _loadField(_nodeContext3Field, _nodeClass, "context3", "I");
  _loadField(_nodeIdField, _nodeClass, "id", "J");
  _loadField(_nodeTreeField, _nodeClass, "tree", "J");

  // TreeCursorNode
  _loadClass(_treeCursorNodeClass,
             "com/itsaky/androidide/treesitter/TSTreeCursorNode");
  _loadField(_treeCursorNodeTypeField, _treeCursorNodeClass, "type",
             "Ljava/lang/String;");
  _loadField(_treeCursorNodeNameField, _treeCursorNodeClass, "name",
             "Ljava/lang/String;");
  _loadField(_treeCursorNodeStartByteField, _treeCursorNodeClass, "startByte",
             "I");
  _loadField(_treeCursorNodeEndByteField, _treeCursorNodeClass, "endByte", "I");

  // TSPoint
  _loadClass(_pointClass, "com/itsaky/androidide/treesitter/TSPoint");
  _loadField(_pointRowField, _pointClass, "row", "I");
  _loadField(_pointColumnField, _pointClass, "column", "I");

  // TSRange
  _loadClass(_rangeClass, "com/itsaky/androidide/treesitter/TSRange");
  _loadField(_rangeClassStartByteField, _rangeClass, "startByte", "I");
  _loadField(_rangeClassEndByteField, _rangeClass, "endByte", "I");
  _loadField(_rangeClassStartPointField, _rangeClass, "startPoint",
             "Lcom/itsaky/androidide/treesitter/TSPoint;");
  _loadField(_rangeClassEndPointField, _rangeClass, "endPoint",
             "Lcom/itsaky/androidide/treesitter/TSPoint;");

  // TSInputEdit
  _loadClass(_inputEditClass, "com/itsaky/androidide/treesitter/TSInputEdit");
  _loadField(_inputEditStartByteField, _inputEditClass, "startByte", "I");
  _loadField(_inputEditOldEndByteField, _inputEditClass, "oldEndByte", "I");
  _loadField(_inputEditNewEndByteField, _inputEditClass, "newEndByte", "I");
  _loadField(_inputEditStartPointField, _inputEditClass, "start_point",
             "Lcom/itsaky/androidide/treesitter/TSPoint;");
  _loadField(_inputEditOldEndPointField, _inputEditClass, "old_end_point",
             "Lcom/itsaky/androidide/treesitter/TSPoint;");
  _loadField(_inputEditNewEndPointField, _inputEditClass, "new_end_point",
             "Lcom/itsaky/androidide/treesitter/TSPoint;");

  // TSQueryMatch
  _loadClass(_matchClass, "com/itsaky/androidide/treesitter/TSQueryMatch");
  _loadField(_matchClassIdField, _matchClass, "id", "I");
  _loadField(_matchClassPatternIndexField, _matchClass, "patternIndex", "I");
  _loadField(_matchClassCapturesField, _matchClass, "captures",
             "[Lcom/itsaky/androidide/treesitter/TSQueryCapture;");

  // TSQueryCapture
  _loadClass(_captureClass, "com/itsaky/androidide/treesitter/TSQueryCapture");
  _loadField(_captureClassIndexField, _captureClass, "index", "I");
  _loadField(_captureClassNodeField, _captureClass, "node",
             "Lcom/itsaky/androidide/treesitter/TSNode;");

  // TSQueryPredicateStep
  _loadClass(_queryPredicateStepClass,
             "com/itsaky/androidide/treesitter/TSQueryPredicateStep");
  _loadField(_queryPredicateStepTypeField, _queryPredicateStepClass, "type",
             "I");
  _loadField(_queryPredicateStepValueIdField, _queryPredicateStepClass,
             "valueId", "I");
}

void onUnload(JNIEnv* env) {
  env->DeleteGlobalRef(_nodeClass);
  env->DeleteGlobalRef(_treeCursorNodeClass);
  env->DeleteGlobalRef(_pointClass);
  env->DeleteGlobalRef(_rangeClass);
  env->DeleteGlobalRef(_inputEditClass);
  env->DeleteGlobalRef(_matchClass);
  env->DeleteGlobalRef(_captureClass);
  env->DeleteGlobalRef(_queryPredicateStepClass);
}

// Node
jobject _marshalNode(JNIEnv* env, TSNode node) {
  jobject javaObject = env->AllocObject(_nodeClass);
  env->SetIntField(javaObject, _nodeContext0Field, node.context[0]);
  env->SetIntField(javaObject, _nodeContext1Field, node.context[1]);
  env->SetIntField(javaObject, _nodeContext2Field, node.context[2]);
  env->SetIntField(javaObject, _nodeContext3Field, node.context[3]);
  env->SetLongField(javaObject, _nodeIdField, (jlong)node.id);
  env->SetLongField(javaObject, _nodeTreeField, (jlong)node.tree);
  return javaObject;
}

TSNode _unmarshalNode(JNIEnv* env, jobject javaObject) {
  return (TSNode){
      {
          (uint32_t)env->GetIntField(javaObject, _nodeContext0Field),
          (uint32_t)env->GetIntField(javaObject, _nodeContext1Field),
          (uint32_t)env->GetIntField(javaObject, _nodeContext2Field),
          (uint32_t)env->GetIntField(javaObject, _nodeContext3Field),
      },
      (const void*)env->GetLongField(javaObject, _nodeIdField),
      (const TSTree*)env->GetLongField(javaObject, _nodeTreeField)};
}

// TreeCursorNode
jobject _marshalTreeCursorNode(JNIEnv* env, TreeCursorNode node) {
  jobject javaObject = env->AllocObject(_treeCursorNodeClass);
  env->SetObjectField(javaObject, _treeCursorNodeTypeField,
                      env->NewStringUTF(node.type));
  env->SetObjectField(javaObject, _treeCursorNodeNameField,
                      env->NewStringUTF(node.name));
  env->SetIntField(javaObject, _treeCursorNodeStartByteField, node.startByte);
  env->SetIntField(javaObject, _treeCursorNodeEndByteField, node.endByte);
  return javaObject;
}

// TSPoint
// Not sure why but only the column needs to be divided by 2
jobject _marshalPoint(JNIEnv* env, TSPoint point) {
  jobject javaObject = env->AllocObject(_pointClass);

  env->SetIntField(javaObject, _pointRowField, point.row);
  env->SetIntField(javaObject, _pointColumnField, point.column / 2);
  return javaObject;
}

TSPoint _unmarshalPoint(JNIEnv* env, jobject javaObject) {
  return (TSPoint){
      (uint32_t)env->GetIntField(javaObject, _pointRowField),
      (uint32_t)env->GetIntField(javaObject, _pointColumnField),
  };
}

// TSInputEdit
TSInputEdit _unmarshalInputEdit(JNIEnv* env, jobject inputEdit) {
  return (TSInputEdit){
      (uint32_t)env->GetIntField(inputEdit, _inputEditStartByteField),
      (uint32_t)env->GetIntField(inputEdit, _inputEditOldEndByteField),
      (uint32_t)env->GetIntField(inputEdit, _inputEditNewEndByteField),
      _unmarshalPoint(
          env, env->GetObjectField(inputEdit, _inputEditStartPointField)),
      _unmarshalPoint(
          env, env->GetObjectField(inputEdit, _inputEditOldEndPointField)),
      _unmarshalPoint(
          env, env->GetObjectField(inputEdit, _inputEditNewEndPointField)),
  };
}

jobject _marshalMatch(JNIEnv* env, TSQueryMatch match) {
  jobject obj = env->AllocObject(_matchClass);
  env->SetIntField(obj, _matchClassIdField, match.id);
  env->SetIntField(obj, _matchClassPatternIndexField, match.pattern_index);
  jobjectArray captures =
      env->NewObjectArray(match.capture_count, _captureClass, NULL);
  for (int i = 0; i < match.capture_count; i++) {
    const TSQueryCapture* c = match.captures + i;
    env->SetObjectArrayElement(captures, i, _marshalCapture(env, *c));
  }
  env->SetObjectField(obj, _matchClassCapturesField, captures);
  return obj;
}

jobject _marshalCapture(JNIEnv* env, TSQueryCapture capture) {
  jobject obj = env->AllocObject(_captureClass);
  env->SetIntField(obj, _captureClassIndexField, capture.index);
  env->SetObjectField(obj, _captureClassNodeField,
                      _marshalNode(env, capture.node));
  return obj;
}

jobject _marshalRange(JNIEnv* env, TSRange range) {
  jobject obj = env->AllocObject(_rangeClass);
  env->SetIntField(obj, _rangeClassStartByteField, range.start_byte);
  env->SetIntField(obj, _rangeClassEndByteField, range.end_byte);
  env->SetObjectField(obj, _rangeClassStartPointField,
                      _marshalPoint(env, range.start_point));
  env->SetObjectField(obj, _rangeClassEndPointField,
                      _marshalPoint(env, range.end_point));
  return obj;
}

TSRange _unmarshalRange(JNIEnv* env, jobject javaObject) {
  return (TSRange){
      _unmarshalPoint(
          env, env->GetObjectField(javaObject, _rangeClassStartPointField)),
      _unmarshalPoint(
          env, env->GetObjectField(javaObject, _rangeClassEndPointField)),
      (uint32_t)env->GetIntField(javaObject, _rangeClassStartByteField),
      (uint32_t)env->GetIntField(javaObject, _rangeClassEndByteField)};
}

jobject _marshalQueryPredicateStep(JNIEnv* env,
                                   const TSQueryPredicateStep* predicate) {
  jobject obj = env->AllocObject(_queryPredicateStepClass);
  env->SetIntField(obj, _queryPredicateStepTypeField,
                   getPredicateTypeId(predicate->type));
  env->SetIntField(obj, _queryPredicateStepValueIdField, predicate->value_id);
  return obj;
}

jint getPredicateTypeId(TSQueryPredicateStepType type) {
  switch (type) {
    case TSQueryPredicateStepTypeDone:
      return 0;
    case TSQueryPredicateStepTypeCapture:
      return 1;
    case TSQueryPredicateStepTypeString:
      return 2;
    default:
      return 0;
  }
}

#endif