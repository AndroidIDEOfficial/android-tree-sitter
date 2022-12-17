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

public class TSQuery implements AutoCloseable {
  final long pointer;
  private int errorOffset;
  private int errorType;

  private String[] captureNames = null;

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

  public String[] getCaptureNames() {
    if (captureNames == null) {
      captureNames = new String[getCaptureCount()];
      for (int i = 0; i < getCaptureCount(); i++) {
        captureNames[i] = getCaptureNameForId(i);
      }
    }
    return captureNames;
  }

  public int getStartByteForPattern(int pattern) {
    validatePatternIndex(pattern);
    return Native.startByteForPattern(this.pointer, pattern);
  }

  public TSQueryPredicateStep[] getPredicatesForPattern(int pattern) {
    validatePatternIndex(pattern);
    return Native.predicatesForPattern(this.pointer, pattern);
  }

  public boolean isPatternRooted(int pattern) {
    validatePatternIndex(pattern);
    return Native.patternRooted(this.pointer, pattern);
  }

  public boolean isPatternGuaranteedAtStep(int offset) {
    return Native.patternGuaranteedAtStep(this.pointer, offset);
  }

  public String getCaptureNameForId(int id) {
    return Native.captureNameForId(this.pointer, id);
  }

  public String getStringValueForId(int id) {
    return Native.stringValueForId(this.pointer, id);
  }

  @Override
  public void close() throws Exception {
    Native.delete(this.pointer);
  }

  private void validatePatternIndex(int pattern) {
    if (pattern < 0 || pattern >= getPatternCount()) {
      throw new IndexOutOfBoundsException(
          "pattern count: " + getPatternCount() + ", pattern: " + pattern);
    }
  }

  private static class Native {
    public static native long newQuery(TSQuery query, long pointer, String source);

    public static native void delete(long query);

    public static native int captureCount(long query);

    public static native int patternCount(long query);

    public static native int stringCount(long query);

    public static native int startByteForPattern(long query, int pattern);

    public static native TSQueryPredicateStep[] predicatesForPattern(long query, int pattern);

    public static native boolean patternRooted(long query, int pattern);

    public static native boolean patternGuaranteedAtStep(long query, int offset);

    public static native String captureNameForId(long query, int id);

    public static native String stringValueForId(long query, int id);
  }
}