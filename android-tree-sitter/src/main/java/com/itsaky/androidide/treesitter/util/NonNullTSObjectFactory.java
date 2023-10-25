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

import com.itsaky.androidide.treesitter.TSInputEdit;
import com.itsaky.androidide.treesitter.TSLanguage;
import com.itsaky.androidide.treesitter.TSLookaheadIterator;
import com.itsaky.androidide.treesitter.TSNode;
import com.itsaky.androidide.treesitter.TSParser;
import com.itsaky.androidide.treesitter.TSPoint;
import com.itsaky.androidide.treesitter.TSQuery;
import com.itsaky.androidide.treesitter.TSQueryCapture;
import com.itsaky.androidide.treesitter.TSQueryCursor;
import com.itsaky.androidide.treesitter.TSQueryMatch;
import com.itsaky.androidide.treesitter.TSQueryPredicateStep;
import com.itsaky.androidide.treesitter.TSRange;
import com.itsaky.androidide.treesitter.TSTree;
import com.itsaky.androidide.treesitter.TSTreeCursor;
import com.itsaky.androidide.treesitter.TSTreeCursorNode;
import com.itsaky.androidide.treesitter.string.UTF16String;
import java.util.Objects;

/**
 * Implementation of {@link TSObjectFactory} which always returns non-null values.
 *
 * @author Akash Yadav
 */
class NonNullTSObjectFactory implements TSObjectFactory {

  private TSObjectFactory factory;

  NonNullTSObjectFactory(TSObjectFactory factory) {
    this.factory = factory;
  }

  void setFactory(TSObjectFactory factory) {
    synchronized (NonNullTSObjectFactory.class) {
      if (factory == null) {
        throw new IllegalArgumentException("TSObjectFactory cannot be null.");
      }
      this.factory = factory;
    }
  }

  TSObjectFactory getFactory() {
    synchronized (TSObjectFactoryProvider.class) {
      return this.factory;
    }
  }

  private static <T> T requireNonNull(T t) {
    return Objects.requireNonNull(t, "A TSObjectFactory must not return null values!");
  }

  @Override
  public TSInputEdit createInputEdit(int startByte, int oldEndByte, int newEndByte,
                                     TSPoint startPoint, TSPoint oldEndPoint, TSPoint newEndPoint
  ) {
    return requireNonNull(
      factory.createInputEdit(startByte, oldEndByte, newEndByte, startPoint, oldEndPoint,
        newEndPoint));
  }

  @Override
  public TSParser createParser(long parserPointer) {
    return requireNonNull(factory.createParser(parserPointer));
  }

  @Override
  public TSQuery createQuery(long queryPointer) {
    return requireNonNull(factory.createQuery(queryPointer));
  }

  @Override
  public TSQueryCursor createQueryCursor(long pointer) {
    return requireNonNull(factory.createQueryCursor(pointer));
  }

  @Override
  public TSPoint createPoint(int row, int column) {
    return requireNonNull(factory.createPoint(row, column));
  }

  @Override
  public TSRange createRange(int startByte, int endByte, TSPoint startPoint, TSPoint endPoint) {
    return requireNonNull(factory.createRange(startByte, endByte, startPoint, endPoint));
  }

  @Override
  public TSRange[] createRangeArr(int size) {
    return requireNonNull(factory.createRangeArr(size));
  }

  @Override
  public TSNode createNode(int context0, int context1, int context2, int context3, long id,
                           long treePointer
  ) {
    return requireNonNull(
      factory.createNode(context0, context1, context2, context3, id, treePointer));
  }

  @Override
  public TSTree createTree(long pointer) {
    return requireNonNull(factory.createTree(pointer));
  }

  @Override
  public TSTreeCursor createTreeCursor(long pointer) {
    return requireNonNull(factory.createTreeCursor(pointer));
  }

  @Override
  public TSQueryCapture createQueryCapture(TSNode node, int index) {
    return requireNonNull(factory.createQueryCapture(node, index));
  }

  @Override
  public TSQueryMatch createQueryMatch(int id, int patternIndex, TSQueryCapture[] captures) {
    return requireNonNull(factory.createQueryMatch(id, patternIndex, captures));
  }

  @Override
  public TSQueryPredicateStep createQueryPredicateStep(int type, int valueId) {
    return requireNonNull(factory.createQueryPredicateStep(type, valueId));
  }

  @Override
  public TSQueryPredicateStep[] createQueryPredicateStepArr(int size) {
    return requireNonNull(factory.createQueryPredicateStepArr(size));
  }

  @Override
  public TSTreeCursorNode createTreeCursorNode(String type, String name, int startByte, int endByte
  ) {
    return requireNonNull(factory.createTreeCursorNode(type, name, startByte, endByte));
  }

  @Override
  public TSLookaheadIterator createLookaheadIterator(long pointer) {
    return requireNonNull(factory.createLookaheadIterator(pointer));
  }

  @Override
  public TSLanguage createLanguage(String name, long[] pointers) {
    return requireNonNull(factory.createLanguage(name, pointers));
  }

  @Override
  public UTF16String createString(long pointer, boolean isSynchronized) {
    return requireNonNull(factory.createString(pointer, isSynchronized));
  }
}
