package com.itsaky.androidide.treesitter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

  public TSNode findNodeWithType(final String type, final boolean namedOnly) {
    for (int i = 0; i < (namedOnly ? getNamedChildCount() : getChildCount()); i++) {
      final var child = namedOnly ? getNamedChild(i) : getChild(i);
      if (child.isNull()) {
        continue;
      }

      if (type.equals(child.getType())) {
        return child;
      }
    }

    return null;
  }

  public List<TSNode> findChildrenWithType(final String type, final boolean reverseSearch, final boolean namedOnly) {
    if (reverseSearch) {
      return findChildrenWithTypeReverse(type, namedOnly);
    }

    final var result = new ArrayList<TSNode>();
    for (int i = 0; i < (namedOnly ? getNamedChildCount() : getChildCount()); i++) {
      final var child = namedOnly ? getNamedChild(i) : getChild(i);
      if (child.isNull()) {
        continue;
      }

      if (Objects.equals(type, child.getType())) {
        result.add(child);
      }
    }

    return result;
  }

  public List<TSNode> findChildrenWithTypeReverse(final String type, final boolean namedOnly) {
    final var result = new ArrayList<TSNode>();
    for (int i = (namedOnly ? getNamedChildCount() : getChildCount()) - 1; i > 0; --i) {
      final var child = namedOnly ? getNamedChild(i) : getChild(i);
      if (child.isNull()) {
        continue;
      }

      if (Objects.equals(type, child.getType())) {
        result.add(child);
      }
    }

    return result;
  }

  @Override
  public String toString() {
    return "TSNode{" + "id=" + id + ", type=" + (isNull() ? "<null>" : getType()) + '}';
  }
}
