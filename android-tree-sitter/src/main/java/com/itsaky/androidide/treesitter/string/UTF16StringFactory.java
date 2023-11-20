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

import com.itsaky.androidide.treesitter.annotations.GenerateNativeHeaders;
import com.itsaky.androidide.treesitter.util.TSObjectFactoryProvider;
import dalvik.annotation.optimization.FastNative;

/**
 * Provides APIs to create instances of {@link UTF16String}.
 *
 * @author Akash Yadav
 */
public class UTF16StringFactory {

  private UTF16StringFactory() {
    throw new UnsupportedOperationException();
  }

  public static UTF16String newString() {
    return newString("");
  }

  public static UTF16String newString(String source) {
    return newString(source, false);
  }

  public static UTF16String newString(String source, boolean isSynchronized) {
    return createString(Native.newString(source), isSynchronized);
  }

  public static UTF16String newString(byte[] bytes) {
    return newString(bytes, false);
  }

  public static UTF16String newString(byte[] bytes, boolean isSynchronized) {
    return newString(bytes, 0, bytes.length, isSynchronized);
  }

  public static UTF16String newString(byte[] bytes, int offset, int len) {
    return newString(bytes, offset, len, false);
  }

  public static UTF16String newString(byte[] bytes, int offset, int len, boolean isSynchronized) {
    return createString(newString0(bytes, offset, len), isSynchronized);
  }

  private static long newString0(byte[] bytes, int offset, int len) {
    Assertions.checkIndex(offset, bytes.length);
    Assertions.checkUpperBound(offset + len, bytes.length);
    return Native.newStringBytes(bytes, offset, len);
  }

  static UTF16String createString(long pointer) {
    return createString(pointer, false);
  }

  static UTF16String createString(long pointer, boolean isSynchronized) {
    return TSObjectFactoryProvider.getFactory().createString(pointer, isSynchronized);
  }

  @GenerateNativeHeaders(fileName = "utf16string_factory")
  private static class Native {

    static {
      registerNatives();
    }

    @FastNative
    static native void registerNatives();

    @FastNative
    static native long newString(String source);

    @FastNative
    static native long newStringBytes(byte[] bytes, int off, int len);
  }
}
