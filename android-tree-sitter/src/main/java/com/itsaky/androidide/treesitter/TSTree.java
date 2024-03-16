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
import java.util.Objects;

public class TSTree extends TSNativeObject {

  protected TSTree(long pointer) {
    super(pointer);
  }

  public static TSTree create(long pointer) {
    return TSObjectFactoryProvider.getFactory().createTree(pointer);
  }

  @Override
  protected void closeNativeObj() {
    Native.delete(getNativeObject());
  }

  /**
   * Get the root node of this tree.
   *
   * @return The root node.
   */
  public TSNode getRootNode() {
    checkAccess();
    return Native.rootNode(getNativeObject());
  }

  /**
   * Get the root node of the syntax tree, but with its position shifted forward by the given
   * offset.
   */
  public TSNode getRootNodeWithOffset(int offsetBytes, TSPoint offsetExtent) {
    checkAccess();
    return Native.rootNodeWithOffset(getNativeObject(), offsetBytes, offsetExtent);
  }

  /**
   * Compare an old edited syntax tree to a new syntax tree representing the same document,
   * returning an array of ranges whose syntactic structure has changed.
   * <p>
   * For this to work correctly, the old syntax tree must have been edited such that its ranges
   * match up to the new tree. Generally, you'll want to call this function right after calling one
   * of the <code>parse*</code> functions in {@link TSParser}. You need to pass the old tree that
   * was passed to parse, as well as the new tree that was returned from that function.
   */
  public TSRange[] getChangedRanges(TSTree oldTree) {
    checkAccess();
    oldTree.checkAccess();

    TSRange[] ranges = Native.changedRanges(getNativeObject(), oldTree.getNativeObject());
    if (ranges == null) {
      return new TSRange[0];
    }
    return ranges;
  }

  /**
   * Get the array of included ranges that was used to parse the syntax tree.
   */
  public TSRange[] getIncludedRanges() {
    checkAccess();

    TSRange[] ranges = Native.includedRanges(getNativeObject());
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
    return TSTree.create(Native.copy(getNativeObject()));
  }

  /**
   * Notify that this tree has been edited.
   *
   * @param edit The edit.
   */
  public void edit(TSInputEdit edit) {
    Objects.requireNonNull(edit, "TSInputEdit cannot be null");
    checkAccess();
    Native.edit(getNativeObject(), edit);
  }

  /**
   * Get the language that was used to parse the syntax tree.
   *
   * @return The tree sitter language.
   */
  public TSLanguage getLanguage() {
    checkAccess();
    final var langPtr = Native.getLanguage(getNativeObject());
    if (langPtr == 0) {
      return null;
    }

    return TSLanguageCache.get(langPtr);
  }

  @GenerateNativeHeaders(fileName = "tree")
  private static class Native {

    @FastNative
    static native void edit(long tree, TSInputEdit inputEdit);

    @FastNative
    static native void delete(long tree);

    @FastNative
    static native long copy(long tree);

    @FastNative
    static native TSNode rootNode(long tree);

    @FastNative
    static native TSNode rootNodeWithOffset(long tree, int offsetBytes, TSPoint offsetExtent);

    @FastNative
    static native TSRange[] changedRanges(long tree, long oldTree);

    @FastNative
    static native long getLanguage(long tree);

    @FastNative
    static native TSRange[] includedRanges(long nativeObject);
  }
}