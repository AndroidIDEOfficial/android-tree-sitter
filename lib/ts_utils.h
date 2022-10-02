#include <jni.h>
#include <tree_sitter/api.h>

struct TreeCursorNode {
  const char* type;
  const char* name;
  uint32_t startByte;
  uint32_t endByte;
};

#define _loadClass(VARIABLE, NAME)             \
  {                                            \
    jclass tmp;                                \
    tmp = env->FindClass(NAME);                \
    VARIABLE = (jclass)env->NewGlobalRef(tmp); \
    env->DeleteLocalRef(tmp);                  \
  }

#define _loadField(VARIABLE, CLASS, NAME, TYPE) \
  { VARIABLE = env->GetFieldID(CLASS, NAME, TYPE); }


void onLoad(JNIEnv* env);

void onUnload(JNIEnv* env);

jobject _marshalNode(JNIEnv* env, TSNode node);
TSNode _unmarshalNode(JNIEnv* env, jobject javaObject);

jobject _marshalPoint(JNIEnv* env, TSPoint point);
TSPoint _unmarshalPoint(JNIEnv* env, jobject javaObject);

jobject _marshalRange(JNIEnv *env, TSRange range);
TSRange _unmarshalRange(JNIEnv *env, jobject javaObject);

jobject _marshalMatch(JNIEnv *env, TSQueryMatch match);
jobject _marshalCapture(JNIEnv *env, TSQueryCapture capture);

jobject _marshalTreeCursorNode(JNIEnv* env, TreeCursorNode node);

TSInputEdit _unmarshalInputEdit(JNIEnv* env, jobject inputEdit);