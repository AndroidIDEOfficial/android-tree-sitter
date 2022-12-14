#include <jni.h>
#include <tree_sitter/api.h>

extern "C" TSLanguage *tree_sitter_python();
extern "C"
JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_python_TSLanguagePython_00024Native_newInstance(JNIEnv *env,
                                                                                      jclass clazz) {
    return (jlong) tree_sitter_python();
}