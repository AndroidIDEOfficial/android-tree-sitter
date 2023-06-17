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

package com.itsaky.androidide.treesitter.util;

/**
 * Pair of two objects.
 *
 * @author Akash Yadav
 */
public class Pair<F, S> {

  public final F first;
  public final S second;

  private Pair(F first, S second) {
    this.first = first;
    this.second = second;
  }

  /**
   * Creates a new pair of the given objects.
   *
   * @param first  The first object.
   * @param second The second object.
   * @param <_F>   The first object type.
   * @param <_S>   The second object type.
   * @return The pair.
   */
  public static <_F, _S> Pair<_F, _S> of(_F first, _S second) {
    return new Pair<>(first, second);
  }
}
