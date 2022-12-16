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
public class TSQueryPredicateStep {

  private Type cachedType = null;
  private int type = -1;
  private int valueId = -1;

  public Type getType() {
    if (cachedType == null) {
      cachedType = Type.forId(this.type);
    }
    return cachedType;
  }

  public int getValueId() {
    return valueId;
  }

  public enum Type {
    Done(0),
    Capture(1),
    String(2);

    private final int id;

    Type(int id) {
      this.id = id;
    }

    public static Type forId(int typeId) {
      for (Type value : values()) {
        if (value.id == typeId) {
          return value;
        }
      }
      return null;
    }
  }
}