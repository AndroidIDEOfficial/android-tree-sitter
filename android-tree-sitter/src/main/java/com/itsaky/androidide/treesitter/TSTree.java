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

public class TSTree extends TSNativeObject {

  TSTree(long pointer) {
    super(pointer);
  }

  @Override
  protected void closeNativeObj() {
    Native.delete(pointer);
  }

  /**
   * Get the root node of this tree.
   *
   * @return The root node.
   */
  public TSNode getRootNode() {
    checkAccess();
    return Native.rootNode(pointer);
  }

  public TSRange[] getChangedRanges(TSTree oldTree) {
    checkAccess();
    oldTree.checkAccess();

    TSRange[] ranges = Native.changedRanges(this.pointer, oldTree.pointer);
    if (ranges == null) {
      return new TSRange[0];
    }
    return ranges;
  }

  /**
   * Make a shallow copy of this tree. This is very fast.
   *
   * @return The copy of this tree.
   */
  public TSTree copy() {
    checkAccess();
    return new TSTree(Native.copy(pointer));
  }

  /**
   * Notify that this tree has been edited.
   *
   * @param edit The edit.
   */
  public void edit(TSInputEdit edit) {
    checkAccess();
    Native.edit(pointer, edit);
  }

  /**
   * Get the language that was used to parse the syntax tree.
   *
   * @return The tree sitter language.
   */
  public TSLanguage getLanguage() {
    checkAccess();
    final var langPtr = Native.getLanguage(this.pointer);
    if (langPtr == 0) {
      return null;
    }

    return TSLanguageCache.get(langPtr);
  }

  private static class Native {
    public static native void edit(long tree, TSInputEdit inputEdit);

    public static native void delete(long tree);

    public static native long copy(long tree);

    public static native TSNode rootNode(long tree);

    public static native TSRange[] changedRanges(long tree, long oldTree);

    public static native long getLanguage(long tree);
  }
}