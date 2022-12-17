#include <jni.h>
#include <vector>

#include "UTF16String.h"
#include "../cache/StrCache.h"

extern "C"
JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_string_UTF16StringFactory_00024Native_newString(JNIEnv *env,
                                                                                      jclass clazz,
                                                                                      jstring source) {
    return (jlong) cache.create(env, source);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_string_UTF16StringFactory_00024Native_newStringBytes(
        JNIEnv *env, jclass clazz, jbyteArray bytes, jint off, jint len) {
    auto ba = env->GetByteArrayElements(bytes, nullptr);
    auto vec = std::vector<jbyte>();
    for (int i = off; i < off + len; ++i) {
        vec.emplace_back(*(ba + off));
    }
    jlong result = (jlong) cache.create(vec);
    env->ReleaseByteArrayElements(bytes, ba,JNI_ABORT);
    return result;
}