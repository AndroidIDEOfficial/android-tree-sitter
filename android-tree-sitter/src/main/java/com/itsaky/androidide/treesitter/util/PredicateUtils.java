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

import com.itsaky.androidide.treesitter.TSQueryPredicateStep;
import com.itsaky.androidide.treesitter.predicate.TSPredicateHandler.PredicateStep;
import java.util.List;

/**
 * Utility class for predicates.
 *
 * @author Akash Yadav
 */
public final class PredicateUtils {

  private PredicateUtils() {
    throw new UnsupportedOperationException();
  }

  /**
   * Check whether the arguments match the expected types.
   *
   * @param expected The expected types.
   * @param args     The arguments.
   * @return Whether the arguments match the expected types.
   */
  public static boolean matchesArgTypes(TSQueryPredicateStep.Type[] expected,
                                        List<PredicateStep> args
  ) {
    if (expected.length != args.size()) {
      return false;
    }

    for (int i = 0; i < expected.length; i++) {
      if (expected[i] != args.get(i).type) {
        return false;
      }
    }

    return true;
  }
}
