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

  /**
   * Whether the maximum number of in-progress matches allowed by this query cursor has been
   * exceeded or not.
   *
   * <p>Query cursors have an optional maximum capacity for storing lists of in-progress captures.
   * If this capacity is exceeded, then the earliest-starting match will silently be dropped to make
   * room for further matches. This maximum capacity is optional — by default, query cursors allow
   * any number of pending matches, dynamically allocating new space for them as needed as the query
   * is executed.
   */
  public boolean didExceedMatchLimit() {
    return Native.exceededMatchLimit(this.pointer);
  }

  /**
   * Get the maximum number of in-progress matches allowed by this query * cursor.
   *
   * @return The match limit.
   * @see #didExceedMatchLimit()
   */
  public int getMatchLimit() {
    return Native.matchLimit(this.pointer);
  }

  /**
   * Set the maximum number of in-progress matches allowed by this query * cursor.
   *
   * @param newLimit The new match limit.
   * @see #didExceedMatchLimit()
   */
  public void setMatchLimit(int newLimit) {
    Native.matchLimit(this.pointer, newLimit);
  }

  public void setByteRange(int start, int end) {
    Native.setByteRange(this.pointer, start, end);
  }

  public void setPointRange(TSPoint start, TSPoint end) {
    Native.setPointRange(this.pointer, start, end);
  }

  public TSQueryMatch nextMatch() {
    return Native.nextMatch(this.pointer);
  }

  public void removeMatch(int id) {
    Native.removeMatch(this.pointer, id);
  }

  @Override
  public void close() throws Exception {
    Native.delete(this.pointer);
  }

  private static class Native {
    public static native long newCursor();

    public static native void delete(long cursor);

    public static native void exec(long cursor, long query, TSNode node);

    public static native boolean exceededMatchLimit(long cursor);

    public static native void matchLimit(long cursor, int newLimit);

    public static native int matchLimit(long cursor);

    public static native void setByteRange(long cursor, int start, int end);

    public static native void setPointRange(long cursor, TSPoint start, TSPoint end);

    public static native TSQueryMatch nextMatch(long cursor);

    public static native void removeMatch(long cursor, int id);
  }
}
