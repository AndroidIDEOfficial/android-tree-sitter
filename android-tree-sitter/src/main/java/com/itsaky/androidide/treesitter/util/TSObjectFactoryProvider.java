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

import com.itsaky.androidide.treesitter.DefaultObjectFactory;

/**
 * Provides instance of {@link TSObjectFactory}.
 *
 * @author Akash Yadav
 */
@SuppressWarnings("unused")
public final class TSObjectFactoryProvider {

  private static final NonNullTSObjectFactory sFactory = new NonNullTSObjectFactory(
    DefaultObjectFactory.getInstance());

  /**
   * Set the {@link TSObjectFactory} used to create tree sitter objects.
   *
   * @param factory The {@link TSObjectFactory} to use.
   * @throws IllegalArgumentException If the provided factory is null.
   */
  public static void setFactory(TSObjectFactory factory) {
    sFactory.setFactory(factory);
  }

  /**
   * Get the {@link TSObjectFactory} instance.
   *
   * @return The {@link TSObjectFactory}.
   */
  public static TSObjectFactory getFactory() {
    return sFactory.getFactory();
  }
}
