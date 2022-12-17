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

/**
 * Type of TSSymbol.
 *
 * @author Akash Yadav
 */
public enum TSSymbolType {
  REGULAR(0),
  ANONYMOUS(1),
  AUXILIARY(2);

  final int id;

  TSSymbolType(int id) {
    this.id = id;
  }

  /**
   * Get symbol type for the given type id.
   *
   * @param id The id.
   */
  public static TSSymbolType forId(int id) {
    for (TSSymbolType symbolType : values()) {
      if (symbolType.id == id) {
        return symbolType;
      }
    }
    return null;
  }
}