#include <jni.h>
#include <tree_sitter/api.h>

extern "C" TSLanguage *tree_sitter_java();

extern "C"
JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_java_TSLanguageJava_00024Native_newInstance(JNIEnv *env,
                                                                                  jclass clazz) {
    return (jlong) tree_sitter_java();
}