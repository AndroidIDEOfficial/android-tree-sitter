/*
 *  This file is part of AndroidIDE.
 *
 *  AndroidIDE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  AndroidIDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/\>.
 */

#include "jni_string.h"

jstring FNI_NewAndroidString(JNIEnv *pEnv, const jbyte *bytes, int len);
jstring FNI_NewJVMString(JNIEnv *env, const jbyte *bytes, jsize len);

static const char *charset_name = "UTF_16LE";

/** Constructs a new java.lang.String object from an array of Unicode
 * characters.
 *
 * Returns a Java string object, or NULL if the string cannot be constructed.
 */
jstring FNI_NewJVMString(JNIEnv *env, const jbyte *bytes, jsize len) {
    jstring result;
    jstring charset = env->NewStringUTF(charset_name);
    jclass strcls = env->FindClass("java/lang/String");
    jmethodID cid = env->GetMethodID(strcls, "<init>", "([BLjava/lang/String;)V");
    jbyteArray ba = env->NewByteArray(len);
    env->SetByteArrayRegion(ba, 0, len, bytes);
    result = (jstring) env->NewObject(strcls, cid, ba, charset);
    env->DeleteLocalRef(ba);
    env->DeleteLocalRef(strcls);
    env->DeleteLocalRef(charset);
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
const jchar *FNI_GetStringChars(JNIEnv *env, jstring string, jint *length) {
    jclass strcls = env->FindClass("java/lang/String");
    jmethodID mid = env->GetMethodID(strcls, "toCharArray", "()[C");
    auto ca = (jcharArray) env->CallObjectMethod(string, mid);
    jsize len = env->GetArrayLength(ca);
    if (length != nullptr) *length = len;
    auto *result = new jchar [len];
    env->GetCharArrayRegion(ca, 0, len, result);
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

jstring FNI_NewString(JNIEnv *env, const jbyte *bytes, int len) {
#ifdef __ANDROID__
    return FNI_NewAndroidString(env, bytes, len);
#else
    return FNI_NewJVMString(env, bytes, len);
#endif
}

jstring FNI_NewAndroidString(JNIEnv *env, const jbyte *bytes, int len) {
    jstring result;
    jclass strcls = env->FindClass("com/itsaky/androidide/treesitter/string/StringDecoder");
    jmethodID newStr = env->GetStaticMethodID(strcls, "fromBytes", "([B)Ljava/lang/String;");
    jbyteArray ba = env->NewByteArray(len);
    env->SetByteArrayRegion(ba, 0, len, bytes);
    result = (jstring) env->CallStaticObjectMethod(strcls, newStr, ba);
    env->DeleteLocalRef(ba);
    env->DeleteLocalRef(strcls);
    return result;
}
