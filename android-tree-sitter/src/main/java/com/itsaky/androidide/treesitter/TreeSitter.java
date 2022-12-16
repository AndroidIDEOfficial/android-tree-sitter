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

package com.itsaky.androidide.treesitter;

/**
 * @author Akash Yadav
 */
public class TreeSitter {

  /**
   * The latest ABI version that is supported by the current version of the library. When Languages
   * are generated by the Tree-sitter CLI, they are assigned an ABI version number that corresponds
   * to the current CLI version. The Tree-sitter library is generally backwards-compatible with
   * languages generated using older CLI versions, but is not forwards-compatible.
   */
  public static native int getLanguageVersion();

  /** The earliest ABI version that is supported by the current version of the library. */
  public static native int getMinimumCompatibleLanguageVersion();
}