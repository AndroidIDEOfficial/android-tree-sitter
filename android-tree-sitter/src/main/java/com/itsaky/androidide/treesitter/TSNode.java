package com.itsaky.androidide.treesitter;

import java.nio.charset.StandardCharsets;

public class TSNode {
  private int context0;
  private int context1;
  private int context2;
  private int context3;
  private long id;
  private long tree;

  public TSNode() {}

  public TSNode getParent() {
    return TreeSitter.nodeParent(this);
  }

  public TSNode getChild(int index) {
    final var count = getChildCount();
    if (index < 0 || index >= count) {
      throw new IndexOutOfBoundsException("count=" + count + ", index=" + index);
    }

    return TreeSitter.nodeChild(this, index);
  }

  public TSNode getNamedChild(int index) {
    final var count = getNamedChildCount();
    if (index < 0 || index >= count) {
      throw new IndexOutOfBoundsException("count=" + count + ", index=" + index);
    }

    return TreeSitter.nodeNamedChild(this, index);
  }

  public TSNode getChildByFieldName(String fieldName) {
    final var bytes = fieldName.getBytes(StandardCharsets.UTF_8);
    return TreeSitter.getChildByFieldName(this, bytes, bytes.length);
  }

  public int getChildCount() {
    return TreeSitter.nodeChildCount(this);
  }

  public int getNamedChildCount() {
    return TreeSitter.nodeNamedChildCount(this);
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

  public boolean isNull() {
    return TreeSitter.nodeIsNull(this);
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

  public boolean hasChanges() {
    return TreeSitter.nodeHasChanges(this);
  }

  public boolean hasErrors() {
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
