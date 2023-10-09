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

import com.itsaky.androidide.treesitter.util.TSObjectFactoryProvider;

/**
 * @author Akash Yadav
 */
public class TSQueryPredicateStep {

  private Type cachedType = null;
  protected int type = -1;
  protected int valueId = -1;

  protected TSQueryPredicateStep() {
  }

  protected TSQueryPredicateStep(int type, int valueId) {
    this.type = type;
    this.valueId = valueId;
  }

  public static TSQueryPredicateStep create(int type, int valueId) {
    return TSObjectFactoryProvider.getFactory().createQueryPredicateStep(type, valueId);
  }

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