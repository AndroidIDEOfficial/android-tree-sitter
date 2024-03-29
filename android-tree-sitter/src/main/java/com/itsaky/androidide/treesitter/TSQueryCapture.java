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
import java.util.Objects;

/**
 * @author Akash Yadav
 */
public class TSQueryCapture {

  protected TSNode node;
  protected int index;

  protected TSQueryCapture() {
  }

  protected TSQueryCapture(TSNode node, int index) {
    this.node = node;
    this.index = index;
  }

  public static TSQueryCapture create(TSNode node, int index) {
    Objects.requireNonNull(node, "TSNode cannot be null");
    return TSObjectFactoryProvider.getFactory().createQueryCapture(node, index);
  }

  public TSNode getNode() {
    return node;
  }

  public int getIndex() {
    return index;
  }
}