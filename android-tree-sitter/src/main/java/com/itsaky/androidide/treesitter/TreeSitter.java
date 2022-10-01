package com.itsaky.androidide.treesitter;

public class TreeSitter {

  // -------------------------------------------
  // ---------- Section: TSParser --------------
  // -------------------------------------------

  // -------------------------------------------
  // ---------- Section: TSTreeCursor ----------
  // -------------------------------------------

  /**
   * Create a new {@link TSTreeCursor} for the given node.
   *
   * @param node The node.
   * @return The pointer to the new {@link TSTreeCursor}.
   */
  public static native long treeCursorNew(TSNode node);

  /**
   * Get the current {@link TSTreeCursorNode}.
   *
   * @param cursor The pointer to the {@link TSTreeCursor}.
   * @return The {@link TSTreeCursorNode}.
   */
  public static native TSTreeCursorNode treeCursorCurrentTreeCursorNode(long cursor);

  /**
   * Get the field name of the tree cursor's current node.
   *
   * <p>This returns <code>null</code> if the current node doesn't have a field.
   */
  public static native String treeCursorCurrentFieldName(long cursor);

  /**
   * Get the current node of the given {@link TSTreeCursor}.
   *
   * @param cursor The pointer to the {@link TSTreeCursor}.
   * @return The current node of the cursor.
   */
  public static native TSNode treeCursorCurrentNode(long cursor);

  /**
   * Delete the given {@link TSTreeCursor}.
   *
   * @param cursor The pointer to the cursor.
   */
  public static native void treeCursorDelete(long cursor);

  /**
   * Instruct the given cursor to go to its first child.
   *
   * @param cursor The pointer to the cursor.
   * @return <code>true</code> if moved successfully, <code>false</code> otherwise.
   */
  public static native boolean treeCursorGotoFirstChild(long cursor);

  /**
   * Instruct the given cursor to go to the next sibling node.
   *
   * @param cursor The pointer to the cursor.
   * @return <code>true</code> if moved successfully, <code>false</code> otherwise.
   */
  public static native boolean treeCursorGotoNextSibling(long cursor);

  /**
   * Instruct the given cursor to go to the parent node of current node.
   *
   * @param cursor The pointer to the cursor.
   * @return <code>true</code> if moved successfully, <code>false</code> otherwise.
   */
  public static native boolean treeCursorGotoParent(long cursor);

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
