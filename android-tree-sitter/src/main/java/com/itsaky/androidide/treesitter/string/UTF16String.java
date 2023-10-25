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

package com.itsaky.androidide.treesitter.string;

import static com.itsaky.androidide.treesitter.string.Assertions.checkIndex;
import static com.itsaky.androidide.treesitter.string.Assertions.checkStringRange;

import com.itsaky.androidide.treesitter.TSNativeObject;
import com.itsaky.androidide.treesitter.annotations.DontSynchronize;
import com.itsaky.androidide.treesitter.annotations.Synchronized;
import com.itsaky.androidide.treesitter.util.Consumer;
import java.util.Objects;

/**
 * @author Akash Yadav
 */
@Synchronized(packagePrivateConstructor = false)
public class UTF16String extends TSNativeObject implements CharSequence {

  public UTF16String(long pointer) {
    super(pointer);
  }

  /**
   * Get the byte at the given index.
   *
   * @param index The index of the byte.
   * @return The byte.
   */
  public byte byteAt(int index) {
    checkIndex(index, byteLength());
    return Native.byteAt(getNativeObject(), index);
  }

  /**
   * Set the byte at the given index.
   *
   * @param index The index of the byte.
   */
  public void setByteAt(int index, byte b) {
    checkIndex(index, byteLength());
    Native.setByteAt(getNativeObject(), index, b);
  }

  /**
   * Get the char at the given index.
   *
   * @param index The index of the char.
   * @return The char.
   */
  @Override
  public char charAt(int index) {
    checkIndex(index, length());
    return Native.chatAt(getNativeObject(), index);
  }

  /**
   * Set the char at the given index.
   *
   * @param index The index of the char.
   */
  public void setCharAt(int index, char c) {
    checkIndex(index, length());
    Native.setCharAt(getNativeObject(), index, c);
  }

  /**
   * Appends the given string to the end of this {@link UTF16String}.
   *
   * @param string The string to append.
   */
  public void append(String string) {
    Native.append(getNativeObject(), string);
  }

  /**
   * Appends the given string to the end of this {@link UTF16String}.
   *
   * @param string    The string to append.
   * @param fromIndex The start offset to append from. This should be Java {@code char}-based index
   *                  in the given string.
   * @param length    The number of character to append from the given string.
   */
  public void append(String string, int fromIndex, int length) {
    checkStringRange(string, fromIndex, length);
    Native.appendPart(getNativeObject(), string, fromIndex, length);
  }

  /**
   * Inserts the given string at the given index.
   *
   * @param index  The index to insert at. This should be Java {@code char}-based index * in the
   *               given string.
   * @param string The string to insert.
   */
  public void insert(int index, String string) {
    if (index == length()) {
      append(string);
      return;
    }
    checkIndex(index, length());
    Native.insert(getNativeObject(), string, index);
  }

  /**
   * Deletes the contents of this {@link UTF16String} between the given indices. The indices must be
   * Java {@code char} based.
   *
   * @param fromIndex The index to delete from.
   * @param toIndex   The index to delete to.
   */
  public void delete(int fromIndex, int toIndex) {
    int size = length();
    checkIndex(fromIndex, size);
    checkIndex(toIndex, size + 1);
    Native.deleteChars(getNativeObject(), fromIndex, toIndex);
  }

  /**
   * Deletes the contents of this string between the given byte indices.
   *
   * @param fromIndex The byte index to delete from.
   * @param toIndex   The byte index to delete to.
   */
  public void deleteBytes(int fromIndex, int toIndex) {
    int size = byteLength();
    checkIndex(fromIndex, size);
    checkIndex(toIndex, size + 1);
    Native.deleteBytes(getNativeObject(), fromIndex, toIndex);
  }

  /**
   * Replaces the contents of this {@link UTF16String} between the given indices with the given
   * string. The indices should be Java {@code char}-based indices in the given string.
   *
   * @param fromIndex The index to replace from.
   * @param toIndex   The index to replace to.
   * @param str       The string to replace with.
   */
  public void replaceChars(int fromIndex, int toIndex, String str) {
    if (str.length() == 0) {
      delete(fromIndex, toIndex);
      return;
    }

    int size = length();
    checkIndex(fromIndex, size);
    checkIndex(toIndex, size + 1);
    Native.replaceChars(getNativeObject(), fromIndex, toIndex, str);
  }

  /**
   * Replaces the contents of this {@link UTF16String} between the given indices with the given
   * string. The indices should be Java {@code byte}-based indices in the given string.
   *
   * @param fromIndex The index to replace from.
   * @param toIndex   The index to replace to.
   * @param str       The string to replace with.
   */
  public void replaceBytes(int fromIndex, int toIndex, String str) {
    if (str.length() == 0) {
      deleteBytes(fromIndex, toIndex);
      return;
    }

    int size = byteLength();
    checkIndex(fromIndex, size);
    checkIndex(toIndex, size + 1);
    Native.replaceBytes(getNativeObject(), fromIndex, toIndex, str);
  }

  /**
   * Get the subsequence of this string.
   *
   * @param start The start index of the substring in characters.
   * @return The subsequence.
   */
  @DontSynchronize
  public UTF16String subseqChars(int start) {
    return subseqChars(start, length());
  }

  /**
   * Get the subsequence of this string.
   *
   * @param start The start index of the substring in characters.
   * @param end   The start index of the substring in characters (exclusive).
   * @return The subsequence.
   */
  public UTF16String subseqChars(int start, int end) {
    int size = length();
    checkIndex(start, size);
    checkIndex(end, size + 1);
    return UTF16StringFactory.createString(Native.substring_chars(getNativeObject(), start, end));
  }

