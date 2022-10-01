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

  public TSNode getChild(int index) {
    final var count = getChildCount();
    if (index < 0 || index >= count) {
      throw new IndexOutOfBoundsException("count=" + count + ", index=" + index);
    }

    return getChildAt(index);
  }

  public TSNode getNamedChild(int index) {
    final var count = getNamedChildCount();
    if (index < 0 || index >= count) {
      throw new IndexOutOfBoundsException("count=" + count + ", index=" + index);
    }

    return getNamedChildAt(index);
  }

  public TSNode getChildByFieldName(String fieldName) {
    final var bytes = fieldName.getBytes(StandardCharsets.UTF_8);
    return getChildByFieldName(bytes, bytes.length);
  }

  public TSTreeCursor walk() {
    return new TSTreeCursor(this);
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

  public List<TSNode> findChildrenWithType(
      final String type, final boolean reverseSearch, final boolean namedOnly) {
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

  /**
   * Get the immediate parent node of this node.
   *
   * @return The parent node.
   */
  public native TSNode getParent();

  /**
   * Get the child of the given node at the child index.
   *
   * @param index The index of the child.
   * @return The child at the child index.
   */
  private native TSNode getChildAt(int index);

  /**
   * Get the named child of the given node at the given index.
   *
   * @param index The index of the named child.
   * @return The named child node at the given index.
   */
  private native TSNode getNamedChildAt(int index);

  /**
   * Find the child node of the given node by field name.
   *
   * @param bytes The field name of the child.
   * @param length The length of `fieldName`.
   * @return The found node.
   */
  private native TSNode getChildByFieldName(byte[] bytes, int length);

  /**
   * Get the number of children of the node.
   *
   * @return The number of children.
   */
  public native int getChildCount();

  /** Get the number of 'named' child nodes in the node. */
  public native int getNamedChildCount();

  /**
   * Get the end byte of this node.
   *
   * @return End byte of node.
   */
  public native int getEndByte();

  /**
   * Get the string representation of this node.
   *
   * @return The string representation of the node.
   */
  public native String getNodeString();

  /**
   * Get the start byte of this node.
   *
   * @return Start byte of node.
   */
  public native int getStartByte();

  /**
   * Get the start position of this node.
   *
   * @return The start position.
   */
  public native TSPoint getStartPoint();

  /**
   * Get the end position of this node.
   *
   * @return The end position.
   */
  public native TSPoint getEndPoint();

  /**
   * Get the type of this node.
   *
   * @return The type of the node.
   */
  public native String getType();

  /** Check if the node is null. */
  public native boolean isNull();

  /**
   * Check if the node is *named*. Named nodes correspond to named rules in the grammar, whereas
   * *anonymous* nodes correspond to string literals in the grammar.
   */
  public native boolean isNamed();

  /**
   * Check if the node is *extra*. Extra nodes represent things like comments, which are not
   * required the grammar, but can appear anywhere.
   */
  public native boolean isExtra();

  /**
   * Check if the node is *missing*. Missing nodes are inserted by the parser in order to recover
   * from certain kinds of syntax errors.
   */
  public native boolean isMissing();

  /** Check if the given node has been edited. */
  public native boolean hasChanges();

  /** Check if the node is a syntax error or contains any syntax errors. */
  public native boolean hasErrors();
}
