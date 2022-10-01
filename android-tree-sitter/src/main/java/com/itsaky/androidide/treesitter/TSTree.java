package com.itsaky.androidide.treesitter;

public class TSTree implements AutoCloseable {

  private final long pointer;

  TSTree(long pointer) {
    this.pointer = pointer;
  }

  @Override
  public void close() {
    TreeSitter.treeDelete(pointer);
  }

  public TSNode getRootNode() {
    return TreeSitter.treeRootNode(pointer);
  }

  public TSTree copy() {
    return new TSTree(TreeSitter.treeCopy(pointer));
  }

  public void edit(TSInputEdit edit) {
    TreeSitter.treeEdit(pointer, edit);
  }

  public long getPointer() {
    return pointer;
  }
}
