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
 * The type of a {@link TSQuery} error.
 *
 * @author Akash Yadav
 */
public enum TSQueryError {
  None(0),
  Syntax(1),
  NodeType(2),
  Field(3),
  Capture(4),
  Structure(5),
  Language(6);

  private final int type;

  TSQueryError(int type) {
    this.type = type;
  }

  public static TSQueryError valueOf(int type) {
    for (TSQueryError value : values()) {
      if (value.type == type) {
        return value;
      }
    }
    return null;
  }
}