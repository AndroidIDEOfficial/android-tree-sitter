#include <string.h>
#include "ts_utils.h"

#if defined(__ANDROID__)
static jint JNI_VERSION = JNI_VERSION_1_6;
#else
static jint JNI_VERSION = JNI_VERSION_10;
#endif

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
  JNIEnv* env;
  if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION) != JNI_OK) {
    return JNI_ERR;
  }

  onLoad(env);

  return JNI_VERSION;
}

void JNI_OnUnload(JavaVM* vm, void* reserved) {
  JNIEnv* env;
  vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION);
  onUnload(env);
}


extern "C" JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_getLanguageVersion
  (JNIEnv *env, jclass self) {
    return (jint) TREE_SITTER_LANGUAGE_VERSION;
  }

extern "C" JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_getMinimumCompatibleLanguageVersion
  (JNIEnv *env, jclass self) {
    return (jint) TREE_SITTER_MIN_COMPATIBLE_LANGUAGE_VERSION;
  }