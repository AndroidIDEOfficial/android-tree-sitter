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

import com.itsaky.androidide.treesitter.util.TSObjectFactory;

/**
 * Default implementation of {@link TSObjectFactory} which always creates new instances of objects.
 *
 * @author Akash Yadav
 */
public class DefaultObjectFactory implements TSObjectFactory {

  private static DefaultObjectFactory sInstance;

  public static DefaultObjectFactory getInstance() {
    if (sInstance == null) {
      sInstance = new DefaultObjectFactory();
    }
    return sInstance;
  }

  private DefaultObjectFactory() {
  }

  @Override
  public TSInputEdit createInputEdit(int startByte, int oldEndByte, int newEndByte,
                                     TSPoint startPoint, TSPoint oldEndPoint, TSPoint newEndPoint
  ) {
    return new TSInputEdit(startByte, oldEndByte, newEndByte, startPoint, oldEndPoint, newEndPoint);
  }

  @Override
  public TSParser createParser(long parserPointer) {
    return new TSParser(parserPointer);
  }

  @Override
  public TSQuery createQuery(long queryPointer) {
    return new TSQuery(queryPointer);
  }

  @Override
  public TSQueryCursor createQueryCursor(long pointer) {
    return new TSQueryCursor(pointer);
  }

  @Override
  public TSPoint createPoint(int row, int column) {
    return new TSPoint(row, column);
  }

  @Override
  public TSRange createRange(int startByte, int endByte, TSPoint startPoint, TSPoint endPoint) {
    return new TSRange(startByte, endByte, startPoint, endPoint);
  }

  @Override
  public TSRange[] createRangeArr(int size) {
    return new TSRange[size];
  }

  @Override
  public TSTree createTree(long pointer) {
    return new TSTree(pointer);
  }

  @Override
  public TSTreeCursor createTreeCursor(long pointer) {
    return new TSTreeCursor(pointer);
  }

  @Override
  public TSNode createNode(int context0, int context1, int context2, int context3, long id,
                           long treePointer
  ) {
    return new TSNode(context0, context1, context2, context3, id, treePointer);
  }

  @Override
  public TSQueryCapture createQueryCapture(TSNode node, int index
  ) {
    return new TSQueryCapture(node, index);
  }

  @Override
  public TSQueryMatch createQueryMatch(int id, int patternIndex, TSQueryCapture[] captures) {
    return new TSQueryMatch(id, patternIndex, captures);
  }

  @Override
  public TSQueryPredicateStep createQueryPredicateStep(int type, int valueId) {
    return new TSQueryPredicateStep(type, valueId);
  }

  @Override
  public TSQueryPredicateStep[] createQueryPredicateStepArr(int size) {
    return new TSQueryPredicateStep[size];
  }

  @Override
  public TSTreeCursorNode createTreeCursorNode(String type, String name, int startByte, int endByte
  ) {
    return new TSTreeCursorNode(type, name, startByte, endByte);
  }

  @Override
  public TSLookaheadIterator createLookaheadIterator(long pointer) {
    return new TSLookaheadIterator(pointer);
  }

  @Override
  public TSLanguage createLanguage(String name, long[] pointers) {
    return new TSLanguage(name, pointers);
  }
}
