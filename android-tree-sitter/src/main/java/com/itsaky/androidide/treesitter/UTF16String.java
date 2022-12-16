package com.itsaky.androidide.treesitter;

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Allows usage of <code>std::string</code> in Java.
 *
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
  public void append(String string, int fromIndex, int length) {}

  /**
   * Inserts the given string at the given index.
   *
   * @param string The string to insert.
   * @param index The index to insert at. This should be Java {@code char}-based index * in the
   *     given string.
   */
  public void insert(String string, int index) {}

  /**
   * Deletes the contents of this {@link UTF16String} between the given indices. The indices must be
   * Java {@code char} based.
   *
   * @param fromIndex The index to delete from.
   * @param toIndex The index to delete to.
   */
  public void delete(int fromIndex, int toIndex) {}

  /**
   * Deletes the contents of this string between the given byte indices.
   *
   * @param fromIndex The byte index to delete from.
   * @param toIndex The byte index to delete to.
   */
  public void deleteBytes(int fromIndex, int toIndex) {}

  /**
   * Replaces the contents of this {@link UTF16String} between the given indices with the given
   * string. The indices should be Java {@code char}-based indices in the given string.
   *
   * @param str The string to replace with.
   * @param fromIndex The index to replace from.
   * @param toIndex The index to replace to.
   */
  public void replace(String str, int fromIndex, int toIndex) {}

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

    static native String toString(long pointer);

    static native int length(long pointer);

    static native int byteLength(long pointer);

    static native void erase(long pointer);
  }
}
