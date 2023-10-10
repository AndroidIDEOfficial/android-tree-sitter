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

#ifndef TS_UTILS
#define TS_UTILS

#include "ts_obj_utils.h"

jint getPredicateTypeId(TSQueryPredicateStepType type);

// Node
static jclass nodeClass;
static jfieldID nodeContext0Field;
static jfieldID nodeContext1Field;
static jfieldID nodeContext2Field;
static jfieldID nodeContext3Field;
static jfieldID nodeIdField;
static jfieldID nodeTreeField;

// TreeCursorNode
static jclass treeCursorNodeClass;
static jfieldID treeCursorNodeTypeField;
static jfieldID treeCursorNodeNameField;
static jfieldID treeCursorNodeStartByteField;
static jfieldID treeCursorNodeEndByteField;

// TSPoint
static jclass pointClass;
static jfieldID pointRowField;
static jfieldID pointColumnField;

// TSRange
static jclass rangeClass;
static jfieldID rangeClassStartByteField;
static jfieldID rangeClassEndByteField;
static jfieldID rangeClassStartPointField;
static jfieldID rangeClassEndPointField;

// TSInputEdit
static jclass inputEditClass;
static jfieldID inputEditStartByteField;
static jfieldID inputEditOldEndByteField;
static jfieldID inputEditNewEndByteField;
static jfieldID inputEditStartPointField;
static jfieldID inputEditOldEndPointField;
static jfieldID inputEditNewEndPointField;

// TSQueryMatch
static jclass matchClass;
static jfieldID matchClassIdField;
static jfieldID matchClassPatternIndexField;
static jfieldID matchClassCapturesField;

// TSQueryCapture
static jclass captureClass;
static jfieldID captureClassIndexField;
static jfieldID captureClassNodeField;

// TSQueryPredicateStep
static jclass queryPredicateStepClass;
static jfieldID queryPredicateStepTypeField;
static jfieldID queryPredicateStepValueIdField;

static jclass objectFactoryClass;
static jmethodID factory_createNode;
static jmethodID factory_createTreeCursorNode;
static jmethodID factory_createPoint;
static jmethodID factory_createRange;
static jmethodID factory_createInputEdit;
static jmethodID factory_createQueryMatch;
static jmethodID factory_createQueryCapture;
static jmethodID factory_createQueryPredicateStep;

