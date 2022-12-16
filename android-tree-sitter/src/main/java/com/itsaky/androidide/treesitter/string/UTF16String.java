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
 *   along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.itsaky.androidide.treesitter.string;

import java.util.Objects;

/**
 * @author Akash Yadav
 */
public class UTF16String implements AutoCloseable {
  private final long pointer;

  public static UTF16String newInstance() {
    return newInstance("");
  }

  public static UTF16String newInstance(String source) {
    return new UTF16String(Native.newUtf16String(source));
  }

  private UTF16String(long pointer) {
    this.pointer = pointer;
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
   * @param length The number of character to append.
   */
  public void append(String string, int fromIndex, int length) {
    Native.appendPart(this.pointer, string, fromIndex, length);
  }

  /**
   * Inserts the given string at the given index.
   *
   * @param index  The index to insert at. This should be Java {@code char}-based index * in the
   *               given string.
   * @param string The string to insert.
   */
  public void insert(int index, String string) {
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
    Native.deleteChars(this.pointer, fromIndex, toIndex);
  }

  /**
   * Deletes the contents of this string between the given byte indices.
   *
   * @param fromIndex The byte index to delete from.
   * @param toIndex The byte index to delete to.
   */
  public void deleteBytes(int fromIndex, int toIndex) {
    Native.deleteBytes(this.pointer, fromIndex, toIndex);
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
    Native.replaceChars(this.pointer, fromIndex, toIndex, str);
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
    Native.replaceBytes(this.pointer, fromIndex, toIndex, str);
  }

  public UTF16String substrChars(int start) {
    return substrChars(start, length());
  }

  public UTF16String substrChars(int start, int end) {
    return new UTF16String(Native.substring_chars(this.pointer, start, end));
  }

  public UTF16String substrBytes(int start) {
    return substrBytes(start, byteLength());
  }

  public UTF16String substrBytes(int start, int end) {
    return new UTF16String(Native.substring_bytes(this.pointer, start, end));
  }

  public String substringChars(int start) {
    return substringChars(start, length());
  }

  public String substringChars(int start, int end) {
    return Native.subjstring_chars(this.pointer, start, end);
  }

  public String substringBytess(int start) {
    return substringBytes(start, length());
  }

  public String substringBytes(int start, int end) {
    return Native.subjstring_bytes(this.pointer, start, end);
  }

  public int length() {
    return Native.length(this.pointer);
  }

  public int byteLength() {
    return Native.byteLength(this.pointer);
  }

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

  @Override
  public int hashCode() {
    return Objects.hash(pointer);
  }

  private static class Native {
    static native long newUtf16String(String src);

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