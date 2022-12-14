#include "com_itsaky_androidide_treesitter_TSQuery_Native.h"
#include "ts_utils.h"

void fillQuery(JNIEnv*, jobject, uint32_t, TSQueryError);
jint getErrorType(TSQueryError);

extern "C" JNIEXPORT jlong JNICALL
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

extern "C" JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSQuery_00024Native_delete(JNIEnv* env,
                                                                 jclass self,
                                                                 jlong query) {
  ts_query_delete((TSQuery*)query);
}

extern "C" JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_TSQuery_00024Native_captureCount(
    JNIEnv* env, jclass self, jlong query) {
  return ts_query_capture_count((TSQuery*)query);
}

extern "C" JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_TSQuery_00024Native_patternCount(
    JNIEnv* env, jclass self, jlong query) {
  return ts_query_pattern_count((TSQuery*)query);
}

extern "C" JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_TSQuery_00024Native_stringCount(
    JNIEnv* env, jclass self, jlong query) {
  return ts_query_string_count((TSQuery*)query);
}

extern "C" JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_TSQuery_00024Native_startByteForPattern(
    JNIEnv* env, jclass self, jlong query, jint pattern) {
  return (jint)ts_query_start_byte_for_pattern((TSQuery*)query, pattern);
}

extern "C" JNIEXPORT jobjectArray JNICALL
Java_com_itsaky_androidide_treesitter_TSQuery_00024Native_predicatesForPattern(
    JNIEnv* env, jclass self, jlong query, jint pattern) {
  uint32_t count;
  const TSQueryPredicateStep* predicates =
      ts_query_predicates_for_pattern((TSQuery*)query, pattern, &count);
  jclass klass =
      env->FindClass("com/itsaky/androidide/treesitter/TSQueryPredicateStep");
  jobjectArray result = env->NewObjectArray(count, klass, NULL);
  for (uint32_t i = 0; i < count; i++) {
    const TSQueryPredicateStep* predicate = (predicates + i);
    jobject obj = _marshalQueryPredicateStep(env, predicate);
    env->SetObjectArrayElement(result, i, obj);
  }
  return result;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSQuery_00024Native_patternRooted(
    JNIEnv* env, jclass self, jlong query, jint pattern) {
  return (jboolean)ts_query_is_pattern_rooted((TSQuery*)query, pattern);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSQuery_00024Native_patternGuaranteedAtStep(
    JNIEnv* env, jclass self, jlong query, jint offset) {
  return (jboolean)ts_query_is_pattern_guaranteed_at_step((TSQuery*)query,
                                                          offset);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_itsaky_androidide_treesitter_TSQuery_00024Native_captureNameForId(
    JNIEnv* env, jclass self, jlong query, jint id) {
  uint32_t count;
  const char* name = ts_query_capture_name_for_id((TSQuery*)query, id, &count);
  return (jstring)env->NewStringUTF(name);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_itsaky_androidide_treesitter_TSQuery_00024Native_stringValueForId(
    JNIEnv* env, jclass self, jlong query, jint id) {
  uint32_t count;
  const char* str = ts_query_string_value_for_id((TSQuery*)query, id, &count);
  return env->NewStringUTF(str);
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