/*
 *  This file is part of android-tree-sitter.
 *
 *  android-tree-sitter library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  android-tree-sitter library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *  along with android-tree-sitter.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.itsaky.androidide.treesitter;

import com.itsaky.androidide.treesitter.string.UTF16String;
import com.itsaky.androidide.treesitter.string.UTF16StringFactory;
import com.itsaky.androidide.treesitter.util.TSObjectFactoryProvider;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementation of tree sitter's <code>TSParser</code> APIs. This implementation always converts
 * the input source code to {@link UTF16String UTF-16 string}.
 */
public class TSParser extends TSNativeObject {

  protected final ReentrantLock parseLock = new ReentrantLock(true);
  protected final ReentrantLock cancellationLock = new ReentrantLock(true);
  protected final AtomicBoolean isParsing = new AtomicBoolean(false);
  protected final AtomicBoolean isCancellationRequested = new AtomicBoolean(false);

  protected TSParser(long pointer) {
    super(pointer);
  }

  protected TSParser() {
    this(Native.newParser());
  }

  public static TSParser create() {
    return create(Native.newParser());
  }

  public static TSParser create(long parserPointer) {
    return TSObjectFactoryProvider.getFactory().createParser(parserPointer);
  }

  private TSTree createTree(long pointer) {
    if (pointer == 0) {
      return null;
    }
    return TSTree.create(pointer);
  }

  /**
   * Set the language of the given parser.
   *
   * @param language The language to set.
   * @see TSLanguage
   */
  public void setLanguage(TSLanguage language) {
    checkAccess();
    Native.setLanguage(getNativeObject(), language.getNativeObject());
  }

  /**
   * Get the language for this parser instance.
   *
   * @return The language instance.
   */
  public TSLanguage getLanguage() {
    checkAccess();
    final var langPtr = Native.getLanguage(this.getNativeObject());
    if (langPtr == 0) {
      return null;
    }

    return TSLanguageCache.get(langPtr);
  }

  /**
   * Parses the given String source. See {@link #parseString(TSTree, UTF16String)} for more
   * details.
   *
   * @param source The source code to parse.
   * @return The parsed tree, or <code>null</code> if the parse failed or was cancelled.
   */
  public TSTree parseString(String source) {
    throwIfParseNotCancelled();
    try (final var str = UTF16StringFactory.newString(source)) {
      return parseString(str);
    }
  }

  /**
   * Parses the given {@link UTF16String} source. See {@link #parseString(TSTree, UTF16String)} for
   * more details.
   *
   * @param source The source code to parse.
   * @return The parsed tree, or <code>null</code> if the parse failed or was cancelled.
   */
  public TSTree parseString(UTF16String source) {
    return parseString(null, source);
  }

  /**
   * Parses the given source code bytes. See {@link #parseString(TSTree, UTF16String)} for more
   * details.
   *
   * @param bytes The source code to parse.
   * @return The parsed tree, or <code>null</code> if the parse failed or was cancelled.
   * @see #parseBytes(byte[], int, int)
   */
  public TSTree parseBytes(byte[] bytes) {
    return parseBytes(bytes, 0, bytes.length);
  }

  /**
   * Parses the given source code bytes. See {@link #parseString(TSTree, UTF16String)} for more
   * details. See {@link #parseString(TSTree, UTF16String)} for more details.
   *
   * @param bytes  The source code to parse.
   * @param offset The start offset in <code>bytes</code>.
   * @param len    The number of bytes to from <code>offset</code> to parse.
   * @return The parsed tree, or <code>null</code> if the parse failed or was cancelled.
   */
  public TSTree parseBytes(byte[] bytes, int offset, int len) {
    throwIfParseNotCancelled();
    try (final var source = UTF16StringFactory.newString(bytes, offset, len)) {
      return parseString(source);
    }
  }

  /**
   * Parse the given edited source code using the previously parsed syntax tree. The given
   * {@link TSTree} must have been edited using {@link TSTree#edit(TSInputEdit)} before calling this
   * method. See {@link #parseString(TSTree, UTF16String)} for more details.
   *
   * @param oldTree The previously parsed syntax tree.
   * @param source  The source code to parse.
   * @return The parsed tree, or <code>null</code> if the parse failed or was cancelled.
   */
  public TSTree parseString(TSTree oldTree, String source) {
    throwIfParseNotCancelled();
    try (final var str = UTF16StringFactory.newString(source)) {
      return parseString(oldTree, str);
    }
  }

