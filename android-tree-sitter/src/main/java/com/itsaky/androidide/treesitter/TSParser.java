package com.itsaky.androidide.treesitter;

import java.io.UnsupportedEncodingException;

public class TSParser implements AutoCloseable {
  private final long pointer;

  public TSParser(long pointer) {
    this.pointer = pointer;
  }

  public TSParser() {
    this(Native.newParser());
  }

  private TSTree createTree(long pointer) {
    if (pointer == 0) {
      return null;
    }
    return new TSTree(pointer);
  }

  /**
   * Set the language of the given parser.
   *
   * @param language The language to set.
   * @see TSLanguage
   * @see TSLanguages
   */
  public void setLanguage(TSLanguage language) {
    Native.setLanguage(pointer, language.pointer);
  }

  /**
   * Get the language for this parser instance.
   *
   * @return The language instance.
   */
  public TSLanguage getLanguage() {
    return new TSLanguage(Native.getLanguage(this.pointer));
  }

  /**
   * Parses the given String source. Uses {@link TSInputEncoding#TSInputEncodingUTF8} as the default
   * encoding.
   *
   * @param source The source code to parse.
   * @return The parsed tree.
   * @throws UnsupportedEncodingException
   */
  public TSTree parseString(String source) throws UnsupportedEncodingException {
    return parseString(source, TSInputEncoding.TSInputEncodingUTF8);
  }

  /**
   * Parses the given String source with the given encoding.
   *
   * @param source The source code to parse.
   * @param encoding The encoding to of the source.
   * @return The parsed tree.
   * @throws UnsupportedEncodingException
   */
  public TSTree parseString(String source, TSInputEncoding encoding)
      throws UnsupportedEncodingException {
    byte[] bytes = source.getBytes(encoding.getCharset());
    final var tree = TSParser.Native.parseBytes(pointer, bytes, bytes.length, encoding.getFlag());
    return createTree(tree);
  }

  /**
   * Parses the given bytes.
   *
   * @param bytes The bytes to parse.
   * @param bytesLength The length of bytes to parse.
   * @param encodingFlag The encoding of the source.
   * @return The parsed tree.
   */
  public TSTree parseBytes(byte[] bytes, int bytesLength, int encodingFlag) {
    return createTree(Native.parseBytes(pointer, bytes, bytesLength, encodingFlag));
  }

  /**
   * @see #parseString(TSTree, String, TSInputEncoding)
   */
  public TSTree parseString(TSTree oldTree, String source) throws UnsupportedEncodingException {
    return parseString(oldTree, source, TSInputEncoding.TSInputEncodingUTF8);
  }

  /**
   * Parses the given string source code.
   *
   * @param oldTree If earlier version of the same document has been parsed and you intend to do an
   *     incremental parsing, then this should be the earlier parsed syntax tree. Otherwise <code>
   *     null</code>.
   * @param source The source code to parse.
   * @param encoding The encoding of the source code.
   * @return The parsed tree.
   * @throws UnsupportedEncodingException
   */
  public TSTree parseString(TSTree oldTree, String source, TSInputEncoding encoding)
      throws UnsupportedEncodingException {
    byte[] bytes = source.getBytes(encoding.getCharset());
    return createTree(
        Native.incrementalParseBytes(
            pointer, oldTree.getPointer(), bytes, bytes.length, encoding.getFlag()));
  }

  /**
   * Set the maximum duration in microseconds that parsing should be allowed to take before halting.
   *
   * <p>If parsing takes longer than this, it will halt early, returning <code>null</code>.
   */
  public void setTimeout(long microseconds) {
    Native.setTimeout(this.pointer, microseconds);
  }

  /**
   * Get the duration in microseconds that parsing is allowed to take.
   *
   * @return The timeout in microseconds.
   */
  public long getTimeout() {
    return Native.getTimeout(this.pointer);
  }

  /**
   * Set the ranges of text that the parser should include when parsing.
   *
   * <p>By default, the parser will always include entire documents. This function allows you to
   * parse only a *portion* of a document but still return a syntax tree whose ranges match up with
   * the document as a whole. You can also pass multiple disjoint ranges.
   *
   * <p>If the ranges parameter is an empty array, then the entire document will be parsed.
   * Otherwise, the given ranges must be ordered from earliest to latest in the document, and they
   * must not overlap. That is, the following must hold for all `i` < `length - 1`:
   * ranges[i].end_byte <= ranges[i + 1].start_byte where `length` is the length of the ranges
   * array.
   *
   * <p>If this requirement is not satisfied, the operation will fail, the ranges will not be
   * assigned, and this function will return `false`. On success, this function returns `true`
   */
  public boolean setIncludedRanges(TSRange[] ranges) {
    return Native.setIncludedRanges(this.pointer, ranges);
  }

  public TSRange[] getIncludedRanges() {
    return Native.getIncludedRanges(this.pointer);
  }

  /**
   * Instruct the parser to start the next parse from the beginning.
   *
   * <p>If the parser previously failed because of a timeout or a cancellation, then by default, it
   * will resume where it left off on the next call to any of the parsing functions. If you don't
   * want to resume, and instead intend to use this parser to parse some other document, you must
   * call this function first.
   */
  public void reset() {
    Native.reset(this.pointer);
  }

  /** Closes and deletes the current parser. */
  @Override
  public void close() {
    Native.delete(pointer);
  }

  private static class Native {
    public static native long newParser();

    public static native void delete(long parser);

    public static native void setLanguage(long parser, long language);

    public static native long getLanguage(long parser);

    public static native long parseBytes(long parser, byte[] source, int length, int encoding);

    public static native long incrementalParseBytes(
        long parser, long old_tree, byte[] source, int length, int encoding);

    public static native void reset(long parser);

    public static native void setTimeout(long parser, long timeout);

    public static native long getTimeout(long parser);

    public static native boolean setIncludedRanges(long parser, TSRange[] ranges);

    public static native TSRange[] getIncludedRanges(long parser);
  }
}
