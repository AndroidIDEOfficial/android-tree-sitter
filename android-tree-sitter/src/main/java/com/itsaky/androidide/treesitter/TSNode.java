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

import com.itsaky.androidide.treesitter.annotations.GenerateNativeHeaders;
import com.itsaky.androidide.treesitter.util.TSObjectFactoryProvider;
import dalvik.annotation.optimization.FastNative;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TSNode extends TSNativeObject {

  protected int context0; // start byte
  protected int context1; // start point row
  protected int context2; // start point column
  protected int context3; // alias
  protected long id;
  protected long tree;

  protected TSTree mTree;

  protected TSNode() {
    super(0);
  }

  protected TSNode(int context0, int context1, int context2, int context3, long id, long tree) {
    super(id);
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

  public int getContext0() {
    return context0;
  }

  public int getContext1() {
    return context1;
  }

  public int getContext2() {
    return context2;
  }

  public int getContext3() {
    return context3;
  }

  public long getNodeId() {
    return id;
  }

  /**
   * Get the syntax tree that this node is associated with.
   *
   * @return The syntax tree.
   */
  public synchronized TSTree getTree() {
    checkAccess();
    if (mTree == null) {
      mTree = TSTree.create(this.tree);
    }
    return mTree;
  }

  /**
   * Edit the node to keep it in-sync with source code that has been edited.
   * <p>
   * This function is only rarely needed. When you edit a syntax tree with the
   * {@link TSTree#edit(TSInputEdit)} function, all of the nodes that you retrieve from the tree
   * afterward will already reflect the edit. You only need to use this method when you have a
   * {@link TSNode} instance that you want to keep and continue to use after an edit.
   */
  public void edit(TSInputEdit edit) {
    Objects.requireNonNull(edit, "TSInputEdit cannot be null");
    checkAccess();
    getTree().checkAccess();
    Native.edit(this, edit);
  }

  /**
   * Get this node's language.
   */
  public TSLanguage getLanguage() {
    checkAccess();
    final var lang = Native.getLanguage(this);
    if (lang == 0) {
      return null;
    }

    return TSLanguageCache.get(lang);
  }

  /**
   * Get the node's type as it appears in the grammar ignoring aliases.
   */
  public String getGrammarType() {
    checkAccess();
    return Native.getGrammarType(this);
  }

  /**
   * Get the child of the given node at the child index.
   *
   * @param index The index of the child.
   * @return The child at the child index.
   */
  public TSNode getChild(int index) {
    checkAccess();
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
    checkAccess();
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
    checkAccess();
    final var bytes = fieldName.getBytes(StandardCharsets.UTF_8);
    return getChildByFieldName(bytes, bytes.length);
  }

  public TSTreeCursor walk() {
    checkAccess();
    return TSTreeCursor.create(this);
  }

  public TSNode findNodeWithType(final String type, final boolean namedOnly) {
    checkAccess();
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
    checkAccess();
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
    checkAccess();
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
    final var canAccess = canAccess();
    final var isNull = canAccess && isNull();
    final var type = canAccess ? getType() : "<unknown>";
    return "TSNode{" + "id=" + id + ", canAccess=" + canAccess + ", type=" +
      (isNull ? "<null>" : type) + '}';
  }

  /**
   * Get the immediate parent node of this node.
   *
   * @return The parent node.
   */
  public TSNode getParent() {
    checkAccess();
    return Native.getParent(this);
  }

  private TSNode getChildAt(int index) {
    checkAccess();
    getTree().checkAccess();
    return Native.getChildAt(this, index);
  }

  private TSNode getNamedChildAt(int index) {
    checkAccess();
    getTree().checkAccess();
    return Native.getNamedChildAt(this, index);
  }

  private TSNode getChildByFieldName(byte[] bytes, int length) {
    checkAccess();
    getTree().checkAccess();
    return Native.getChildByFieldName(this, bytes, length);
  }

  /**
   * Get the field name for node's child at the given index, where zero represents * the first
   * child.
   *
   * @param childIndex The index of the child.
   * @return The field name for the child or <code>null</code>.
   */
  public String getFieldNameForChild(int childIndex) {
    checkAccess();
    getTree().checkAccess();
    return Native.getFieldNameForChild(this, childIndex);
  }

  /**
   * Get the child for the given field id.
   *
   * @param fieldId The field id.
   * @return The child node. Maybe <code>null</code>.
   */
  public TSNode getChildByFieldId(int fieldId) {
    checkAccess();
    getTree().checkAccess();
    return Native.getChildByFieldId(this, fieldId);
  }

  /**
   * Get the next sibling node of this node.
   *
   * @return The next sibling node.
   */
  public TSNode getNextSibling() {
    checkAccess();
    getTree().checkAccess();
    return Native.getNextSibling(this);
  }

  /**
   * Get the previous sibling node of this node.
   *
   * @return The previous sibling node.
   */
  public TSNode getPreviousSibling() {
    checkAccess();
    getTree().checkAccess();
    return Native.getPreviousSibling(this);
  }

  /**
   * Get the next named sibling node of this node.
   *
   * @return The next named sibling node.
   */
  public TSNode getNextNamedSibling() {
    checkAccess();
    getTree().checkAccess();
    return Native.getNextNamedSibling(this);
  }

  /**
   * Get the previous named sibling node of this node.
   *
   * @return The previous named sibling node.
   */
  public TSNode getPreviousNamedSibling() {
    checkAccess();
    getTree().checkAccess();
    return Native.getPreviousNamedSibling(this);
  }

  /**
   * Get the node's first child that extends beyond the given byte offset.
   *
   * @param byteOffset The byte offsest.
   * @return The first child beyond the byte offset.
   */
  public TSNode getFirstChildForByte(int byteOffset) {
    checkAccess();
    getTree().checkAccess();
    return Native.getFirstChildForByte(this, byteOffset);
  }

  /**
   * Get the node's first named child that extends beyond the given byte offset.
   *
   * @param byteOffset The byte offsest.
   * @return The first named child beyond the byte offset.
   */
  public TSNode getFirstNamedChildForByte(int byteOffset) {
    checkAccess();
    getTree().checkAccess();
    return Native.getFirstNamedChildForByte(this, byteOffset);
  }

  /**
   * Get the node's number of descendants, including one for the node itself.
   */
  public int getDescendantCount() {
    checkAccess();
    getTree().checkAccess();
    return Native.getDescendantCount(this);
  }

  /**
   * Get the smallest node within this node that spans the given range of bytes or (row, column)
   * positions.
   */
  public TSNode getDescendantForByteRange(int start, int end) {
    checkAccess();
    getTree().checkAccess();
    return Native.getDescendantForByteRange(this, start, end);
  }

  /**
   * @see #getDescendantForByteRange(int, int)
   */
  public TSNode getDescendantForPointRange(TSPoint start, TSPoint end) {
    checkAccess();
    getTree().checkAccess();
    return Native.getDescendantForPointRange(this, start, end);
  }

  /**
   * Get the smallest node within this node that spans the given range of bytes or (row, column)
   * positions.
   */
  public TSNode getNamedDescendantForByteRange(int start, int end) {
    checkAccess();
    getTree().checkAccess();
    return Native.getNamedDescendantForByteRange(this, start, end);
  }

  /**
   * @see #getNamedDescendantForByteRange(int, int)
   */
  public TSNode getNamedDescendantForPointRange(TSPoint start, TSPoint end) {
    checkAccess();
    getTree().checkAccess();
    return Native.getNamedDescendantForPointRange(this, start, end);
  }

  /**
   * Check if this node and the other node are identical.
   *
   * @param another The node to check.
   */
  public boolean isEqualTo(TSNode another) {
    Objects.requireNonNull(another, "TSNode (another) cannot be null");
    checkAccess();
    getTree().checkAccess();
    return Native.isEqualTo(this, another);
  }

  /**
   * Get the number of children of the node.
   *
   * @return The number of children.
   */
  public int getChildCount() {
    checkAccess();
    getTree().checkAccess();
    return Native.getChildCount(this);
  }

  /**
   * Get the number of 'named' child nodes in the node.
   */
  public int getNamedChildCount() {
    checkAccess();
    getTree().checkAccess();
    return Native.getNamedChildCount(this);
  }

  /**
   * Get the string representation of this node.
   *
   * @return The string representation of the node.
   */
  public String getNodeString() {
    checkAccess();
    getTree().checkAccess();
    return Native.getNodeString(this);
  }

  /**
   * Get the start byte of this node. This returns the <code>context[0]</code> value.
   *
   * @return Start byte of node.
   * @see #getStartByteNative()
   */
  public int getStartByte() {
    // ts_node_start_byte simply returns context[0]
    return context0;
//    checkAccess();
//    return Native.getStartByte(this);
  }

  /**
   * Get the start byte of this node from the native TSNode object.
   *
   * @return Start byte of the node.
   */
  public int getStartByteNative() {
    checkAccess();
    return Native.getStartByte(this);
  }

  /**
   * Get the end byte of this node.
   *
   * @return End byte of node.
   */
  public int getEndByte() {
    checkAccess();
    // tree is not accessed here
    return Native.getEndByte(this);
  }

  /**
   * Get the start position of this node. This uses the <code>context[1]</code> and
   * <code>context[2]</code> values to create the {@link TSPoint} instance.
   *
   * @return The start position.
   * @see #getStartPointNative()
   */
  public TSPoint getStartPoint() {
    // ts_node_start_point simply returns TSPoint { .row = context[1], .column = context[2] }
    return TSPoint.create(/*row*/context1, /*column*/context2);
//    checkAccess();
//    return Native.getStartPoint(this);
  }

  /**
   * Get the start point of this node from the native TSNode object.
   *
   * @return Start point of the node.
   */
  public TSPoint getStartPointNative() {
    checkAccess();
    return Native.getStartPoint(this);
  }

  /**
   * Get the end position of this node.
   *
   * @return The end position.
   */
  public TSPoint getEndPoint() {
    checkAccess();
    // tree is not accessed here
    return Native.getEndPoint(this);
  }

  /**
   * Get the type of this node.
   *
   * @return The type of the node.
   */
  public String getType() {
    checkAccess();
    return Native.getType(this);
  }

  /**
   * Get the node's type as a numerical id.
   *
   * @return The node's type as a numerical id.
   */
  public int getSymbol() {
    checkAccess();
    return Native.getSymbol(this);
  }

  /**
   * Check if the node is null.
   */
  public boolean isNull() {
    checkAccess();
    return Native.isNull(this);
  }

  /**
   * Check if the node is *named*. Named nodes correspond to named rules in the grammar, whereas
   * *anonymous* nodes correspond to string literals in the grammar.
   */
  public boolean isNamed() {
    checkAccess();
    return Native.isNamed(this);
  }

  /**
   * Check if the node is *extra*. Extra nodes represent things like comments, which are not
   * required the grammar, but can appear anywhere.
   */
  public boolean isExtra() {
    checkAccess();
    return Native.isExtra(this);
  }

  /**
   * Check if the node is *missing*. Missing nodes are inserted by the parser in order to recover
   * from certain kinds of syntax errors.
   */
  public boolean isMissing() {
    checkAccess();
    return Native.isMissing(this);
  }

  /**
   * Check if the given node has been edited.
   */
  public boolean hasChanges() {
    checkAccess();
    return Native.hasChanges(this);
  }

  /**
   * Check if the node is a syntax error or contains any syntax errors.
   */
  public boolean hasErrors() {
    checkAccess();
    return Native.hasErrors(this);
  }

  /**
   * Check if the node is an error.
   */
  public boolean isError() {
    checkAccess();
    return Native.isError(this);
  }

  /**
   * Get this node's parse state.
   */
  public short getParseState() {
    checkAccess();
    return Native.getParseState(this);
  }

  /**
   * Get the parse state after this node.
   */
  public short getNextParseState() {
    checkAccess();
    getTree().checkAccess();
    return Native.getNextParseState(this);
  }

  @Override
  public boolean canAccess() {
    return Native.canAccess(this.getNodeId());
  }

  @Override
  protected void closeNativeObj() {
    // no need to do anything
  }

  @GenerateNativeHeaders(fileName = "node")
  private static final class Native {

    static {
      registerNatives();
    }

    @FastNative
    static native void registerNatives();

    @FastNative
    static native boolean canAccess(long id);

    @FastNative
    static native TSNode getParent(TSNode self);

    @FastNative
    static native TSNode getChildAt(TSNode self, int index);

    @FastNative
    static native TSNode getNamedChildAt(TSNode self, int index);

    @FastNative
    static native TSNode getChildByFieldName(TSNode self, byte[] bytes, int length);

    @FastNative
    static native String getFieldNameForChild(TSNode self, int childIndex);

    @FastNative
    static native TSNode getChildByFieldId(TSNode self, int fieldId);

    @FastNative
    static native TSNode getNextSibling(TSNode self);

    @FastNative
    static native TSNode getPreviousSibling(TSNode self);

    @FastNative
    static native TSNode getNextNamedSibling(TSNode self);

    @FastNative
    static native TSNode getPreviousNamedSibling(TSNode self);

    @FastNative
    static native TSNode getFirstChildForByte(TSNode self, int byteOffset);

    @FastNative
    static native TSNode getFirstNamedChildForByte(TSNode self, int byteOffset);

    @FastNative
    static native TSNode getDescendantForByteRange(TSNode self, int start, int end);

    @FastNative
    static native TSNode getDescendantForPointRange(TSNode self, TSPoint start, TSPoint end);

    @FastNative
    static native TSNode getNamedDescendantForByteRange(TSNode self, int start, int end);

    @FastNative
    static native TSNode getNamedDescendantForPointRange(TSNode self, TSPoint start, TSPoint end);

    @FastNative
    static native boolean isEqualTo(TSNode self, TSNode another);

    @FastNative
    static native int getChildCount(TSNode self);

    @FastNative
    static native int getNamedChildCount(TSNode self);

    @FastNative
    static native String getNodeString(TSNode self);

    @FastNative
    static native int getStartByte(TSNode self);

    @FastNative
    static native int getEndByte(TSNode self);

    @FastNative
    static native TSPoint getStartPoint(TSNode self);

    @FastNative
    static native TSPoint getEndPoint(TSNode self);

    @FastNative
    static native String getType(TSNode self);

    @FastNative
    static native int getSymbol(TSNode self);

    @FastNative
    static native boolean isNull(TSNode self);

    @FastNative
    static native boolean isNamed(TSNode self);

    @FastNative
    static native boolean isExtra(TSNode self);

    @FastNative
    static native boolean isMissing(TSNode self);

    @FastNative
    static native boolean hasChanges(TSNode self);

    @FastNative
    static native boolean hasErrors(TSNode self);

    @FastNative
    static native boolean isError(TSNode self);

    @FastNative
    static native short getParseState(TSNode self);

    @FastNative
    static native void edit(TSNode self, TSInputEdit edit);

    @FastNative
    static native short getNextParseState(TSNode self);

    @FastNative
    public static native int getDescendantCount(TSNode self);

    @FastNative
    public static native String getGrammarType(TSNode self);

    @FastNative
    public static native long getLanguage(TSNode self);
  }
}
