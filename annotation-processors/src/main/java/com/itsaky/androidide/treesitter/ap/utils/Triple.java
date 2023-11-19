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

package com.itsaky.androidide.treesitter.ap.utils;

/**
 * @author Akash Yadav
 */
public class Triple<F, S, T> {

  public final F first;
  public final S second;
  public final T third;

  public Triple(F first, S second, T third) {
    this.first = first;
    this.second = second;
    this.third = third;
  }

  public static <F, S, T> Triple<F, S, T> of (F first, S second, T third) {
    return new Triple<>(first, second, third);
  }

  public static <T> Triple<T, T, T> of (T t) {
    return new Triple<>(t, t, t);
  }
}