  /**
   * Parse the given edited source code using the previously parsed syntax tree. The given
   * {@link TSTree} must have been edited using {@link TSTree#edit(TSInputEdit)} before calling this
   * method.
   * <p>
   * Throws {@link ParseInProgressException} if the parser is currently parsing a syntax tree and
   * the cancellation was NOT requested using {@link #requestCancellation()}. This method blocks the
   * current thread if the previous parse was requested to be cancelled but the parse operation has
   * not been cancelled yet.
   *
   * @param oldTree The previously parsed syntax tree.
   * @param source  The source code to parse.
   * @return The parsed tree, or <code>null</code> if the parse failed or was cancelled.
   * @throws IllegalStateException    If the parser is not accessible. See
   *                                  {@link TSNativeObject#canAccess()} for more details.
   * @throws ParseInProgressException If the parser is currently parsing another syntax tree.
   */
  public TSTree parseString(TSTree oldTree, UTF16String source) {
    checkAccess();

    // if the parser is currently parsing a syntax tree and the cancellation
    // was not requested, throw an error
    throwIfParseNotCancelled();

    // acquire the lock
    // this will wait until the cancelled parse call returns
    parseLock.lock();
    setCancellationRequested(false);
    setParsingFlag();
    try {
      final var strPointer = source.getPointer();
      final var oldTreePointer = oldTree != null ? oldTree.getNativeObject() : 0;
      final var tree = Native.parse(this.getNativeObject(), oldTreePointer, strPointer);
      return createTree(tree);
    } finally {
      unsetParsingFlag();
      parseLock.unlock();
    }
  }

  /**
   * Set the maximum duration in microseconds that parsing should be allowed to take before
   * halting.
   *
   * <p>If parsing takes longer than this, it will halt early, returning <code>null</code>.
   */
  public void setTimeout(long microseconds) {
    checkAccess();
    Native.setTimeout(getNativeObject(), microseconds);
  }

  /**
   * Get the duration in microseconds that parsing is allowed to take.
   *
   * @return The timeout in microseconds.
   */
  public long getTimeout() {
    checkAccess();
    return Native.getTimeout(getNativeObject());
  }

  /**
   * Check whether the parser is in the process of parsing a syntax tree.
   *
   * @return <code>true</code> if the parser is parsing a syntax tree, <code>false</code> otherwise.
   */
  public boolean isParsing() {
    return isParsing.get();
  }

  /**
   * Sets the 'parsing' flag to indicate that the parser is in the process of parsing a syntax
   * tree.
   */
  protected boolean setParsingFlag() {
    return this.isParsing.compareAndSet(false, true);
  }

  /**
   * Sets the 'parsing' flag to indicate that the parsing operation is NOT in progress.
   */
  protected boolean unsetParsingFlag() {
    return this.isParsing.compareAndSet(true, false);
  }

  /**
   * Request the parsing operation to be cancelled if the parser is in the process of parsing a
   * syntax tree.
   * <p>
   * The parse operation is NOT immediately cancelled.
   *
   * @return <code>true</code> if the cancellation was requested successfully, <code>false</code>
   * otherwise.
   */
  public boolean requestCancellation() {
    final var requested = Native.requestCancellation();
    setCancellationRequested(requested);
    return requested;
  }

  protected void setCancellationRequested(boolean isRequested) {
    cancellationLock.lock();
    try {
      this.isCancellationRequested.set(isRequested);
    } finally {
      cancellationLock.unlock();
    }
  }

  protected synchronized boolean isCancellationRequested() {
    cancellationLock.lock();
    try {
      return this.isCancellationRequested.get();
    } finally {
      cancellationLock.unlock();
    }
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
    checkAccess();
    return Native.setIncludedRanges(getNativeObject(), ranges);
  }

  public TSRange[] getIncludedRanges() {
    checkAccess();
    return Native.getIncludedRanges(getNativeObject());
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
    checkAccess();
    Native.reset(getNativeObject());
  }

  @Override
  protected void closeNativeObj() {
    Native.delete(getNativeObject());
  }

  private void throwIfParseNotCancelled() {
    if (isParsing() && !isCancellationRequested()) {
      throw new ParseInProgressException(
        "Parser is already parsing another syntax tree! Cancel the previous parse before starting another.");
    }
  }

  /**
   * Base class the {@link TSParser} exceptions.
   */
  protected static class TSParserException extends RuntimeException {

    public TSParserException(String message) {
      super(message);
    }

    public TSParserException(String message, Throwable cause) {
      super(message, cause);
    }

    public TSParserException(Throwable cause) {
      super(cause);
    }
  }

  /**
   * Thrown when a parse is requested while another parse is already in progress.
   */
  protected static final class ParseInProgressException extends TSParserException {

    public ParseInProgressException(String message) {
      super(message);
    }
  }

  private static class Native {

    public static native long newParser();

    public static native void delete(long parser);

    public static native void setLanguage(long parser, long language);

    public static native long getLanguage(long parser);

    public static native void reset(long parser);

    public static native void setTimeout(long parser, long timeout);

    public static native long getTimeout(long parser);

    public static native boolean setIncludedRanges(long parser, TSRange[] ranges);

    public static native TSRange[] getIncludedRanges(long parser);

    public static native long parse(long parser, long treePointer, long strPointer);

    public static native boolean requestCancellation();
  }
}