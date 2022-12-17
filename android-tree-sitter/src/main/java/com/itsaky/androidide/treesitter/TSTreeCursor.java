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

public class TSTreeCursor implements AutoCloseable {
  private final long pointer;
  private int context0;
  private int context1;
  private long id;
  private long tree;

  public TSTreeCursor(TSNode node) {
    this(Native.newCursor(node));
  }

  TSTreeCursor(long pointer) {
    this.pointer = pointer;
  }

  /** Close and delete this tree cursor. */
  @Override
  public void close() {
    Native.delete(pointer);
  }

  /**
   * Get the current node of this tree cursor.
   *
   * @return The current {@link TSNode}.
   */
  public TSNode getCurrentNode() {
    return Native.currentNode(pointer);
  }

  /**
   * Get the current field name.
   *
   * @return The field name.
   */
  public String getCurrentFieldName() {
    return Native.currentFieldName(pointer);
  }

  /**
   * Get the current tree cursor node.
   *
   * @return The current tree cursor node.
   */
  public TSTreeCursorNode getCurrentTreeCursorNode() {
    return Native.currentTreeCursorNode(pointer);
  }

  /**
   * Moves to the next child.
   *
   * @return <code>true</code> if moved successfully, <code>false</code> otherwise.
   */
  public boolean gotoFirstChild() {
    return Native.gotoFirstChild(pointer);
  }

  /**
   * Moves to the next sibling node.
   *
   * @return <code>true</code> if moved successfully, <code>false</code> otherwise.
   */
  public boolean gotoNextSibling() {
    return Native.gotoNextSibling(pointer);
  }

  /**
   * Moves to the parent node.
   *
   * @return <code>true</code> if moved successfully, <code>false</code> otherwise.
   */
  public boolean gotoParent() {
    return Native.gotoParent(pointer);
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
  }
}