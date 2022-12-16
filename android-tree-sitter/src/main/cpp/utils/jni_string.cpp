#include "jni_string.h"

/** Constructs a new java.lang.String object from an array of Unicode
 * characters.
 *
 * Returns a Java string object, or NULL if the string cannot be constructed.
 */
jstring FNI_NewString(JNIEnv *env, const jchar *unicodeChars, jsize len) {
    jstring result;
    jclass strcls = env->FindClass("java/lang/String");
    jmethodID cid = env->GetMethodID(strcls, "<init>", "([C)V");
    jcharArray ca = env->NewCharArray(len);
    env->SetCharArrayRegion(ca, 0, len, unicodeChars);
    result = (jstring) env->NewObject(strcls, cid, ca);
    env->DeleteLocalRef(ca);
    env->DeleteLocalRef(strcls);
    return result;
}

/** Returns the length (the count of Unicode characters) of a Java string.
 */
jsize FNI_GetStringLength(JNIEnv *env, jstring string) {
    jclass strcls = env->FindClass("java/lang/String");
    jmethodID mid = env->GetMethodID(strcls, "length", "()I");
    env->DeleteLocalRef(strcls);
    return env->CallIntMethod(string, mid);
}

/** Returns a pointer to the array of Unicode characters of the string.
 * This pointer is valid until ReleaseStringchars() is called.
 *
 * If isCopy is not NULL, then *isCopy is set to JNI_TRUE if a copy is made;
 * or it is set to JNI_FALSE if no copy is made.
 *
 * Returns a pointer to a Unicode string, or NULL if the operation fails.
 */
const jchar *FNI_GetStringChars(JNIEnv *env, jstring string, uint32_t *length, jboolean *isCopy) {
    jclass strcls = env->FindClass("java/lang/String");
    jmethodID mid = env->GetMethodID(strcls, "toCharArray", "()[C");
    auto ca = (jcharArray) env->CallObjectMethod(string, mid);
    jsize len = env->GetArrayLength(ca);
    *length = len;
    auto *result = new jchar [len];
    env->GetCharArrayRegion(ca, 0, len, result);
    if (isCopy != nullptr) *isCopy = JNI_TRUE;
    env->DeleteLocalRef(strcls);
    env->DeleteLocalRef(ca);
    return result;
}

/** Informs the VM that the native code no longer needs access to chars.
 * The chars argument is a pointer obtained from string using GetStringChars().
 */
void FNI_ReleaseStringChars(const jchar *chars) {
    delete [] chars;
}