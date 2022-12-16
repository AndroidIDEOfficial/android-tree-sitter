//
// Created by itsaky on 12/15/22.
//

#ifndef ANDROIDTREESITTER_UTF16STRING_H
#define ANDROIDTREESITTER_UTF16STRING_H

#include <jni.h>
#include <vector>

using namespace std;

/**
 * Provides access to <code>std::string</code> to Java classes.
 */
class UTF16String {

private:
    std::vector<jbyte> _string;

public:
    UTF16String();

    /**
     * Appends the given source jstring to this StdString.
     * @param src The jstring to append.
     * @return Returns this instance.
     */
    UTF16String *append(JNIEnv *env, jstring src);

    /**
     * @return The length (char-based) of this string.
     */
    jint length();

    /**
     * @return The length (byte-based) of this string.
     */
    jint length_bytes();

    /**
     * @return This string as C-style string.
     */
    const char *to_cstring();

    /**
     * @return This string as jstring.
     */
    jstring to_jstring(JNIEnv *env);

    bool operator==(const UTF16String &rhs) const;

    bool operator!=(const UTF16String &rhs) const;
};

UTF16String *as_str(jlong pointer);
int vsize(const vector<jbyte>& vc);

#endif //ANDROIDTREESITTER_UTF16STRING_H
