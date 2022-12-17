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
    return new UTF16String(Native.newString(source));
  }

  public static UTF16String newString(byte[] bytes) {
    return newString(bytes, 0, bytes.length);
  }

  public static UTF16String newString(byte[] bytes, int offset, int len) {
    return new UTF16String(Native.newStringBytes(bytes, offset, len));
  }

  private static class Native {
    static native long newString(String source);
    static native long newStringBytes(byte[] bytes, int off, int len);
  }
}
