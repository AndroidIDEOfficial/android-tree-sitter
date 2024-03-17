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
import com.itsaky.androidide.treesitter.TSQueryMatch.Metadata;
import com.itsaky.androidide.treesitter.TSQueryPredicateStep;
import com.itsaky.androidide.treesitter.TSQueryPredicateStep.Type;
import com.itsaky.androidide.treesitter.util.PredicateUtils;
import java.util.List;

/**
 * Handles the <code>#set!</code> directive in queries.
 *
 * @author Akash Yadav
 */
public class SetDirectiveHandler implements TSPredicateHandler {

  public static final String DIRECTIVE = "set!";
  public static final String[] SUPPORTED_PREDICATES = {DIRECTIVE};

  public static final TSQueryPredicateStep.Type[] PARAMETERS_1 = {Type.String, // set!
    Type.String, // key
    Type.String, // value
    Type.Done};

  public static final TSQueryPredicateStep.Type[] PARAMETERS_2 = {Type.String, // set!
    Type.Capture, // @capture
    Type.String,  // key
    Type.String,  // value
    Type.Done};

  @Override
  public String[] getSupportedPredicates() {
    return SUPPORTED_PREDICATES;
  }

  @Override
  public Result handle(TSQuery query, TSQueryMatch match, List<PredicateStep> args
  ) {
    if (!DIRECTIVE.equals(args.get(0).value)) {
      return Result.UNHANDLED;
    }

    final var matchMetadata = match.getMetadata();
    if (PredicateUtils.matchesArgTypes(PARAMETERS_1, args)) {
      final var key = args.get(1).value;
      final var value = args.get(2).value;
      if (key != null) {
        matchMetadata.putString(key, value);
        return Result.OK;
      }

      return Result.ERR;
    }

    if (PredicateUtils.matchesArgTypes(PARAMETERS_2, args)) {
      final var capture = args.get(1).value;
      final var key = args.get(2).value;
      final var value = args.get(3).value;
      if (capture != null && key != null) {
        var captureMeta = matchMetadata.getCaptureMetadata(capture);
        if (captureMeta == null) {
          captureMeta = new Metadata();
        }
        captureMeta.putString(key, value);
        matchMetadata.putCaptureMetadata(capture, captureMeta);
        return Result.OK;
      }

      return Result.ERR;
    }

    return Result.UNHANDLED;
  }
}
