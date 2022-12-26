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

import java.util.Objects;

/**
 * @author Akash Yadav
 */
public class UTF16String implements CharSequence, AutoCloseable {
  private final long pointer;

  UTF16String(long pointer) {
    this.pointer = pointer;
  }

  /**
   * Get the byte at the given index.
   *
   * @param index The index of the byte.
   * @return The byte.
   */
  public byte byteAt(int index) {
    checkIndex(index, byteLength());
    return Native.byteAt(this.pointer, index);
  }

  /**
   * Set the byte at the given index.
   *
   * @param index The index of the byte.
   */
  public void setByteAt(int index, byte b) {
    checkIndex(index, byteLength());
    Native.setByteAt(this.pointer, index, b);
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
    return Native.chatAt(this.pointer, index);
  }

  /**
   * Set the char at the given index.
   *
   * @param index The index of the char.
   */
  public void setCharAt(int index, char c) {
    checkIndex(index, length());
    Native.setCharAt(this.pointer, index, c);
  }

  /**
   * Appends the given string to the end of this {@link UTF16String}.
   *
   * @param string The string to append.
   */
  public void append(String string) {
    Native.append(this.pointer, string);
  }

  /**
   * Appends the given string to the end of this {@link UTF16String}.
   *
   * @param string The string to append.
   * @param fromIndex The start offset to append from. This should be Java {@code char}-based index
   *     in the given string.
   * @param length The number of character to append from the given string.
   */
  public void append(String string, int fromIndex, int length) {
    checkStringRange(string, fromIndex, length);
    Native.appendPart(this.pointer, string, fromIndex, length);
  }

  /**
   * Inserts the given string at the given index.
   *
   * @param index The index to insert at. This should be Java {@code char}-based index * in the
   *     given string.
   * @param string The string to insert.
   */
  public void insert(int index, String string) {
    if (index == length()) {
      append(string);
      return;
    }
    checkIndex(index, length());
    Native.insert(this.pointer, string, index);
  }

  /**
   * Deletes the contents of this {@link UTF16String} between the given indices. The indices must be
   * Java {@code char} based.
   *
   * @param fromIndex The index to delete from.
   * @param toIndex The index to delete to.
   */
  public void delete(int fromIndex, int toIndex) {
    int size = length();
    checkIndex(fromIndex, size);
    checkIndex(toIndex, size + 1);
    Native.deleteChars(this.pointer, fromIndex, toIndex);
  }

  /**
   * Deletes the contents of this string between the given byte indices.
   *
   * @param fromIndex The byte index to delete from.
   * @param toIndex The byte index to delete to.
   */
  public void deleteBytes(int fromIndex, int toIndex) {
    int size = byteLength();
    checkIndex(fromIndex, size);
    checkIndex(toIndex, size + 1);
    Native.deleteBytes(this.pointer, fromIndex, toIndex);
  }

  /**
   * Replaces the contents of this {@link UTF16String} between the given indices with the given
   * string. The indices should be Java {@code char}-based indices in the given string.
   *
   * @param fromIndex The index to replace from.
   * @param toIndex The index to replace to.
   * @param str The string to replace with.
   */
  public void replaceChars(int fromIndex, int toIndex, String str) {
    if (str.length() == 0) {
      delete(fromIndex, toIndex);
      return;
    }

    int size = length();
    checkIndex(fromIndex, size);
    checkIndex(toIndex, size + 1);
    Native.replaceChars(this.pointer, fromIndex, toIndex, str);
  }

  /**
   * Replaces the contents of this {@link UTF16String} between the given indices with the given
   * string. The indices should be Java {@code byte}-based indices in the given string.
   *
   * @param fromIndex The index to replace from.
   * @param toIndex The index to replace to.
   * @param str The string to replace with.
   */
  public void replaceBytes(int fromIndex, int toIndex, String str) {
    if (str.length() == 0) {
      deleteBytes(fromIndex, toIndex);
      return;
    }

    int size = byteLength();
    checkIndex(fromIndex, size);
    checkIndex(toIndex, size + 1);
    Native.replaceBytes(this.pointer, fromIndex, toIndex, str);
  }

  /**
   * Get the subsequence of this string.
   *
   * @param start The start index of the substring in characters.
   * @return The subsequence.
   */
  public UTF16String subseqChars(int start) {
    checkIndex(start, length());
    return subseqChars(start, length());
  }

  /**
   * Get the subsequence of this string.
   *
   * @param start The start index of the substring in characters.
   * @param end The start index of the substring in characters (exclusive).
   * @return The subsequence.
   */
  public UTF16String subseqChars(int start, int end) {
    int size = length();
    checkIndex(start, size);
    checkIndex(end, size + 1);
    return new UTF16String(Native.substring_chars(this.pointer, start, end));
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
   * @param end The start index of the substring in bytes (exclusive).
   * @return The subsequence.
   */
  public UTF16String subseqBytes(int start, int end) {
    int size = byteLength();
    checkIndex(start, size);
    checkIndex(end, size + 1);
    return new UTF16String(Native.substring_bytes(this.pointer, start, end));
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
   * @param end The start index of the substring in characters (exclusive).
   * @return The substring.
   */
  public String substringChars(int start, int end) {
    int size = length();
    checkIndex(start, size);
    checkIndex(end, size + 1);
    return Native.subjstring_chars(this.pointer, start, end);
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
   * @param end The start index of the substring in bytes (exclusive).
   * @return The substring.
   */
  public String substringBytes(int start, int end) {
    int size = byteLength();
    checkIndex(start, size);
    checkIndex(end, size + 1);
    return Native.subjstring_bytes(this.pointer, start, end);
  }

  /**
   * Get the length of this string in terms of Java characters.
   *
   * @return The length in characters.
   */
  public int length() {
    return Native.length(this.pointer);
  }

  /**
   * Get the length of this string in terms of Java bytes.
   *
   * @return The length in bytes.
   */
  public int byteLength() {
    return Native.byteLength(this.pointer);
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    final var count = length();
    checkIndex(start, count);
    checkIndex(start, count + 1);
    return subseqChars(start, end);
  }

  /** Close this string and release resources. */
  @Override
  public void close() {
    Native.erase(this.pointer);
  }

  @Override
  public String toString() {
    return Native.toString(this.pointer);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UTF16String)) return false;
    UTF16String that = (UTF16String) o;
    return pointer == that.pointer;
  }

  /**
   * Get the pointer to the native UTF16String object.
   *
   * @return The pointer.
   */
  public long getPointer() {
    return pointer;
  }

  @Override
  public int hashCode() {
    return Objects.hash(pointer);
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
