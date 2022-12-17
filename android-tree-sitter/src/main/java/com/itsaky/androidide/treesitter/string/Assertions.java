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
 * Assertions used in {@link UTF16String}.
 *
 * @author Akash Yadav
 */
class Assertions {

  private Assertions() {
    throw new UnsupportedOperationException();
  }

  public static void checkIndex(int index, int size) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("index " + index + " out of bounds, size = " + size);
    }
  }

  public static void checkStringRange(String str, int off, int len) {
    if (off < 0 || off + len > str.length()) {
      throw new StringIndexOutOfBoundsException("offset: " + off + ", len: " + len + ", actual length: " + str.length());
    }
  }
}
