#include "com_itsaky_androidide_treesitter_TreeSitter.h"

#include <jni.h>
#include <string.h>
#include <tree_sitter/api.h>

struct TreeCursorNode {
  const char* type;
  const char* name;
  uint32_t startByte;
  uint32_t endByte;
};

#if defined(__ANDROID__)
static jint JNI_VERSION = JNI_VERSION_1_6;
#else
static jint JNI_VERSION = JNI_VERSION_10;
#endif

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

// TSInputEdit
static jclass _inputEditClass;
static jfieldID _inputEditStartByteField;
static jfieldID _inputEditOldEndByteField;
static jfieldID _inputEditNewEndByteField;
static jfieldID _inputEditStartPointField;
static jfieldID _inputEditOldEndPointField;
static jfieldID _inputEditNewEndPointField;


#define _loadClass(VARIABLE, NAME)             \
  {                                            \
    jclass tmp;                                \
    tmp = env->FindClass(NAME);                \
    VARIABLE = (jclass)env->NewGlobalRef(tmp); \
    env->DeleteLocalRef(tmp);                  \
  }

#define _loadField(VARIABLE, CLASS, NAME, TYPE) \
  { VARIABLE = env->GetFieldID(CLASS, NAME, TYPE); }

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
  JNIEnv* env;
  if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION) != JNI_OK) {
    return JNI_ERR;
  }

  // Node
  _loadClass(_nodeClass, "com/itsaky/androidide/treesitter/TSNode");
  _loadField(_nodeContext0Field, _nodeClass, "context0", "I");
  _loadField(_nodeContext1Field, _nodeClass, "context1", "I");
  _loadField(_nodeContext2Field, _nodeClass, "context2", "I");
  _loadField(_nodeContext3Field, _nodeClass, "context3", "I");
  _loadField(_nodeIdField, _nodeClass, "id", "J");
  _loadField(_nodeTreeField, _nodeClass, "tree", "J");

  // TreeCursorNode
  _loadClass(_treeCursorNodeClass, "com/itsaky/androidide/treesitter/TSTreeCursorNode");
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

  // TSInputEdit
  _loadClass(_inputEditClass, "com/itsaky/androidide/treesitter/TSInputEdit");
  _loadField(_inputEditStartByteField, _inputEditClass, "startByte", "I");
  _loadField(_inputEditOldEndByteField, _inputEditClass, "oldEndByte", "I");
  _loadField(_inputEditNewEndByteField, _inputEditClass, "newEndByte", "I");
  _loadField(_inputEditStartPointField, _inputEditClass, "start_point", "Lcom/itsaky/androidide/treesitter/TSPoint;");
  _loadField(_inputEditOldEndPointField, _inputEditClass, "old_end_point", "Lcom/itsaky/androidide/treesitter/TSPoint;");
  _loadField(_inputEditNewEndPointField, _inputEditClass, "new_end_point", "Lcom/itsaky/androidide/treesitter/TSPoint;");


  return JNI_VERSION;
}

void JNI_OnUnload(JavaVM* vm, void* reserved) {
  JNIEnv* env;
  vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION);

  env->DeleteGlobalRef(_nodeClass);
  env->DeleteGlobalRef(_treeCursorNodeClass);
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
  return (TSNode) {
    {
      (uint32_t)env->GetIntField(javaObject, _nodeContext0Field),
      (uint32_t)env->GetIntField(javaObject, _nodeContext1Field),
      (uint32_t)env->GetIntField(javaObject, _nodeContext2Field),
      (uint32_t)env->GetIntField(javaObject, _nodeContext3Field),
    },
    (const void*)env->GetLongField(javaObject, _nodeIdField),
    (const TSTree*)env->GetLongField(javaObject, _nodeTreeField)
  };
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

// Point
// Not sure why I need to divide by two
jobject _marshalPoint(JNIEnv* env, TSPoint point) {
  jobject javaObject = env->AllocObject(_pointClass);

  env->SetIntField(javaObject, _pointRowField, point.row / 2);
  env->SetIntField(javaObject, _pointColumnField, point.column / 2);
  return javaObject;
}

TSPoint _unmarshalPoint(JNIEnv* env, jobject javaObject) {
  return (TSPoint) {
    (uint32_t)env->GetIntField(javaObject, _pointRowField),
    (uint32_t)env->GetIntField(javaObject, _pointColumnField),
  };
}

// TSInputEdit
TSInputEdit _unmarshalInputEdit(JNIEnv* env, jobject inputEdit) {
  return (TSInputEdit) {
    (uint32_t)env->GetIntField(inputEdit, _inputEditStartByteField),
    (uint32_t)env->GetIntField(inputEdit, _inputEditOldEndByteField),
    (uint32_t)env->GetIntField(inputEdit, _inputEditNewEndByteField),   
    _unmarshalPoint(env, env->GetObjectField(inputEdit, _inputEditStartPointField)),
    _unmarshalPoint(env, env->GetObjectField(inputEdit, _inputEditOldEndPointField)),
    _unmarshalPoint(env, env->GetObjectField(inputEdit, _inputEditNewEndPointField)),
  };
}

// -------------------------------------------
// ---------- Section: Parser ----------------
// -------------------------------------------

JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_TreeSitter_parserNew(JNIEnv* env, jclass self) {
  return (jlong)ts_parser_new();
}

JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_parserDelete(
  JNIEnv* env, jclass self, jlong parser) {
  ts_parser_delete((TSParser*)parser);
}

JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_parserSetLanguage(
  JNIEnv* env, jclass self, jlong parser, jlong language) {
  ts_parser_set_language((TSParser*)parser, (TSLanguage*)language);
}

