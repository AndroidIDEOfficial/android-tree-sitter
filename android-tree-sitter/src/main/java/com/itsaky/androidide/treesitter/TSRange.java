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
public class TSRange {

  protected int startByte;
  protected int endByte;
  protected TSPoint startPoint;
  protected TSPoint endPoint;

  protected TSRange() {
  }

  protected TSRange(int startByte, int endByte, TSPoint startPoint, TSPoint endPoint) {
    this.startByte = startByte;
    this.endByte = endByte;
    this.startPoint = startPoint;
    this.endPoint = endPoint;
  }

  public static TSRange create(int startByte, int endByte, TSPoint startPoint, TSPoint endPoint) {
    return TSObjectFactoryProvider.getFactory()
      .createRange(startByte, endByte, startPoint, endPoint);
  }

  public int getStartByte() {
    return startByte;
  }

  public int getEndByte() {
    return endByte;
  }

  public TSPoint getStartPoint() {
    return startPoint;
  }

  public TSPoint getEndPoint() {
    return endPoint;
  }
}