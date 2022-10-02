#include "com_itsaky_androidide_treesitter_TSParser_Native.h"
#include "ts_utils.h"

JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_newParser(
    JNIEnv* env, jclass self) {
  return (jlong)ts_parser_new();
}

JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_delete(
    JNIEnv* env, jclass self, jlong parser) {
  ts_parser_delete((TSParser*)parser);
}

JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_setLanguage(
    JNIEnv* env, jclass self, jlong parser, jlong language) {
  ts_parser_set_language((TSParser*)parser, (TSLanguage*)language);
}

JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_getLanguage(
    JNIEnv* env, jclass self, jlong parser) {
  return (jlong)ts_parser_language((TSParser*)parser);
}

JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_parseBytes(
    JNIEnv* env, jclass self, jlong parser, jbyteArray source_bytes,
    jint length, jint encodingFlag) {
  TSInputEncoding encoding =
      encodingFlag == 0 ? TSInputEncodingUTF8 : TSInputEncodingUTF16;
  jbyte* source = env->GetByteArrayElements(source_bytes, NULL);
  jlong result = (jlong)ts_parser_parse_string_encoding(
      (TSParser*)parser, NULL, reinterpret_cast<const char*>(source), length,
      encoding);
  env->ReleaseByteArrayElements(source_bytes, source, JNI_ABORT);
  return result;
}

JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_incrementalParseBytes(
    JNIEnv* env, jclass self, jlong parser, jlong old_tree,
    jbyteArray source_bytes, jint length, jint encodingFlag) {
  TSInputEncoding encoding =
      encodingFlag == 0 ? TSInputEncodingUTF8 : TSInputEncodingUTF16;
  jbyte* source = env->GetByteArrayElements(source_bytes, NULL);
  jlong result = (jlong)ts_parser_parse_string_encoding(
      (TSParser*)parser, (TSTree*)old_tree,
      reinterpret_cast<const char*>(source), length, TSInputEncodingUTF16);
  env->ReleaseByteArrayElements(source_bytes, source, JNI_ABORT);
  return result;
}

JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_reset(JNIEnv* env,
                                                                 jclass self,
                                                                 jlong parser) {
  ts_parser_reset((TSParser*)parser);
}

JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_setTimeout(
    JNIEnv* env, jclass self, jlong parser, jlong macros) {
  ts_parser_set_timeout_micros((TSParser*)parser, macros);
}

JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_getTimeout(
    JNIEnv* env, jclass self, jlong parser) {
  return (jlong) ts_parser_timeout_micros((TSParser*)parser);
}