void onLoad(JNIEnv *env) {

  _loadClass(objectFactoryClass,
             "com/itsaky/androidide/treesitter/internal/NativeObjectFactory")

  _loadStaticMethod(factory_createNode,
                    objectFactoryClass,
                    "createNode",
                    "(IIIIJJ)Lcom/itsaky/androidide/treesitter/TSNode;")

  _loadStaticMethod(factory_createTreeCursorNode,
                    objectFactoryClass,
                    "createTreeCursorNode",
                    "(Ljava/lang/String;Ljava/lang/String;II)Lcom/itsaky/androidide/treesitter/TSTreeCursorNode;")

  _loadStaticMethod(factory_createPoint,
                    objectFactoryClass,
                    "createPoint",
                    "(II)Lcom/itsaky/androidide/treesitter/TSPoint;")

  _loadStaticMethod(factory_createRange,
                    objectFactoryClass,
                    "createRange",
                    "(IIIIII)Lcom/itsaky/androidide/treesitter/TSRange;")

  _loadStaticMethod(factory_createInputEdit,
                    objectFactoryClass,
                    "createInputEdit",
                    "(IIIIIIIII)Lcom/itsaky/androidide/treesitter/TSInputEdit;")

  _loadStaticMethod(factory_createQueryMatch,
                    objectFactoryClass,
                    "createQueryMatch",
                    "(II[Lcom/itsaky/androidide/treesitter/TSQueryCapture;)Lcom/itsaky/androidide/treesitter/TSQueryMatch;")

  _loadStaticMethod(factory_createQueryCapture,
                    objectFactoryClass,
                    "createQueryCapture",
                    "(IIIIIJJ)Lcom/itsaky/androidide/treesitter/TSQueryCapture;")

  _loadStaticMethod(factory_createQueryPredicateStep,
                    objectFactoryClass,
                    "createQueryPredicateStep",
                    "(II)Lcom/itsaky/androidide/treesitter/TSQueryPredicateStep;")

  // Node
  _loadClass(nodeClass, "com/itsaky/androidide/treesitter/TSNode")
  _loadField(nodeContext0Field, nodeClass, "context0", "I")
  _loadField(nodeContext1Field, nodeClass, "context1", "I")
  _loadField(nodeContext2Field, nodeClass, "context2", "I")
  _loadField(nodeContext3Field, nodeClass, "context3", "I")
  _loadField(nodeIdField, nodeClass, "id", "J")
  _loadField(nodeTreeField, nodeClass, "tree", "J")

  // TreeCursorNode
  _loadClass(treeCursorNodeClass,
             "com/itsaky/androidide/treesitter/TSTreeCursorNode")
  _loadField(treeCursorNodeTypeField, treeCursorNodeClass, "type",
             "Ljava/lang/String;")
  _loadField(treeCursorNodeNameField, treeCursorNodeClass, "name",
             "Ljava/lang/String;")
  _loadField(treeCursorNodeStartByteField, treeCursorNodeClass, "startByte",
             "I")
  _loadField(treeCursorNodeEndByteField, treeCursorNodeClass, "endByte", "I")

  // TSPoint
  _loadClass(pointClass, "com/itsaky/androidide/treesitter/TSPoint")
  _loadField(pointRowField, pointClass, "row", "I")
  _loadField(pointColumnField, pointClass, "column", "I")

  // TSRange
  _loadClass(rangeClass, "com/itsaky/androidide/treesitter/TSRange")
  _loadField(rangeClassStartByteField, rangeClass, "startByte", "I")
  _loadField(rangeClassEndByteField, rangeClass, "endByte", "I")
  _loadField(rangeClassStartPointField, rangeClass, "startPoint",
             "Lcom/itsaky/androidide/treesitter/TSPoint;")
  _loadField(rangeClassEndPointField, rangeClass, "endPoint",
             "Lcom/itsaky/androidide/treesitter/TSPoint;")

  // TSInputEdit
  _loadClass(inputEditClass, "com/itsaky/androidide/treesitter/TSInputEdit")
  _loadField(inputEditStartByteField, inputEditClass, "startByte", "I")
  _loadField(inputEditOldEndByteField, inputEditClass, "oldEndByte", "I")
  _loadField(inputEditNewEndByteField, inputEditClass, "newEndByte", "I")
  _loadField(inputEditStartPointField, inputEditClass, "startPoint",
             "Lcom/itsaky/androidide/treesitter/TSPoint;")
  _loadField(inputEditOldEndPointField, inputEditClass, "oldEndPoint",
             "Lcom/itsaky/androidide/treesitter/TSPoint;")
  _loadField(inputEditNewEndPointField, inputEditClass, "newEndPoint",
             "Lcom/itsaky/androidide/treesitter/TSPoint;")

  // TSQueryMatch
  _loadClass(matchClass, "com/itsaky/androidide/treesitter/TSQueryMatch")
  _loadField(matchClassIdField, matchClass, "id", "I")
  _loadField(matchClassPatternIndexField, matchClass, "patternIndex", "I")
  _loadField(matchClassCapturesField, matchClass, "captures",
             "[Lcom/itsaky/androidide/treesitter/TSQueryCapture;")

  // TSQueryCapture
  _loadClass(captureClass, "com/itsaky/androidide/treesitter/TSQueryCapture")
  _loadField(captureClassIndexField, captureClass, "index", "I")
  _loadField(captureClassNodeField, captureClass, "node",
             "Lcom/itsaky/androidide/treesitter/TSNode;")

  // TSQueryPredicateStep
  _loadClass(queryPredicateStepClass,
             "com/itsaky/androidide/treesitter/TSQueryPredicateStep")
  _loadField(queryPredicateStepTypeField, queryPredicateStepClass, "type",
             "I")
  _loadField(queryPredicateStepValueIdField, queryPredicateStepClass,
             "valueId", "I")
}

void onUnload(JNIEnv *env) {
  env->DeleteGlobalRef(nodeClass);
  env->DeleteGlobalRef(treeCursorNodeClass);
  env->DeleteGlobalRef(pointClass);
  env->DeleteGlobalRef(rangeClass);
  env->DeleteGlobalRef(inputEditClass);
  env->DeleteGlobalRef(matchClass);
  env->DeleteGlobalRef(captureClass);
  env->DeleteGlobalRef(queryPredicateStepClass);
  env->DeleteGlobalRef(objectFactoryClass);
}

// Node
jobject _marshalNode(JNIEnv *env, TSNode node) {
  return env->CallStaticObjectMethod(objectFactoryClass,
                                     factory_createNode,
                                     (jint) node.context[0],
                                     (jint) node.context[1],
                                     (jint) node.context[2],
                                     (jint) node.context[3],
                                     (jlong) node.id,
                                     (jlong) node.tree);
}

