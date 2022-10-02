#include "com_itsaky_androidide_treesitter_TSQueryCursor_Native.h"
#include "ts_utils.h"

#include <iostream>

void fillMatch(JNIEnv *, TSQueryMatch *, jobject);
jobject createCaptureObj(JNIEnv *, const TSQueryCapture *, int, jclass);

JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_TSQueryCursor_00024Native_newCursor(
    JNIEnv *env, jclass self) {
  return (jlong)ts_query_cursor_new();
}

JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSQueryCursor_00024Native_delete(
    JNIEnv *env, jclass self, jlong cursor) {
  ts_query_cursor_delete((TSQueryCursor *)cursor);
}

JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSQueryCursor_00024Native_exec(
    JNIEnv *env, jclass self, jlong cursor, jlong query, jobject node) {
  ts_query_cursor_exec((TSQueryCursor *)cursor, (TSQuery *)query,
                       _unmarshalNode(env, node));
}

JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSQueryCursor_00024Native_exceededMatchLimit(
    JNIEnv *env, jclass self, jlong cursor) {
  return (jboolean)ts_query_cursor_did_exceed_match_limit(
      (TSQueryCursor *)cursor);
}

JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSQueryCursor_00024Native_matchLimit__JI(
    JNIEnv *env, jclass self, jlong cursor, jint newLimit) {
  ts_query_cursor_set_match_limit((TSQueryCursor *)cursor, newLimit);
}

JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_TSQueryCursor_00024Native_matchLimit__J(
    JNIEnv *env, jclass self, jlong cursor) {
  return ts_query_cursor_match_limit((TSQueryCursor *)cursor);
}

JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSQueryCursor_00024Native_setByteRange(
    JNIEnv *env, jclass self, jlong cursor, jint start, jint end) {
  ts_query_cursor_set_byte_range((TSQueryCursor *)cursor, start, end);
}

JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSQueryCursor_00024Native_setPointRange(
    JNIEnv *env, jclass self, jlong cursor, jobject start, jobject end) {
  ts_query_cursor_set_point_range((TSQueryCursor *)cursor,
                                  _unmarshalPoint(env, start),
                                  _unmarshalPoint(env, end));
}

JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSQueryCursor_00024Native_nextMatch(
    JNIEnv *env, jclass self, jlong cursor, jobject match) {
  TSQueryMatch *m = new TSQueryMatch;
  bool b = ts_query_cursor_next_match((TSQueryCursor *)cursor, m);
  if (!b) {
    return false;
  }
  fillMatch(env, m, match);
  return b;
}

JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSQueryCursor_00024Native_removeMatch(
    JNIEnv *env, jclass self, jlong cursor, jint id) {
  ts_query_cursor_remove_match((TSQueryCursor *)cursor, id);
}

void fillMatch(JNIEnv *env, TSQueryMatch *m, jobject match) {
  jclass klass = env->GetObjectClass(match);
  jfieldID id = env->GetFieldID(klass, "id", "I");
  jfieldID pI = env->GetFieldID(klass, "patternIndex", "I");
  jfieldID cs = env->GetFieldID(
      klass, "captures", "[Lcom/itsaky/androidide/treesitter/TSQueryCapture;");
  env->SetIntField(match, id, m->id);
  env->SetIntField(match, pI, m->pattern_index);

  uint16_t count = m->capture_count;
  jclass qClass;
  _loadClass(qClass, "com/itsaky/androidide/treesitter/TSQueryCapture");
  jobjectArray arr = env->NewObjectArray(count, qClass, NULL);

  std::cout << "Match ID:" << m->id << std::endl;
  std::cout << "Match pattern index:" << m->pattern_index << std::endl;
  std::cout << "Match capture count:" << m->capture_count << std::endl;
  std::cout << "Number of captures in match:" << count << std::endl;
  for (uint16_t i = 0; i < count; i++) {
    env->SetObjectArrayElement(arr, i,
                               createCaptureObj(env, m->captures, i, qClass));
  }

  env->SetObjectField(match, cs, arr);
}

jobject createCaptureObj(JNIEnv *env, TSQueryCapture *captures, int index,
                         jclass klass) {
  jobject obj = env->AllocObject(klass);
  jfieldID nF = env->GetFieldID(klass, "node",
                                "Lcom/itsaky/androidide/treesitter/TSNode;");
  jfieldID iF = env->GetFieldID(klass, "index", "I");

  TSQueryCapture *capture = (captures + index);
  env->SetIntField(obj, iF, capture->index);
  env->SetObjectField(obj, nF, _marshalNode(env, capture->node));
  return obj;
}