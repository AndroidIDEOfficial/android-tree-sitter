#include "com_itsaky_androidide_treesitter_TSQuery_Native.h"
#include "ts_utils.h"

JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_TSQuery_00024Native_newQuery(
    JNIEnv* env, jclass self, jobject queryObject, jlong language,
    jstring source) {
  const char* c_source;
  uint32_t source_length = env->GetStringLength(source);
  c_source = env->GetStringUTFChars(source, NULL);
  uint32_t* error_offset = new uint32_t;
  TSQueryError* error_type = new TSQueryError;
  TSQuery* query = ts_query_new((TSLanguage*)language, c_source, source_length,
                                error_offset, error_type);
  fillQuery(env, queryObject, *error_offset, *error_type);
  return (jlong)query;
}

JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSQuery_00024Native_delete(JNIEnv* env,
                                                                 jclass self,
                                                                 jlong query) {
  ts_query_delete((TSQuery*)query);
}

JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_TSQuery_00024Native_captureCount(
    JNIEnv* env, jclass self, jlong query) {
  return ts_query_capture_count((TSQuery*)query);
}

JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_TSQuery_00024Native_patternCount(
    JNIEnv* env, jclass self, jlong query) {
  return ts_query_pattern_count((TSQuery*)query);
}

JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_TSQuery_00024Native_stringCount(
    JNIEnv* env, jclass self, jlong query) {
  return ts_query_string_count((TSQuery*)query);
}

void fillQuery(JNIEnv* env, jobject query, uint32_t error_offset,
               TSQueryError error_type) {
  jclass klass = env->GetObjectClass(query);
  jfieldID offsetField = env->GetFieldID(klass, "errorOffset", "I");
  jfieldID errorTypeField = env->GetFieldID(klass, "errorType", "I");
  env->SetIntField(query, offsetField, error_offset);
  env->SetIntField(query, errorTypeField, getErrorType(error_type));
}

jint getErrorType(TSQueryError error) {
  switch (error) {
    default:
    case TSQueryErrorNone:
      return 0;
    case TSQueryErrorSyntax:
      return 1;
    case TSQueryErrorNodeType:
      return 2;
    case TSQueryErrorField:
      return 3;
    case TSQueryErrorCapture:
      return 4;
    case TSQueryErrorStructure:
      return 5;
    case TSQueryErrorLanguage:
      return 6;
  }
}