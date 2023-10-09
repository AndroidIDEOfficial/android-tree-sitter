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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TSNode {

  public int context0;
  public int context1;
  public int context2;
  public int context3;
  public long id;
  public long tree;

  private TSTree mTree;

  protected TSNode() {
  }

  protected TSNode(int context0, int context1, int context2, int context3, long id, long tree) {
    this.context0 = context0;
    this.context1 = context1;
    this.context2 = context2;
    this.context3 = context3;
    this.id = id;
    this.tree = tree;
  }

  public static TSNode create(int context0, int context1, int context2, int context3, long id,
                              long tree
  ) {
    return TSObjectFactoryProvider.getFactory()
      .createNode(context0, context1, context2, context3, id, tree);
  }

  /**
   * Get the syntax tree that this node is associated with.
   *
   * @return The syntax tree.
   */
  public TSTree getTree() {
    if (mTree == null) {
      mTree = TSTree.create(this.tree);
    }
    return mTree;
  }

  /**
   * Get the child of the given node at the child index.
   *
   * @param index The index of the child.
   * @return The child at the child index.
   */
  public TSNode getChild(int index) {
    final var count = getChildCount();
    if (index < 0 || index >= count) {
      throw new IndexOutOfBoundsException("count=" + count + ", index=" + index);
    }

    return getChildAt(index);
  }

  /**
   * Get the named child of the given node at the given index.
   *
   * @param index The index of the named child.
   * @return The named child node at the given index.
   */
  public TSNode getNamedChild(int index) {
    final var count = getNamedChildCount();
    if (index < 0 || index >= count) {
      throw new IndexOutOfBoundsException("count=" + count + ", index=" + index);
    }

    return getNamedChildAt(index);
  }

  /**
   * Find the child node of the given node by field name.
   *
   * @param fieldName The field name.
   * @return The found node.
   */
  public TSNode getChildByFieldName(String fieldName) {
    final var bytes = fieldName.getBytes(StandardCharsets.UTF_8);
    return getChildByFieldName(bytes, bytes.length);
  }

  public TSTreeCursor walk() {
    return TSTreeCursor.create(this);
  }

  public TSNode findNodeWithType(final String type, final boolean namedOnly) {
    for (int i = 0; i < (namedOnly ? getNamedChildCount() : getChildCount()); i++) {
      final var child = namedOnly ? getNamedChild(i) : getChild(i);
      if (child.isNull()) {
        continue;
      }

      if (type.equals(child.getType())) {
        return child;
      }
    }

    return null;
  }

  public List<TSNode> findChildrenWithType(final String type, final boolean reverseSearch,
                                           final boolean namedOnly
  ) {
    if (reverseSearch) {
      return findChildrenWithTypeReverse(type, namedOnly);
    }

    final var result = new ArrayList<TSNode>();
    for (int i = 0; i < (namedOnly ? getNamedChildCount() : getChildCount()); i++) {
      final var child = namedOnly ? getNamedChild(i) : getChild(i);
      if (child.isNull()) {
        continue;
      }

      if (Objects.equals(type, child.getType())) {
        result.add(child);
      }
    }

    return result;
  }

  public List<TSNode> findChildrenWithTypeReverse(final String type, final boolean namedOnly) {
    final var result = new ArrayList<TSNode>();
    for (int i = (namedOnly ? getNamedChildCount() : getChildCount()) - 1; i > 0; --i) {
      final var child = namedOnly ? getNamedChild(i) : getChild(i);
      if (child.isNull()) {
        continue;
      }

      if (Objects.equals(type, child.getType())) {
        result.add(child);
      }
    }

    return result;
  }

  @Override
  public String toString() {
    return "TSNode{" + "id=" + id + ", type=" + (isNull() ? "<null>" : getType()) + '}';
  }

  /**
   * Get the immediate parent node of this node.
   *
   * @return The parent node.
   */
  public native TSNode getParent();

  private native TSNode getChildAt(int index);

  private native TSNode getNamedChildAt(int index);

  private native TSNode getChildByFieldName(byte[] bytes, int length);

  /**
   * Get the field name for node's child at the given index, where zero represents * the first
   * child.
   *
   * @param childIndex The index of the child.
   * @return The field name for the child or <code>null</code>.
   */
  public native String getFieldNameForChild(int childIndex);

  /**
   * Get the child for the given field id.
   *
   * @param fieldId The field id.
   * @return The child node. Maybe <code>null</code>.
   */
  public native TSNode getChildByFieldId(int fieldId);

  /**
   * Get the next sibling node of this node.
   *
   * @return The next sibling node.
   */
  public native TSNode getNextSibling();

  /**
   * Get the previous sibling node of this node.
   *
   * @return The previous sibling node.
   */
  public native TSNode getPreviousSibling();

  /**
   * Get the next named sibling node of this node.
   *
   * @return The next named sibling node.
   */
  public native TSNode getNextNamedSibling();

  /**
   * Get the previous named sibling node of this node.
   *
   * @return The previous named sibling node.
   */
  public native TSNode getPreviousNamedSibling();

  /**
   * Get the node's first child that extends beyond the given byte offset.
   *
   * @param byteOffset The byte offsest.
   * @return The first child beyond the byte offset.
   */
  public native TSNode getFirstChildForByte(int byteOffset);

  /**
   * Get the node's first named child that extends beyond the given byte offset.
   *
   * @param byteOffset The byte offsest.
   * @return The first named child beyond the byte offset.
   */
  public native TSNode getFirstNamedChildForByte(int byteOffset);

  /**
   * Get the smallest node within this node that spans the given range of bytes or (row, column)
   * positions.
   */
  public native TSNode getDescendantForByteRange(int start, int end);

  /**
   * @see #getDescendantForByteRange(int, int)
   */
  public native TSNode getDescendantForPointRange(TSPoint start, TSPoint end);

  /**
   * Get the smallest node within this node that spans the given range of bytes or (row, column)
   * positions.
   */
  public native TSNode getNamedDescendantForByteRange(int start, int end);

  /**
   * @see #getNamedDescendantForByteRange(int, int)
   */
  public native TSNode getNamedDescendantForPointRange(TSPoint start, TSPoint end);

  /**
   * Check if this node and the other node are identical.
   *
   * @param another The node to check.
   */
  public native boolean isEqualTo(TSNode another);

  /**
   * Get the number of children of the node.
   *
   * @return The number of children.
   */
  public native int getChildCount();

  /**
   * Get the number of 'named' child nodes in the node.
   */
  public native int getNamedChildCount();

  /**
   * Get the end byte of this node.
   *
   * @return End byte of node.
   */
  public native int getEndByte();

  /**
   * Get the string representation of this node.
   *
   * @return The string representation of the node.
   */
  public native String getNodeString();

  /**
   * Get the start byte of this node.
   *
   * @return Start byte of node.
   */
  public native int getStartByte();

  /**
   * Get the start position of this node.
   *
   * @return The start position.
   */
  public native TSPoint getStartPoint();

  /**
   * Get the end position of this node.
   *
   * @return The end position.
   */
  public native TSPoint getEndPoint();

  /**
   * Get the type of this node.
   *
   * @return The type of the node.
   */
  public native String getType();

  /**
   * Get the node's type as a numerical id.
   *
   * @return The node's type as a numerical id.
   */
  public native int getSymbol();

  /**
   * Check if the node is null.
   */
  public native boolean isNull();

  /**
   * Check if the node is *named*. Named nodes correspond to named rules in the grammar, whereas
   * *anonymous* nodes correspond to string literals in the grammar.
   */
  public native boolean isNamed();

  /**
   * Check if the node is *extra*. Extra nodes represent things like comments, which are not
   * required the grammar, but can appear anywhere.
   */
  public native boolean isExtra();

  /**
   * Check if the node is *missing*. Missing nodes are inserted by the parser in order to recover
   * from certain kinds of syntax errors.
   */
  public native boolean isMissing();

  /**
   * Check if the given node has been edited.
   */
  public native boolean hasChanges();

  /**
   * Check if the node is a syntax error or contains any syntax errors.
   */
  public native boolean hasErrors();

  /**
   * Check if the node is an error.
   */
  public native boolean isError();

  /**
   * Get this node's parse state.
   */
  public native short getParseState();
}