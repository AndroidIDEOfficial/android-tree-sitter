package com.itsaky.androidide.treesitter;

public class TSTree implements AutoCloseable {

  private final long pointer;

  TSTree(long pointer) {
    this.pointer = pointer;
  }

  /** Close and delete this tree. */
  @Override
  public void close() {
    Native.delete(pointer);
  }

  /**
   * Get the root node of this tree.
   *
   * @return The root node.
   */
  public TSNode getRootNode() {
    return Native.rootNode(pointer);
  }

  /**
   * Make a shallow copy of this tree. This is very fast.
   *
   * @return The copy of this tree.
   */
  public TSTree copy() {
    return new TSTree(Native.copy(pointer));
  }

  /**
   * Notify that this tree has been edited.
   *
   * @param edit The edit.
   */
  public void edit(TSInputEdit edit) {
    Native.edit(pointer, edit);
  }

  /**
   * Get the pointer of this tree.
   *
   * @return The pointer.
   */
  long getPointer() {
    return pointer;
  }

  private static class Native {
    public static native void edit(long tree, TSInputEdit inputEdit);
    public static native void delete(long tree);
    public static native long copy(long tree);
    public static native TSNode rootNode(long tree);
  }
}
