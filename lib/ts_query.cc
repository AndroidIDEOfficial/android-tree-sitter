#include "com_itsaky_androidide_treesitter_TSQuery_Native.h"
#include "ts_utils.h"

JNIEXPORT jlong JNICALL Java_com_itsaky_androidide_treesitter_TSQuery_00024Native_newQuery(
  JNIEnv* env, jclass self, jlong language, jstring source) {

  const char* c_source;
  uint32_t source_length = env->GetStringLength(source);
  c_source = env->GetStringUTFChars(source, NULL);
  uint32_t* error_offset = new uint32_t;
  TSQueryError* error_type = new TSQueryError;
  TSQuery* query = ts_query_new((TSLanguage*) language, c_source, source_length, error_offset, error_type);
  return (jlong) query;
}
