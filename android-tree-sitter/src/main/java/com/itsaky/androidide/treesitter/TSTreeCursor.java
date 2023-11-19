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
import java.util.Objects;

public class TSTreeCursor extends TSNativeObject {

  protected int context0;
  protected int context1;
  protected long id;
  protected long tree;

  protected TSTreeCursor(TSNode node) {
    this(Native.newCursor(Objects.requireNonNull(node, "TSNode cannot be null")));
  }

  protected TSTreeCursor(long pointer) {
    super(pointer);
  }

  public static TSTreeCursor create(long pointer) {
    return TSObjectFactoryProvider.getFactory().createTreeCursor(pointer);
  }

  public static TSTreeCursor create(TSNode node) {
    Objects.requireNonNull(node, "TSNode cannot be null");
    return TSTreeCursor.create(Native.newCursor(node));
  }

  @Override
  protected void closeNativeObj() {
    Native.delete(getNativeObject());
  }

  /**
   * Get the current node of this tree cursor.
   *
   * @return The current {@link TSNode}.
   */
  public TSNode getCurrentNode() {
    checkAccess();
    return Native.currentNode(getNativeObject());
  }

  /**
   * Get the current field name.
   *
   * @return The field name.
   */
  public String getCurrentFieldName() {
    checkAccess();
    return Native.currentFieldName(getNativeObject());
  }

  /**
   * Get the field id of the tree cursor's current node.
   * <p>
   * This returns zero if the current node doesn't have a field.
   */
  public short getCurrentFieldId() {
    checkAccess();
    return Native.currentFieldId(getNativeObject());
  }

  /**
   * Get the current tree cursor node.
   *
   * @return The current tree cursor node.
   */
  public TSTreeCursorNode getCurrentTreeCursorNode() {
    checkAccess();
    return Native.currentTreeCursorNode(getNativeObject());
  }

  /**
   * Moves to the next child.
   *
   * @return <code>true</code> if moved successfully, <code>false</code> otherwise.
   */
  public boolean gotoFirstChild() {
    checkAccess();
    return Native.gotoFirstChild(getNativeObject());
  }

  /**
   * Move the cursor to the first child of its current node that extends beyond the given byte
   * offset.
   * <p>
   * This returns the index of the child node if one was found, and returns -1 if no such child was
   * found.
   */
  public long gotoFirstChildForByte(int byteIndex) {
    checkAccess();
    return Native.gotoFirstChildForByte(getNativeObject(), byteIndex);
  }

  /**
   * Move the cursor to the first child of its current node that extends beyond the given point.
   * <p>
   * This returns the index of the child node if one was found, and returns -1 if no such child was
   * found.
   */
  public boolean gotoFirstChildForPoint(TSPoint point) {
    checkAccess();
    return Native.gotoFirstChildForPoint(getNativeObject(), point);
  }

  /**
   * Move to the last child.
   *
   * @return <code>true</code> if moved successfully, <code>false</code> otherwise.
   */
  public boolean gotoLastChild() {
    checkAccess();
    return Native.gotoLastChild(getNativeObject());
  }

  /**
   * Moves to the next sibling node.
   *
   * @return <code>true</code> if moved successfully, <code>false</code> otherwise.
   */
  public boolean gotoNextSibling() {
    checkAccess();
    return Native.gotoNextSibling(getNativeObject());
  }

  /**
   * Move to the previous sibling node.
   *
   * @return <code>true</code> if moved successfully, <code>false</code> otherwise.
   */
  public boolean gotoPreviousSibling() {
    checkAccess();
    return Native.gotoPreviousSibling(getNativeObject());
  }

  /**
   * Moves to the parent node.
   *
   * @return <code>true</code> if moved successfully, <code>false</code> otherwise.
   */
  public boolean gotoParent() {
    checkAccess();
    return Native.gotoParent(getNativeObject());
  }

  /**
   * Move the cursor to the node that is the nth descendant of the original node that the cursor was
   * constructed with, where zero represents the original node itself.
   */
  public void gotoDescendant(int descendantIndex) {
    checkAccess();
    Native.gotoDescendant(getNativeObject(), descendantIndex);
  }

  /**
   * Get the index of the cursor's current node out of all of the descendants of the original node
   * that the cursor was constructed with.
   */
  public int getCurrentDescendantIndex() {
    checkAccess();
    return Native.currentDescendantIndex(getNativeObject());
  }

  /**
   * Get the depth of the cursor's current node relative to the original node that the cursor was
   * constructed with.
   */
  public int getDepth() {
    checkAccess();
    return Native.depth(getNativeObject());
  }

  /**
   * Re-initialize a tree cursor to start at a different node.
   */
  public void reset(TSNode node) {
    Objects.requireNonNull(node, "TSNode cannot be null");
    checkAccess();
    Native.reset(getNativeObject(), node);
  }

  /**
   * Re-initialize a tree cursor to the same position as another cursor.
   * <p>
   * Unlike {@link #reset(TSNode)}, this will not lose parent information and allows reusing already
   * created cursors.
   */
  public void resetTo(TSTreeCursor another) {
    checkAccess();
    another.checkAccess();
    Native.resetTo(getNativeObject(), another.getNativeObject());
  }

  /**
   * Create a copy of this cursor.
   *
   * @return The copied {@link TSTreeCursor} or <code>null</code> if there was an error copying the
   * cursor.
   */
  public TSTreeCursor copy() {
    checkAccess();
    final var pointer = Native.copy(this.getNativeObject());
    if (pointer == 0) {
      return null;
    }

    return TSObjectFactoryProvider.getFactory().createTreeCursor(pointer);
  }

  @GenerateNativeHeaders(fileName = "tree_cursor")
  private static class Native {

    static {
      registerNatives();
    }

    static native long newCursor(TSNode node);

    static native TSTreeCursorNode currentTreeCursorNode(long cursor);

    static native String currentFieldName(long cursor);

    static native TSNode currentNode(long cursor);

    static native void delete(long cursor);

    static native boolean gotoFirstChild(long cursor);

    static native boolean gotoNextSibling(long cursor);

    static native boolean gotoParent(long cursor);

    static native short currentFieldId(long pointer);

    static native long gotoFirstChildForByte(long pointer, int byteIndex);

    static native boolean gotoFirstChildForPoint(long pointer, TSPoint point);

    static native boolean gotoLastChild(long pointer);

    static native boolean gotoPreviousSibling(long pointer);

    static native void gotoDescendant(long pointer, int descendantIndex);

    static native int currentDescendantIndex(long pointer);

    static native int depth(long pointer);

    static native void reset(long pointer, TSNode node);

    static native void resetTo(long pointer, long another);

    static native long copy(long pointer);

    static native void registerNatives();
  }
}