  /**
   * Get the subsequence of this string.
   *
   * @param start The start index of the substring in bytes.
   * @return The subsequence.
   */
  public UTF16String subseqBytes(int start) {
    int size = byteLength();
    checkIndex(start, size);
    return subseqBytes(start, byteLength());
  }

  /**
   * Get the subsequence of this string.
   *
   * @param start The start index of the substring in bytes.
   * @param end   The start index of the substring in bytes (exclusive).
   * @return The subsequence.
   */
  public UTF16String subseqBytes(int start, int end) {
    int size = byteLength();
    checkIndex(start, size);
    checkIndex(end, size + 1);
    return UTF16StringFactory.createString(Native.substring_bytes(getNativeObject(), start, end));
  }

  /**
   * Get the substring of this string.
   *
   * @param start The start index of the substring in characters.
   * @return The substring.
   */
  public String substringChars(int start) {
    int size = length();
    checkIndex(start, size);
    return substringChars(start, length());
  }

  /**
   * Get the substring of this string.
   *
   * @param start The start index of the substring in characters.
   * @param end   The start index of the substring in characters (exclusive).
   * @return The substring.
   */
  public String substringChars(int start, int end) {
    int size = length();
    checkIndex(start, size);
    checkIndex(end, size + 1);
    return Native.subjstring_chars(getNativeObject(), start, end);
  }

  /**
   * Get the substring of this string.
   *
   * @param start The start index of the substring in bytes.
   * @return The substring.
   */
  public String substringBytes(int start) {
    int size = byteLength();
    checkIndex(start, size);
    return substringBytes(start, length());
  }

  /**
   * Get the substring of this string.
   *
   * @param start The start index of the substring in bytes.
   * @param end   The start index of the substring in bytes (exclusive).
   * @return The substring.
   */
  public String substringBytes(int start, int end) {
    int size = byteLength();
    checkIndex(start, size);
    checkIndex(end, size + 1);
    return Native.subjstring_bytes(getNativeObject(), start, end);
  }

  /**
   * Get the length of this string in terms of Java characters.
   *
   * @return The length in characters.
   */
  public int length() {
    return Native.length(getNativeObject());
  }

  /**
   * Get the length of this string in terms of Java bytes.
   *
   * @return The length in bytes.
   */
  public int byteLength() {
    return Native.byteLength(getNativeObject());
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    final var count = length();
    checkIndex(start, count);
    checkIndex(start, count + 1);
    return subseqChars(start, end);
  }

  /**
   * Close this string and release resources.
   */
  @Override
  public void closeNativeObj() {
    Native.erase(getNativeObject());
  }

  @Override
  public String toString() {
    return Native.toString(getNativeObject());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof UTF16String)) {
      return false;
    }
    UTF16String that = (UTF16String) o;
    return getNativeObject() == that.getNativeObject();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getNativeObject());
  }

  /**
   * Iterate over the characters in this string.
   *
   * @param consumer The consumer to consume the characters.
   */
  @DontSynchronize
  public void forEachChar(Consumer<Character> consumer) {
    forEachChar(0, length(), consumer);
  }

  /**
   * Iterate over the characters in this string.
   *
   * @param from     The start index (inclusive).
   * @param to       The end index (exclusive).
   * @param consumer The consumer to consume the characters.
   */
  public void forEachChar(int from, int to, Consumer<Character> consumer) {
    final var length = length();
    Assertions.checkIndex(from, length);
    Assertions.checkUpperBound(to, length);
    for (int i = from; i < to; i++) {
      consumer.accept(charAt(i));
    }
  }

  /**
   * Iterate over the bytes in this string.
   *
   * @param consumer The consumer to consume the bytes.
   */
  @DontSynchronize
  public void forEachByte(Consumer<Byte> consumer) {
    forEachByte(0, byteLength(), consumer);
  }

  /**
   * Iterate over the bytes in this string.
   *
   * @param from     The start byte index (inclusive).
   * @param to       The end byte index (exclusive).
   * @param consumer The consumer to consume the bytes.
   */
  public void forEachByte(int from, int to, Consumer<Byte> consumer) {
    final var length = length();
    Assertions.checkIndex(from, length);
    Assertions.checkUpperBound(to, length);
    for (int i = from; i < to; i++) {
      consumer.accept(byteAt(i));
    }
  }

  /**
   * Returns a new synchronized version of this string. Please note that the returned string will
   * still use the same native object as this string and hence, changes made to either of the
   * strings will be reflected in both.
   *
   * @return A new synchronized version of this string, or <code>this</code> object if it is a
   * {@link SynchronizedUTF16String} instance.
   */
  public UTF16String synchronizedString() {
    if (this instanceof SynchronizedUTF16String) {
      return this;
    }
    return UTF16StringFactory.createString(getNativeObject(), true);
  }

  private static class Native {

    static native byte byteAt(long pointer, int index);

    static native void setByteAt(long pointer, int index, byte b);

    static native char chatAt(long pointer, int index);

    static native void setCharAt(long pointer, int index, char c);

    static native void append(long pointer, String str);

    static native void appendPart(long pointer, String str, int fromIndex, int len);

    static native void insert(long pointer, String str, int index);

    static native void deleteChars(long pointer, int start, int end);

    static native void deleteBytes(long pointer, int start, int end);

    static native void replaceChars(long pointer, int start, int end, String str);

    static native void replaceBytes(long pointer, int start, int end, String str);

    static native long substring_chars(long pointer, int start, int end);

    static native long substring_bytes(long pointer, int start, int end);

    static native String subjstring_chars(long pointer, int start, int end);

    static native String subjstring_bytes(long pointer, int start, int end);

    static native String toString(long pointer);

    static native int length(long pointer);

    static native int byteLength(long pointer);

    static native void erase(long pointer);
  }
}
