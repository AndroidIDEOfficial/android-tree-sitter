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
 * InputEdit
 *
 * @noinspection unused
 */
public class TSInputEdit {

  protected int startByte;
  protected int oldEndByte;
  protected int newEndByte;
  protected TSPoint startPoint;
  protected TSPoint oldEndPoint;
  protected TSPoint newEndPoint;

  protected TSInputEdit() {
  }

  protected TSInputEdit(int startByte, int oldEndByte, int newEndByte, TSPoint startPoint,
                        TSPoint oldEndPoint, TSPoint newEndPoint
  ) {
    this.startByte = startByte;
    this.oldEndByte = oldEndByte;
    this.newEndByte = newEndByte;
    this.startPoint = startPoint;
    this.oldEndPoint = oldEndPoint;
    this.newEndPoint = newEndPoint;
  }

  public int getStartByte() {
    return startByte;
  }

  public int getOldEndByte() {
    return oldEndByte;
  }

  public int getNewEndByte() {
    return newEndByte;
  }

  public TSPoint getStartPoint() {
    return startPoint;
  }

  public TSPoint getOldEndPoint() {
    return oldEndPoint;
  }

  public TSPoint getNewEndPoint() {
    return newEndPoint;
  }

  public static TSInputEdit create(int startByte, int oldEndByte, int newEndByte,
                                   TSPoint startPoint, TSPoint oldEndPoint, TSPoint newEndPoint
  ) {
    return TSObjectFactoryProvider.getFactory()
      .createInputEdit(startByte, oldEndByte, newEndByte, startPoint, oldEndPoint, newEndPoint);
  }
}