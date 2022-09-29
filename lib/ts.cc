#include "com_itsaky_androidide_treesitter_TreeSitter.h"

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