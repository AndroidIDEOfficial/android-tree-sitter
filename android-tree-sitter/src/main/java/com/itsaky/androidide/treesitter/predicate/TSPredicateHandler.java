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

package com.itsaky.androidide.treesitter.predicate;

import com.itsaky.androidide.treesitter.TSQuery;
import com.itsaky.androidide.treesitter.TSQueryMatch;
import com.itsaky.androidide.treesitter.TSQueryPredicateStep;
import com.itsaky.androidide.treesitter.TSQueryPredicateStep.Type;
import java.util.List;

/**
 * Handles predicates specified in tree sitter query patterns.
 * @author Akash Yadav
 */
public interface TSPredicateHandler {

  /**
   * @return The names of the supported predicates (must not contain leading '#').
   */
  String[] getSupportedPredicates();

  /**
   * Handle the predicate from the query.
   *
   * @param query The tree sitter query.
   * @param match The query match.
   * @param args  The arguments provided to the predicate (the first element is always name of the
   *              predicate).
   * @return Whether the predicate was handled.
   */
  Result handle(
    TSQuery query,
    TSQueryMatch match,
    List<PredicateStep> args
  );

  /**
   * Predicate step for predicates.
   */
  class PredicateStep {

    /**
     * The type of the predicate step.
     */
    public final TSQueryPredicateStep.Type type;

    /**
     * The value for the predicate step. In case the type is {@link TSQueryPredicateStep.Type#Capture},
     * this will be the capture name. Otherwise, this will be the string value of the predicate step.
     */
    public final String value;

    public PredicateStep(Type type, String value) {
      this.type = type;
      this.value = value;
    }
  }

  /**
   * Result of a predicate.
   */
  enum Result {
    OK, ERR, UNHANDLED
  }
}
