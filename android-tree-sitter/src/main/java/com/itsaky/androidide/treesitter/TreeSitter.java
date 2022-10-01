package com.itsaky.androidide.treesitter;

public class TreeSitter {

  // -------------------------------------------
  // ---------- Section: TSParser --------------
  // -------------------------------------------

  // -------------------------------------------
  // ---------- Section: TSTreeCursor ----------
  // -------------------------------------------

  // -------------------------------------------
  // ---------- Section: TSTree ----------------
  // -------------------------------------------
  /**
   * Notify that the tree has been edited.
   *
   * @param tree The pointer to the tree.
   * @param inputEdit The edit descriptor.
   */
  public static native void treeEdit(long tree, TSInputEdit inputEdit);

  /**
   * Delete the given tree.
   *
   * @param tree The pointer to the tree.
   */
  public static native void treeDelete(long tree);

  /**
   * Creates shallow of the given tree. This is very fast. Suitable for use in multiple threads.
   *
   * @param tree The pointer to the tree to copy.
   * @return The pointer to the copied tree.
   */
  public static native long treeCopy(long tree);

  /**
   * Get the root node of the given tree.
   *
   * @param tree The pointer to the tree.
   * @return The root node.
   */
  public static native TSNode treeRootNode(long tree);

  /**
   * @param language
   * @param source
   * @return
   */
  public static native long tsQueryNew(long language, String source);
}