TSNode _unmarshalNode(JNIEnv *env, jobject javaObject) {
  return (TSNode) {
      {
          (uint32_t) env->GetIntField(javaObject, nodeContext0Field),
          (uint32_t) env->GetIntField(javaObject, nodeContext1Field),
          (uint32_t) env->GetIntField(javaObject, nodeContext2Field),
          (uint32_t) env->GetIntField(javaObject, nodeContext3Field),
      },
      (const void *) env->GetLongField(javaObject, nodeIdField),
      (const TSTree *) env->GetLongField(javaObject, nodeTreeField)};
}

// TreeCursorNode
jobject _marshalTreeCursorNode(JNIEnv *env, TreeCursorNode node) {
  return env->CallStaticObjectMethod(objectFactoryClass,
                                     factory_createTreeCursorNode,
                                     env->NewStringUTF(node.type),
                                     env->NewStringUTF(node.name),
                                     (jint) node.startByte,
                                     (jint) node.endByte);
}

// TSPoint
jobject _marshalPoint(JNIEnv *env, TSPoint point) {
  return env->CallStaticObjectMethod(objectFactoryClass,
                                     factory_createPoint,
                                     point.row,
                                     point.column);
}

TSPoint _unmarshalPoint(JNIEnv *env, jobject javaObject) {
  return (TSPoint) {
      (uint32_t) env->GetIntField(javaObject, pointRowField),
      (uint32_t) env->GetIntField(javaObject, pointColumnField),
  };
}

// TSInputEdit
TSInputEdit _unmarshalInputEdit(JNIEnv *env, jobject inputEdit) {
  return (TSInputEdit) {
      (uint32_t) env->GetIntField(inputEdit, inputEditStartByteField),
      (uint32_t) env->GetIntField(inputEdit, inputEditOldEndByteField),
      (uint32_t) env->GetIntField(inputEdit, inputEditNewEndByteField),
      _unmarshalPoint(
          env, env->GetObjectField(inputEdit, inputEditStartPointField)),
      _unmarshalPoint(
          env, env->GetObjectField(inputEdit, inputEditOldEndPointField)),
      _unmarshalPoint(
          env, env->GetObjectField(inputEdit, inputEditNewEndPointField)),
  };
}

jobject _marshalMatch(JNIEnv *env, TSQueryMatch match) {
  jobjectArray captures =
      env->NewObjectArray(match.capture_count, captureClass, nullptr);
  for (int i = 0; i < match.capture_count; i++) {
    const TSQueryCapture *c = match.captures + i;
    env->SetObjectArrayElement(captures, i, _marshalCapture(env, *c));
  }

  return env->CallObjectMethod(objectFactoryClass,
                               factory_createQueryMatch,
                               (jint) match.id,
                               (jint) match.pattern_index,
                               captures);
}

jobject _marshalCapture(JNIEnv *env, TSQueryCapture capture) {
  auto node = capture.node;
  return env->CallObjectMethod(objectFactoryClass,
                               factory_createQueryCapture,
                               (jint) capture.index,
                               (jint) node.context[0],
                               (jint) node.context[1],
                               (jint) node.context[2],
                               (jint) node.context[3],
                               (jlong) node.id,
                               (jlong) node.tree);
}

jobject _marshalRange(JNIEnv *env, TSRange range) {
  return env->CallObjectMethod(objectFactoryClass, factory_createRange,
                               (jint) range.start_byte,
                               (jint) range.end_byte,
                               (jint) range.start_point.row,
                               (jint) range.start_point.column,
                               (jint) range.end_point.row,
                               (jint) range.end_point.column);
}

TSRange _unmarshalRange(JNIEnv *env, jobject javaObject) {
  return (TSRange) {
      _unmarshalPoint(
          env, env->GetObjectField(javaObject, rangeClassStartPointField)),
      _unmarshalPoint(
          env, env->GetObjectField(javaObject, rangeClassEndPointField)),
      (uint32_t) env->GetIntField(javaObject, rangeClassStartByteField),
      (uint32_t) env->GetIntField(javaObject, rangeClassEndByteField)};
}

jobject _marshalQueryPredicateStep(JNIEnv *env,
                                   const TSQueryPredicateStep *predicate) {
  return env->CallObjectMethod(objectFactoryClass,
                               factory_createQueryPredicateStep,
                               (jint) getPredicateTypeId(predicate->type),
                               (jint) predicate->value_id);
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