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

package com.itsaky.androidide.treesitter;

import java.nio.charset.StandardCharsets;

/**
 * A tree-sitter language.
 *
 * @author Akash Yadav
 */
public class TSLanguage {

  final long pointer;

  /**
   * Create a new {@link TSLanguage} instance with the given pointer.
   *
   * @param pointer The pointer to the language implementation in C.
   */
  public TSLanguage(long pointer) {
    this.pointer = pointer;
  }

  /** Get the number of distinct node types in the language. */
  public int getSymbolCount() {
    return Native.symCount(this.pointer);
  }

  public int getFieldCount() {
    return Native.fldCount(this.pointer);
  }

  public String getSymbolName(int symbol) {
    return Native.symName(this.pointer, symbol);
  }

  public int getSymbolForTypeString(String name, boolean isNamed) {
    final var bytes = name.getBytes(StandardCharsets.UTF_8);
    return Native.symForName(this.pointer, bytes, bytes.length, isNamed);
  }

  public String getFieldNameForId(int id) {
    return Native.fldNameForId(this.pointer, id);
  }

  public int getFieldIdForName(String name) {
    final var bytes = name.getBytes(StandardCharsets.UTF_8);
    return Native.fldIdForName(this.pointer, bytes, bytes.length);
  }

  public TSSymbolType getSymbolType(int symbol) {
    return TSSymbolType.forId(Native.symType(this.pointer, symbol));
  }

  public int getLanguageVersion() {
    return Native.langVer(this.pointer);
  }

  private static class Native {
    private static native int symCount(long ptr);

    private static native int fldCount(long ptr);

    private static native int symForName(long ptr, byte[] name, int length, boolean named);

    private static native String symName(long lngPtr, int sym);

    private static native String fldNameForId(long ptr, int id);

    private static native int fldIdForName(long ptr, byte[] name, int length);

    private static native int symType(long ptr, int sym);

    private static native int langVer(long ptr);
  }
}