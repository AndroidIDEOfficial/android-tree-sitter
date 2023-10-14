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
import java.io.UnsupportedEncodingException;

public class TSParser extends TSNativeObject {

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
   * Parses the given String source. Uses {@link TSInputEncoding#TSInputEncodingUTF8} as the default
   * encoding.
   *
   * @param source The source code to parse.
   * @return The parsed tree.
   */
  public TSTree parseString(String source) {
    try (final var str = UTF16StringFactory.newString(source)) {
      return parseString(str);
    }
  }

  public TSTree parseString(UTF16String source) {
    return parseString(null, source);
  }

  public TSTree parseBytes(byte[] bytes) {
    return parseBytes(bytes, 0, bytes.length);
  }

  public TSTree parseBytes(byte[] bytes, int off, int len) {
    try (final var source = UTF16StringFactory.newString(bytes, off, len)) {
      return parseString(source);
    }
  }

  public TSTree parseString(TSTree oldTree, String source) throws UnsupportedEncodingException {
    try (final var str = UTF16StringFactory.newString(source)) {
      return parseString(oldTree, str);
    }
  }

  public TSTree parseString(TSTree oldTree, UTF16String source) {
    checkAccess();

    final var strPointer = source.getPointer();
    final var oldTreePointer = oldTree != null ? oldTree.getNativeObject() : 0;
    final var tree = Native.parse(this.getNativeObject(), oldTreePointer, strPointer);
    return createTree(tree);
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
  }
}