JNIEXPORT jlong JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_parserParseBytes(
  JNIEnv* env, jclass self, jlong parser, jbyteArray source_bytes,
  jint length, jint encodingFlag) {
  TSInputEncoding encoding = encodingFlag == 0 ? TSInputEncodingUTF8 : TSInputEncodingUTF16;
  jbyte* source = env->GetByteArrayElements(source_bytes, NULL);
  jlong result = (jlong)ts_parser_parse_string_encoding(
                   (TSParser*)parser, NULL, reinterpret_cast<const char*>(source), length, encoding);
  env->ReleaseByteArrayElements(source_bytes, source, JNI_ABORT);
  return result;
}

JNIEXPORT jlong JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_parserIncrementalParseBytes(
  JNIEnv* env, jclass self, jlong parser, jlong old_tree, jbyteArray source_bytes,
  jint length, jint encodingFlag) {
  TSInputEncoding encoding = encodingFlag == 0 ? TSInputEncodingUTF8 : TSInputEncodingUTF16;
  jbyte* source = env->GetByteArrayElements(source_bytes, NULL);
  jlong result = (jlong)ts_parser_parse_string_encoding(
                   (TSParser*)parser, (TSTree*)old_tree, reinterpret_cast<const char*>(source), length, TSInputEncodingUTF16);
  env->ReleaseByteArrayElements(source_bytes, source, JNI_ABORT);
  return result;
}

// -------------------------------------------
// ---------- Section: TSNode ----------------
// -------------------------------------------

JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_nodeParent(
  JNIEnv* env, jclass self, jobject node) {
  return _marshalNode(env, ts_node_parent(_unmarshalNode(env, node)));
}

JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_nodeChildCount(
  JNIEnv* env, jclass self, jobject node) {
  return (jint)ts_node_child_count(_unmarshalNode(env, node));
}

JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_nodeChild(
  JNIEnv* env, jclass self, jobject node, jint child) {
  return _marshalNode(
           env, ts_node_child(_unmarshalNode(env, node), (uint32_t)child));
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

// -------------------------------------------
// ---------- Section: TSTreeCursor ----------
// -------------------------------------------

JNIEXPORT jlong JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_treeCursorNew(
  JNIEnv* env, jclass self, jobject node) {
  TSTreeCursor* cursor =
    new TSTreeCursor(ts_tree_cursor_new(_unmarshalNode(env, node)));
  return (jlong)cursor;
}

JNIEXPORT jstring JNICALL
Java_com_itsaky_androidide_treesitter_TreeSitter_treeCursorCurrentFieldName(
  JNIEnv* env, jclass self, jlong cursor) {
  const char* name = ts_tree_cursor_current_field_name((TSTreeCursor*)cursor);
  jstring result = env->NewStringUTF(name);
  return result;
}

JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TreeSitter_treeCursorCurrentNode(JNIEnv* env,
    jclass self,
    jlong cursor) {
  return _marshalNode(env, ts_tree_cursor_current_node((TSTreeCursor*)cursor));
}

JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TreeSitter_treeCursorCurrentTreeCursorNode(
  JNIEnv* env, jclass self, jlong cursor) {
  TSNode node = ts_tree_cursor_current_node((TSTreeCursor*)cursor);
  return _marshalTreeCursorNode(
           env,
  (TreeCursorNode) {
    ts_node_type(node),
                 ts_tree_cursor_current_field_name((TSTreeCursor*)cursor),
                 ts_node_start_byte(node) / 2, ts_node_end_byte(node) / 2
  });
}

JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_treeCursorDelete(
  JNIEnv* env, jclass self, jlong cursor) {
  delete (TSTreeCursor*)cursor;
}

JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TreeSitter_treeCursorGotoFirstChild(JNIEnv* env,
    jclass self,
    jlong cursor) {
  return (jboolean)ts_tree_cursor_goto_first_child((TSTreeCursor*)cursor);
}

JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TreeSitter_treeCursorGotoNextSibling(JNIEnv* env,
    jclass self,
    jlong cursor) {
  return (jboolean)ts_tree_cursor_goto_next_sibling((TSTreeCursor*)cursor);
}

JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TreeSitter_treeCursorGotoParent(JNIEnv* env,
    jclass self,
    jlong cursor) {
  return (jboolean)ts_tree_cursor_goto_parent((TSTreeCursor*)cursor);
}

// -------------------------------------------
// ---------- Section: TSTree ----------------
// -------------------------------------------

JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_treeEdit(
  JNIEnv* env, jclass self, jlong tree, jobject inputEdit) {

  TSInputEdit edit = _unmarshalInputEdit(env, inputEdit);
  ts_tree_edit((TSTree*) tree, &edit);
}

JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_treeDelete(
  JNIEnv* env, jclass self, jlong tree) {
  ts_tree_delete((TSTree*)tree);
}

JNIEXPORT jlong JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_treeCopy(
  JNIEnv* env, jclass self, jlong tree) {
  return (jlong) ts_tree_copy((TSTree*)tree);
}

JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_treeRootNode(
  JNIEnv* env, jclass self, jlong tree) {
  return _marshalNode(env, ts_tree_root_node((TSTree*)tree));
}

// -------------------------------------------
// ---------- Section: TSQuery ---------------
// -------------------------------------------

JNIEXPORT jlong JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_tsQueryNew(
  JNIEnv* env, jclass self, jlong language, jstring source) {

  const char* c_source;
  uint32_t source_length = env->GetStringLength(source);
  c_source = env->GetStringUTFChars(source, NULL);
  uint32_t* error_offset = new uint32_t;
  TSQueryError* error_type = new TSQueryError;
  TSQuery* query = ts_query_new((TSLanguage*) language, c_source, source_length, error_offset, error_type);
  return (jlong) query;
}