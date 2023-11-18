/*
 *  This file is part of android-tree-sitter.
 *
 *  android-tree-sitter library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  android-tree-sitter library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *  along with android-tree-sitter.  If not, see <https://www.gnu.org/licenses/>.
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
    vector<jbyte> _string;

public:
    UTF16String();
    UTF16String(vector<jbyte> bytes);

    /**
     * Get the Java 'byte' at the given index.
     * @param index The index of the byte to retrive.
     * @return The Java byte.
     */
    jbyte byte_at(jint index);

    /**
     * Get the Java 'char' at the given index.
     * @param index The index of the character to retrive.
     * @return The Java character.
     */
    jchar char_at(jint index);

    /**
     * Set the Java 'byte' at the given index.
     * @param index The index of the byte to set.
     * @return The Java byte.
     */
    UTF16String *set_byte_at(jint index, jbyte byte);

    /**
     * Set the Java 'char' at the given index.
     * @param index The index of the character to set.
     * @return The Java character.
     */
    UTF16String *set_char_at(jint index, jchar c);

    /**
     * Insert the Java 'byte' at the given index.
     * @param byte The Java byte to insert.
     * @param index The index to insert at.
     * @return Returns this instance.
     */
    UTF16String *insert(jint index, jbyte byte);

    /**
     * Insert the Java 'char' at the given index.
     * @param c The Java character to insert.
     * @param index The index to insert at.
     * @return Returns this instance.
     */
    UTF16String *insert(jint index, jchar c);

    /**
     * Appends the given Java character.
     * @param c The character to append.
     */
    void append(jchar c);

    /**
     * Appends the given source jstring to this UTF16String.
     * @param src The jstring to append.
     * @return Returns this instance.
     */
    UTF16String *append(JNIEnv *env, jstring src);

    /**
     * Appends the given part of the source jstring to this UTF16String.
     * @param src The jstring to append.
     * @return Returns this instance.
     */
    UTF16String *append(JNIEnv *env, jstring src, jint from, jint len);

    /**
     * Insert the given string at the given index.
     * @param env The JNI environment.
     * @param src The source string to insert.
     * @param index The index to insert at.
     * @return Returns this instance.
     */
    UTF16String *insert(JNIEnv *env, jstring src, jint index);

    /**
     * Deletes the range of characters from this string. The indices must be Java char-based.
     *
     * @param env The JNI environment.
     * @param start The start index to delete from.
     * @param end The end index to delete to.
     * @return Returns this instance.
     */
    UTF16String *delete_chars(jint start, jint end);

    /**
     * Deletes the range of characters from this string. The indices must be byte-based.
     *
     * @param env The JNI environment.
     * @param start The start index to delete from.
     * @param end The end index to delete to.
     * @return Returns this instance.
     */
    UTF16String *delete_bytes(jint start, jint end);

    /**
     * Replaces the range of characters from this string. The indices must be Java char-based.
     * @param env The JNI environment.
     * @param start The start index to replace from.
     * @param end The end index to replace to.
     * @param str The string to replace with.
     * @return Returns this instance.
     */
    UTF16String *replace_chars(JNIEnv *env, jint start, jint end, jstring str);

    /**
     * Replaces the range of characters from this string. The indices must be byte-based.
     * @param env The JNI environment.
     * @param start The start index to replace from.
     * @param end The end index to replace to.
     * @param str The string to replace with.
     * @return Returns this instance.
     */
    UTF16String *replace_bytes(JNIEnv *env, jint start, jint end, jstring str);

    /**
     * Creates a sub-string of this string containing the characters between the given indices.
     * The indices must be Java char-based.
     *
     * @param env The JNI environment.
     * @param start The start index.
     * @param end The end index.
     * @return The substring.
     */
    UTF16String *substring_chars(jint start, jint end);

    /**
     * Creates a sub-string of this string containing the characters between the given indices.
     * The indices must be byte-based.
     *
     * @param env The JNI environment.
     * @param start The start index.
     * @param end The end index.
     * @return The substring.
     */
    UTF16String *substring_bytes(jint start, jint end);

    /**
     * Creates a sub-string of this string containing the characters between the given indices.
     * The indices must be Java char-based.
     *
     * @param env The JNI environment.
     * @param start The start index.
     * @param end The end index.
     * @return The substring.
     */
    jstring subjstring_chars(JNIEnv *env, jint start, jint end);

    /**
     * Creates a sub-string of this string containing the characters between the given indices.
     * The indices must be byte-based.
     *
     * @param env The JNI environment.
     * @param start The start index.
     * @param end The end index.
     * @return The substring.
     */
    jstring subjstring_bytes(JNIEnv *env, jint start, jint end);

    /**
     * @return The length (char-based) of this string.
     */
    jint length();

    /**
     * @return The byte-based length of this string.
     */
    jint byte_length();

    /**
     * Returns this string as a C-style string.
     *
     * @return This string as C-style string. It is the responsibility of the caller to call
     *         <code>delete [] chars</code> on the returned pointer.
     */
    const char *to_cstring();

    /**
     * @return This string as jstring.
     */
    jstring to_jstring(JNIEnv *env);

    bool operator==(const UTF16String &rhs) const;

    bool operator!=(const UTF16String &rhs) const;
};

UTF16String *as_str(JNIEnv *env, jlong pointer);

int vsize(const vector<jbyte> &vc);

#endif //ANDROIDTREESITTER_UTF16STRING_H