package com.itsaky.androidide.treesitter;

public class TSQuery implements AutoCloseable {
  final long pointer;

  private int errorOffset;
  private int errorType;

  /**
   * Create a new query from a string containing one or more S-expression patterns. The query is
   * associated with a particular language, and can only be run on syntax nodes parsed with that
   * language.
   *
   * <p>If all of the given patterns are valid, this returns a `TSQuery`. If a pattern is invalid,
   * this returns `NULL`, and provides two pieces of information about the problem: 1. The byte
   * offset of the error is written to the `error_offset` parameter. 2. The type of error is written
   * to the `error_type` parameter.
   */
  public TSQuery(TSLanguage language, String source) {
    this.pointer = Native.newQuery(this, language.pointer, source);
  }

  public int getErrorOffset() {
    return this.errorOffset;
  }

  public TSQueryError getErrorType() {
    return TSQueryError.valueOf(this.errorType);
  }

  /**
   * Get the number of captures in the query.
   *
   * @return The count.
   */
  public int getCaptureCount() {
    return Native.captureCount(this.pointer);
  }

  /**
   * Get the number of patterns in the query.
   *
   * @return The count.
   */
  public int getPatternCount() {
    return Native.patternCount(this.pointer);
  }

  /**
   * Get the number of string literals in the query.
   *
   * @return The count.
   */
  public int getStringCount() {
    return Native.stringCount(this.pointer);
  }

  @Override
  public void close() throws Exception {
    Native.delete(this.pointer);
  }

  private static class Native {
    public static native long newQuery(TSQuery query, long pointer, String source);

    public static native void delete(long query);

    public static native int captureCount(long query);

    public static native int patternCount(long query);

    public static native int stringCount(long query);
  }
}
