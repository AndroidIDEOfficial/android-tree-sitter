package com.itsaky.androidide.treesitter;

public class TSTreeCursor implements AutoCloseable {
  private long pointer;
  private int context0;
  private int context1;
  private long id;
  private long tree;

  TSTreeCursor(long pointer) {
    this.pointer = pointer;
  }

  @Override
  public void close() {
    TreeSitter.treeCursorDelete(pointer);
  }

  public TSNode getCurrentNode() {
    return TreeSitter.treeCursorCurrentNode(pointer);
  }

  public String getCurrentFieldName() {
    return TreeSitter.treeCursorCurrentFieldName(pointer);
  }

  public TSTreeCursorNode getCurrentTreeCursorNode() {
    return TreeSitter.treeCursorCurrentTreeCursorNode(pointer);
  }

  public boolean gotoFirstChild() {
    return TreeSitter.treeCursorGotoFirstChild(pointer);
  }

  public boolean gotoNextSibling() {
    return TreeSitter.treeCursorGotoNextSibling(pointer);
  }

  public boolean gotoParent() {
    return TreeSitter.treeCursorGotoParent(pointer);
  }
}
