#include "ts_utils.h"
#include "com_itsaky_androidide_treesitter_TSQueryCursor_Native.h"

JNIEXPORT jlong JNICALL Java_com_itsaky_androidide_treesitter_TSQueryCursor_00024Native_newCursor
  (JNIEnv* env, jclass self) {
    return (jlong) ts_query_cursor_new();
  }

JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TSQueryCursor_00024Native_delete
  (JNIEnv* env, jclass self, jlong cursor) {
    ts_query_cursor_delete((TSQueryCursor*) cursor);
  }

JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TSQueryCursor_00024Native_exec
  (JNIEnv* env, jclass self, jlong cursor, jlong query, jobject node) {
    ts_query_cursor_exec((TSQueryCursor*) cursor, (TSQuery*) query, _unmarshalNode(env, node));
  }