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