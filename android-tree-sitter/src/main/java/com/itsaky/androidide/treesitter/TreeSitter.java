package com.itsaky.androidide.treesitter;

public class TreeSitter {

  // -------------------------------------------
  // ---------- Section: TSNode ----------------
  // -------------------------------------------

  /**
   * Get the number of children of the given node.
   *
   * @param node The node to get the children count of.
   * @return The number of children.
   */
  public static native int nodeChildCount(TSNode node);

  /**
   * Get the child of the given node at the given child index.
   *
   * @param node The node to get the child of.
   * @param child The index of the child.
   * @return The child at the child index.
   */
  public static native TSNode nodeChild(TSNode node, int child);

  public static native int nodeEndByte(TSNode node);

  public static native int nodeStartByte(TSNode node);

  /**
   * Get the start position of the given node.
   *
   * @param node The node.
   * @return The start position.
   */
  public static native TSPoint nodeStartPoint(TSNode node);

  /**
   * Get the end position of the given node.
   *
   * @param node The node.
   * @return The end position.
   */
  public static native TSPoint nodeEndPoint(TSNode node);

  /**
   * Get the string representation of the node.
   *
   * @param node The node.
   * @return The string representation of the node.
   */
  public static native String nodeString(TSNode node);

  /**
   * Get the type of the given node.
   *
   * @param node The node.
   * @return The type of the node.
   */
  public static native String nodeType(TSNode node);

  /**
   * Check if the node is *named*. Named nodes correspond to named rules in the grammar, whereas
   * *anonymous* nodes correspond to string literals in the grammar.
   *
   * @param node The node to check.
   */
  public static native boolean nodeIsNamed(TSNode node);

  /**
   * Check if the node is *missing*. Missing nodes are inserted by the parser in order to recover
   * from certain kinds of syntax errors.
   *
   * @param node The node to check.
   */
  public static native boolean nodeIsMissing(TSNode node);

  /**
   * Check if the node is *extra*. Extra nodes represent things like comments, which are not
   * required the grammar, but can appear anywhere.
   *
   * @param node The node to check.
   */
  public static native boolean nodeIsExtra(TSNode node);

  /**
   * Check if the given node has been edited.
   *
   * @param node The node the check.
   */
  public static native boolean nodeHasChanges(TSNode node);

  /**
   * Check if the node is a syntax error or contains any syntax errors.
   *
   * @param node The node to check.
   */
  public static native boolean nodeHasError(TSNode node);
  // -------------------------------------------
  // ---------- Section: TSParser --------------
  // -------------------------------------------

  /**
   * Create a new parser instance.
   *
   * @return The pointer to the new parser.
   */
  public static native long parserNew();

  /**
   * Delete the parser.
   *
   * @param parser The pointer to the parser to delete.
   */
  public static native void parserDelete(long parser);

  /**
   * Set the language of the given parser.
   *
   * @param parser The pointer of the parser.
   * @param language The language to set.
   * @see TSLanguages
   */
  public static native void parserSetLanguage(long parser, long language);

  /**
   * Parse the given source with the given parser.
   *
   * @param parser The pointer of the parser to use.
   * @param source The source code.
   * @param length The length of the source code.
   * @return The pointer to the parsed {@link TSTree}.
   * @see #parserParseBytes(long, byte[], int, int)
   */
  public static long parserParseBytes(long parser, byte[] source, int length) {
    return parserParseBytes(parser, source, length, TSInputEncoding.TSInputEncodingUTF8.getFlag());
  }

  /**
   * Use the parser to parse the source code and create a new syntax tree.
   *
   * @param parser The pointer to the parser.
   * @param old_tree The pointer to the old syntax tree.
   * @param source The source code.
   * @param length The length of the source code.
   * @return The pointer to the new {@link TSTree}.
   * @see #parserIncrementalParseBytes(long, long, byte[], int, int)
   * @see TSInputEncoding
   */
  public static long parserIncrementalParseBytes(
      long parser, long old_tree, byte[] source, int length) {
    return parserIncrementalParseBytes(
        parser, old_tree, source, length, TSInputEncoding.TSInputEncodingUTF8.getFlag());
  }

  /**
   * Parse the given source with the given parser.
   *
   * @param parser The pointer of the parser to use.
   * @param source The source code.
   * @param length The length of the source code.
   * @param encoding The encoding of the source code.
   * @return The pointer to the parsed {@link TSTree}.
   * @see TSInputEncoding
   */
  public static native long parserParseBytes(long parser, byte[] source, int length, int encoding);

  /**
   * Use the parser to parse the source code and create a new syntax tree.
   *
   * @param parser The pointer to the parser.
   * @param old_tree The pointer to the old syntax tree.
   * @param source The source code.
   * @param length The length of the source code.
   * @param encoding The encoding of the source code.
   * @return The pointer to the new {@link TSTree}.
   * @see TSInputEncoding
   */
  public static native long parserIncrementalParseBytes(
      long parser, long old_tree, byte[] source, int length, int encoding);

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
