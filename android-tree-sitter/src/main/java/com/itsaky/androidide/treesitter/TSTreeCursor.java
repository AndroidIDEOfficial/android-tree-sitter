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
import java.util.Objects;

public class TSTreeCursor extends TSNativeObject {

  protected int context0;
  protected int context1;
  protected long id;
  protected long tree;

  protected TSTreeCursor(TSNode node) {
    this(Native.newCursor(node));
  }

  protected TSTreeCursor(long pointer) {
    super(pointer);
  }

  public static TSTreeCursor create(long pointer) {
    return TSObjectFactoryProvider.getFactory().createTreeCursor(pointer);
  }

  public static TSTreeCursor create(TSNode node) {
    Objects.requireNonNull(node);
    return TSTreeCursor.create(Native.newCursor(node));
  }

  @Override
  protected void closeNativeObj() {
    Native.delete(pointer);
  }

  /**
   * Get the current node of this tree cursor.
   *
   * @return The current {@link TSNode}.
   */
  public TSNode getCurrentNode() {
    checkAccess();
    return Native.currentNode(pointer);
  }

  /**
   * Get the current field name.
   *
   * @return The field name.
   */
  public String getCurrentFieldName() {
    checkAccess();
    return Native.currentFieldName(pointer);
  }

  /**
   * Get the field id of the tree cursor's current node.
   * <p>
   * This returns zero if the current node doesn't have a field.
   */
  public short getCurrentFieldId() {
    checkAccess();
    return Native.currentFieldId(pointer);
  }

  /**
   * Get the current tree cursor node.
   *
   * @return The current tree cursor node.
   */
  public TSTreeCursorNode getCurrentTreeCursorNode() {
    checkAccess();
    return Native.currentTreeCursorNode(pointer);
  }

  /**
   * Moves to the next child.
   *
   * @return <code>true</code> if moved successfully, <code>false</code> otherwise.
   */
  public boolean gotoFirstChild() {
    checkAccess();
    return Native.gotoFirstChild(pointer);
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
    return Native.gotoFirstChildForByte(pointer, byteIndex);
  }

  /**
   * Move the cursor to the first child of its current node that extends beyond the given point.
   * <p>
   * This returns the index of the child node if one was found, and returns -1 if no such child was
   * found.
   */
  public boolean gotoFirstChildForPoint(TSPoint point) {
    checkAccess();
    return Native.gotoFirstChildForPoint(pointer, point);
  }

  /**
   * Move to the last child.
   *
   * @return <code>true</code> if moved successfully, <code>false</code> otherwise.
   */
  public boolean gotoLastChild() {
    checkAccess();
    return Native.gotoLastChild(pointer);
  }

  /**
   * Moves to the next sibling node.
   *
   * @return <code>true</code> if moved successfully, <code>false</code> otherwise.
   */
  public boolean gotoNextSibling() {
    checkAccess();
    return Native.gotoNextSibling(pointer);
  }

  /**
   * Move to the previous sibling node.
   *
   * @return <code>true</code> if moved successfully, <code>false</code> otherwise.
   */
  public boolean gotoPreviousSibling() {
    checkAccess();
    return Native.gotoPreviousSibling(pointer);
  }

  /**
   * Moves to the parent node.
   *
   * @return <code>true</code> if moved successfully, <code>false</code> otherwise.
   */
  public boolean gotoParent() {
    checkAccess();
    return Native.gotoParent(pointer);
  }

  /**
   * Move the cursor to the node that is the nth descendant of the original node that the cursor was
   * constructed with, where zero represents the original node itself.
   */
  public void gotoDescendant(int descendantIndex) {
    checkAccess();
    Native.gotoDescendant(pointer, descendantIndex);
  }

  /**
   * Get the index of the cursor's current node out of all of the descendants of the original node
   * that the cursor was constructed with.
   */
  public int getCurrentDescendantIndex() {
    checkAccess();
    return Native.currentDescendantIndex(pointer);
  }

  /**
   * Get the depth of the cursor's current node relative to the original node that the cursor was
   * constructed with.
   */
  public int getDepth() {
    checkAccess();
    return Native.depth(pointer);
  }

  /**
   * Re-initialize a tree cursor to start at a different node.
   */
  public void reset(TSNode node) {
    checkAccess();
    Native.reset(pointer, node);
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
    Native.resetTo(pointer, another.pointer);
  }

  /**
   * Create a copy of this cursor.
   *
   * @return The copied {@link TSTreeCursor} or <code>null</code> if there was an error copying the
   * cursor.
   */
  public TSTreeCursor copy() {
    checkAccess();
    final var pointer = Native.copy(this.pointer);
    if (pointer == 0) {
      return null;
    }

    return TSObjectFactoryProvider.getFactory().createTreeCursor(pointer);
  }

  private static class Native {

    public static native long newCursor(TSNode node);

    public static native TSTreeCursorNode currentTreeCursorNode(long cursor);

    public static native String currentFieldName(long cursor);

    public static native TSNode currentNode(long cursor);

    public static native void delete(long cursor);

    public static native boolean gotoFirstChild(long cursor);

    public static native boolean gotoNextSibling(long cursor);

    public static native boolean gotoParent(long cursor);

    public static native short currentFieldId(long pointer);

    public static native long gotoFirstChildForByte(long pointer, int byteIndex);

    public static native boolean gotoFirstChildForPoint(long pointer, TSPoint point);

    public static native boolean gotoLastChild(long pointer);

    public static native boolean gotoPreviousSibling(long pointer);

    public static native void gotoDescendant(long pointer, int descendantIndex);

    public static native int currentDescendantIndex(long pointer);

    public static native int depth(long pointer);

    public static native void reset(long pointer, TSNode node);

    public static native void resetTo(long pointer, long another);

    public static native long copy(long pointer);
  }
}