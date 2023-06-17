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

import android.text.TextUtils;

public class TSQuery extends TSNativeObject {

  /**
   * An empty query.
   */
  public static final TSQuery EMPTY = new EmptyQuery();

  protected int errorOffset;
  protected int errorType;

  private String[] captureNames = null;

  /**
   * Create a new query from a string containing one or more S-expression patterns. The query is
   * associated with a particular language, and can only be run on syntax nodes parsed with that
   * language.
   *
   * <p>If all of the given patterns are valid, this returns a `TSQuery`. If a pattern is
   * invalid, this returns `NULL`, and provides two pieces of information about the problem: 1. The
   * byte offset of the error is written to the `error_offset` parameter. 2. The type of error is
   * written to the `error_type` parameter.
   */
  public TSQuery(TSLanguage language, String source) {
    super(0);
    if (language == null) {
      throw new IllegalArgumentException("Language cannot be null");
    }

    if (source == null || source.isEmpty()) {
      throw new IllegalArgumentException("Query source cannot be null");
    }

    this.pointer = Native.newQuery(this, language.pointer, source);
  }

  /**
   * For subclasses only!
   * <p>
   * Constructs an invalid query.
   */
  protected TSQuery() {
    super(0);
  }

  /**
   * @return Whether the query is valid or not. A {@link TSQuery} is valid if it has a valid pointer
   * to a native object.
   * @deprecated Use {@link TSNativeObject#canAccess()} instead.
   */
  @Deprecated(since = "3.1.0")
  public boolean isValid() {
    return canAccess();
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
    checkAccess();
    return Native.captureCount(this.pointer);
  }

  /**
   * Get the number of patterns in the query.
   *
   * @return The count.
   */
  public int getPatternCount() {
    checkAccess();
    return Native.patternCount(this.pointer);
  }

  /**
   * Get the number of string literals in the query.
   *
   * @return The count.
   */
  public int getStringCount() {
    checkAccess();
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
    checkAccess();
    validatePatternIndex(pattern);
    return Native.startByteForPattern(this.pointer, pattern);
  }

  public TSQueryPredicateStep[] getPredicatesForPattern(int pattern) {
    checkAccess();
    validatePatternIndex(pattern);
    return Native.predicatesForPattern(this.pointer, pattern);
  }

  public boolean isPatternRooted(int pattern) {
    checkAccess();
    validatePatternIndex(pattern);
    return Native.patternRooted(this.pointer, pattern);
  }

  public boolean isPatternNonLocal(int pattern) {
    checkAccess();
    validatePatternIndex(pattern);
    return Native.patternNonLocal(this.pointer, pattern);
  }

  public boolean isPatternGuaranteedAtStep(int offset) {
    checkAccess();
    return Native.patternGuaranteedAtStep(this.pointer, offset);
  }

  public String getCaptureNameForId(int id) {
    checkAccess();
    return Native.captureNameForId(this.pointer, id);
  }

  public String getStringValueForId(int id) {
    checkAccess();
    return Native.stringValueForId(this.pointer, id);
  }

  @Override
  protected void closeNativeObj() {
    Native.delete(this.pointer);
  }

  private void validatePatternIndex(int pattern) {
    if (pattern < 0 || pattern >= getPatternCount()) {
      throw new IndexOutOfBoundsException(
        "pattern count: " + getPatternCount() + ", pattern: " + pattern);
    }
  }

  /**
   * Creates a new {@link TSQuery} with the given {@link TSLanguage} and query source. If the
   * language or the query source is invalid, {@link TSQuery#EMPTY} is returned.
   *
   * @param language The {@link TSLanguage} for the query.
   * @param query    The query source.
   * @return The {@link TSQuery} object.
   */
  public static TSQuery create(TSLanguage language, String query) {
    if (language == null || query == null || TextUtils.getTrimmedLength(query) == 0) {
      return EMPTY;
    }
    return new TSQuery(language, query);
  }

  /**
   * An empty query. Instances of this class are invalid queries and does not have any patterns,
   * capture names, etc. The <code>get*Count()</code> methods always return <code>0</code>, the
   * <code>get*(int index)</code> methods always throw an {@link IndexOutOfBoundsException} and
   * the <code>get*ForId(int id)</code> methods always throw {@link UnsupportedOperationException}.
   */
  private static final class EmptyQuery extends TSQuery {

    private EmptyQuery() {
    }

    @Override
    public int getCaptureCount() {
      return 0;
    }

    @Override
    public int getPatternCount() {
      return 0;
    }

    @Override
    public int getStringCount() {
      return 0;
    }

    @Override
    public int getErrorOffset() {
      return -1;
    }

    @Override
    public String[] getCaptureNames() {
      return new String[0];
    }

    @Override
    public TSQueryError getErrorType() {
      return TSQueryError.None;
    }

    @Override
    public boolean isPatternGuaranteedAtStep(int offset) {
      return false;
    }

    @Override
    public String getCaptureNameForId(int id) {
      throw new UnsupportedOperationException();
    }

    @Override
    public String getStringValueForId(int id) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
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

    public static native boolean patternNonLocal(long query, int pattern);

    public static native boolean patternGuaranteedAtStep(long query, int byteOffset);

    public static native String captureNameForId(long query, int id);

    public static native String stringValueForId(long query, int id);
  }
}