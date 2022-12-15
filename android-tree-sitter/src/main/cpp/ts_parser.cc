#include "utils/ts_utils.h"

#include <iostream>

extern "C" JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_newParser(
    JNIEnv* env, jclass self) {
  return (jlong)ts_parser_new();
}

extern "C" JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_delete(
    JNIEnv* env, jclass self, jlong parser) {
  ts_parser_delete((TSParser*)parser);
}

extern "C" JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_setLanguage(
    JNIEnv* env, jclass self, jlong parser, jlong language) {
  ts_parser_set_language((TSParser*)parser, (TSLanguage*)language);
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_getLanguage(
    JNIEnv* env, jclass self, jlong parser) {
  return (jlong)ts_parser_language((TSParser*)parser);
}

extern "C" JNIEXPORT jlong JNICALL
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

extern "C" JNIEXPORT jlong JNICALL
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

extern "C" JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_reset(JNIEnv* env,
                                                                 jclass self,
                                                                 jlong parser) {
  ts_parser_reset((TSParser*)parser);
}

extern "C" JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_setTimeout(
    JNIEnv* env, jclass self, jlong parser, jlong macros) {
  ts_parser_set_timeout_micros((TSParser*)parser, macros);
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_getTimeout(
    JNIEnv* env, jclass self, jlong parser) {
  return (jlong)ts_parser_timeout_micros((TSParser*)parser);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_setIncludedRanges(
    JNIEnv* env, jclass self, jlong parser, jobjectArray ranges) {
  int count = env->GetArrayLength(ranges);
  TSRange tsRanges[count];
  for (int i = 0; i < count; i++) {
    tsRanges[i] = _unmarshalRange(env, env->GetObjectArrayElement(ranges, i));
  }

  const TSRange* r = tsRanges;
  return (jboolean) ts_parser_set_included_ranges((TSParser*)parser, r, count);
}

extern "C" JNIEXPORT jobjectArray JNICALL
Java_com_itsaky_androidide_treesitter_TSParser_00024Native_getIncludedRanges(
    JNIEnv* env, jclass self, jlong parser) {
  uint32_t count;
  const TSRange* ranges = ts_parser_included_ranges((TSParser*)parser, &count);
  jclass klass = env->FindClass("com/itsaky/androidide/treesitter/TSRange");
  jobjectArray result = env->NewObjectArray(count, klass, NULL);
  for (uint32_t i = 0; i < count; i++) {
    const TSRange *r = (ranges + i);
    env->SetObjectArrayElement(result, i, _marshalRange(env, *r));
  }
  return result;
}