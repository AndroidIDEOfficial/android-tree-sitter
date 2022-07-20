package com.itsaky.androidide.treesitter;

public class TSNode {
  private int context0;
  private int context1;
  private int context2;
  private int context3;
  private long id;
  private long tree;

  public TSNode() {}

  public TSNode getChild(int child) {
    final var count = getChildCount();
    if (child < 0 || child >= count) {
      throw new IndexOutOfBoundsException("count=" + count + ", index=" + child);
    }

    return TreeSitter.nodeChild(this, child);
  }

  public int getChildCount() {
    return TreeSitter.nodeChildCount(this);
  }

  public int getEndByte() {
    return TreeSitter.nodeEndByte(this);
  }

  public String getNodeString() {
    return TreeSitter.nodeString(this);
  }

  public int getStartByte() {
    return TreeSitter.nodeStartByte(this);
  }

  public TSPoint getStartPoint() {
    return TreeSitter.nodeStartPoint(this);
  }

  public TSPoint getEndPoint() {
    return TreeSitter.nodeEndPoint(this);
  }

  public String getType() {
    return TreeSitter.nodeType(this);
  }

  public boolean isNamed() {
    return TreeSitter.nodeIsNamed(this);
  }

  public boolean isExtra() {
    return TreeSitter.nodeIsExtra(this);
  }

  public boolean isMissing() {
    return TreeSitter.nodeIsMissing(this);
  }

  public boolean hasError() {
    return TreeSitter.nodeHasError(this);
  }

  public TSTreeCursor walk() {
    return new TSTreeCursor(TreeSitter.treeCursorNew(this));
  }

  @Override
  public String toString() {
    return "TSNode{" + "id=" + id + ", type=" + getType() + '}';
  }
}
