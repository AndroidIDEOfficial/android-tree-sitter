package com.itsaky.androidide.treesitter;

/**
 * @author Akash Yadav
 */
public class TSQueryCursor implements AutoCloseable {

  final long pointer;

  public TSQueryCursor() {
    this.pointer = Native.newCursor();
  }

  /** Start running the given query on the given node. */
  public void exec(TSQuery query, TSNode node) {
    Native.exec(this.pointer, query.pointer, node);
  }

  @Override
  public void close() throws Exception {
    Native.delete(this.pointer);
  }

  private static class Native {
    public static native long newCursor();

    public static native void delete(long cursor);

    public static native void exec(long cursor, long query, TSNode node);
  }
}
