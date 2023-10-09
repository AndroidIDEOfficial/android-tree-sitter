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
public class TSQueryMatch {

  protected int id;
  protected int patternIndex;
  protected TSQueryCapture[] captures;

  protected TSQueryMatch() {
  }

  protected TSQueryMatch(int id, int patternIndex, TSQueryCapture[] captures) {
    this.id = id;
    this.patternIndex = patternIndex;
    this.captures = captures;
  }

  public static TSQueryMatch create(int id, int patternIndex, TSQueryCapture[] captures) {
    return TSObjectFactoryProvider.getFactory().createQueryMatch(id, patternIndex, captures);
  }

  public int getId() {
    return id;
  }

  public int getPatternIndex() {
    return patternIndex;
  }

  public TSQueryCapture[] getCaptures() {
    return captures;
  }

  public TSQueryCapture getCapture(int index) {
    return captures[index];
  }
}