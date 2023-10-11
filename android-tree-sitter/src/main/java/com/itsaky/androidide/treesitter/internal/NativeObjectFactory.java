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

package com.itsaky.androidide.treesitter.internal;

import static com.itsaky.androidide.treesitter.util.TSObjectFactoryProvider.getFactory;

import com.itsaky.androidide.treesitter.TSInputEdit;
import com.itsaky.androidide.treesitter.TSNode;
import com.itsaky.androidide.treesitter.TSPoint;
import com.itsaky.androidide.treesitter.TSQueryCapture;
import com.itsaky.androidide.treesitter.TSQueryMatch;
import com.itsaky.androidide.treesitter.TSQueryPredicateStep;
import com.itsaky.androidide.treesitter.TSRange;
import com.itsaky.androidide.treesitter.TSTreeCursorNode;

/**
 * Static methods which are called from JNI to create TS objects.
 * <p>
 * Try to define methods in this class such that they only have primitive types and String as their
 * parameters.
 *
 * @author Akash Yadav
 */
@SuppressWarnings("unused")
class NativeObjectFactory {

  static TSNode createNode(int context0, int context1, int context2, int context3, long id,
                           long tree
  ) {
    return getFactory().createNode(context0, context1, context2, context3, id, tree);
  }

  static TSTreeCursorNode createTreeCursorNode(String type, String name, int startByte, int endByte
  ) {
    return getFactory().createTreeCursorNode(type, name, startByte, endByte);
  }

  static TSPoint createPoint(int row, int column
  ) {
    return getFactory().createPoint(row, column);
  }

  static TSRange createRange(int startByte, int endByte, int startRow, int startColumn, int endRow,
                             int endColumn
  ) {
    final var start = createPoint(startRow, startColumn);
    final var end = createPoint(endRow, endColumn);
    return getFactory().createRange(startByte, endByte, start, end);
  }

  static TSRange[] createRangeArr(int size) {
    return getFactory().createRangeArr(size);
  }

  static TSInputEdit createInputEdit(int startByte, int oldEndByte, int newEndByte, int startRow,
                                     int startColumn, int oldEndRow, int oldEndColumn,
                                     int newEndRow, int newEndColumn
  ) {
    final var startPoint = createPoint(startRow, startColumn);
    final var oldEndPoint = createPoint(oldEndRow, oldEndColumn);
    final var newEndPoint = createPoint(newEndRow, newEndColumn);
    return getFactory().createInputEdit(startByte, oldEndByte, newEndByte, startPoint, oldEndPoint,
      newEndPoint);
  }

  static TSQueryMatch createQueryMatch(int id, int patternIndex, TSQueryCapture[] captures
  ) {
    return getFactory().createQueryMatch(id, patternIndex, captures);
  }

  static TSQueryCapture createQueryCapture(int index, int nodeContext0, int nodeContext1,
                                           int nodeContext2, int nodeContext3, long nodeId,
                                           long nodeTree
  ) {
    final var node = createNode(nodeContext0, nodeContext1, nodeContext2, nodeContext3, nodeId,
      nodeTree);
    return getFactory().createQueryCapture(node, index);
  }

  static TSQueryPredicateStep createQueryPredicateStep(int type, int valueId
  ) {
    return getFactory().createQueryPredicateStep(type, valueId);
  }

  static TSQueryPredicateStep[] createQueryPredicateStepArr(int size) {
    return getFactory().createQueryPredicateStepArr(size);
  }
}
