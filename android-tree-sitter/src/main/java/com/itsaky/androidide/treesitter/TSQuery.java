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
import com.itsaky.androidide.treesitter.annotations.GenerateNativeHeaders;
import com.itsaky.androidide.treesitter.util.TSObjectFactoryProvider;
import dalvik.annotation.optimization.FastNative;

public class TSQuery extends TSNativeObject {

  /**
   * An empty query.
   */
  public static final TSQuery EMPTY = new EmptyQuery();

  protected int errorOffset;
  protected int errorType;

  protected String[] captureNames = null;

  /**
   * For internal use only!
   * <p>
   * Constructs an invalid query.
   */
  protected TSQuery(long pointer) {
    super(pointer);
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
    return Native.captureCount(getNativeObject());
  }

  /**
   * Get the number of patterns in the query.
   *
   * @return The count.
   */
  public int getPatternCount() {
    checkAccess();
    return Native.patternCount(getNativeObject());
  }

  /**
   * Get the number of string literals in the query.
   *
   * @return The count.
   */
  public int getStringCount() {
    checkAccess();
    return Native.stringCount(getNativeObject());
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
    return Native.startByteForPattern(getNativeObject(), pattern);
  }

  public TSQueryPredicateStep[] getPredicatesForPattern(int pattern) {
    checkAccess();
    validatePatternIndex(pattern);
    return Native.predicatesForPattern(getNativeObject(), pattern);
  }

  public boolean isPatternRooted(int pattern) {
    checkAccess();
    validatePatternIndex(pattern);
    return Native.patternRooted(getNativeObject(), pattern);
  }

  public boolean isPatternNonLocal(int pattern) {
    checkAccess();
    validatePatternIndex(pattern);
    return Native.patternNonLocal(getNativeObject(), pattern);
  }

  public boolean isPatternGuaranteedAtStep(int offset) {
    checkAccess();
    return Native.patternGuaranteedAtStep(getNativeObject(), offset);
  }

  public String getCaptureNameForId(int id) {
    checkAccess();
    return Native.captureNameForId(getNativeObject(), id);
  }

  public String getStringValueForId(int id) {
    checkAccess();
    return Native.stringValueForId(getNativeObject(), id);
  }

  public TSQuantifier getCaptureQuantifierForId(int pattern, int capture) {
    checkAccess();
    validatePatternIndex(pattern);
    return TSQuantifier.forId(Native.captureQuantifierForId(getNativeObject(), pattern, capture));
  }

  @Override
  protected void closeNativeObj() {
    Native.delete(getNativeObject());
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
   * The query string should contain one or more S-expression patterns. The query is
   * associated with a particular language, and can only be run on syntax nodes parsed with that
   * language.
   *
   * <p>If all of the given patterns are valid, this returns a <code>TSQuery</code>. If a pattern is
   * invalid, this returns <code>null</code>, and provides two pieces of information about the problem:
   * 1. The byte offset of the error is written to the {@link #errorOffset} parameter.
   * 2. The type of error is written to the {@link #errorType} parameter.
   *
   * @param language The {@link TSLanguage} for the query.
   * @param querySource    The query source.
   * @return The {@link TSQuery} object.
   */
  public static TSQuery create(TSLanguage language, String querySource) {
    if (language == null) {
      throw new IllegalArgumentException("Language cannot be null");
    }

    if (querySource == null || TextUtils.getTrimmedLength(querySource) == 0) {
      throw new IllegalArgumentException("Query cannot be null or blank");
    }

    final var query = TSObjectFactoryProvider.getFactory().createQuery(0);
    query.setNativeObject(Native.newQuery(query, language.getNativeObject(), querySource));
    return query;
  }

  /**
   * An empty query. Instances of this class are invalid queries and does not have any patterns,
   * capture names, etc. The <code>get*Count()</code> methods always return <code>0</code>, the
   * <code>get*(int index)</code> methods always throw an {@link IndexOutOfBoundsException} and
   * the <code>get*ForId(int id)</code> methods always throw {@link UnsupportedOperationException}.
   */
  private static final class EmptyQuery extends TSQuery {

    private EmptyQuery() {
      super(0);
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

  @GenerateNativeHeaders(fileName = "query")
  private static class Native {

    static {
      registerNatives();
    }

    @FastNative
    static native void registerNatives();

    @FastNative
    static native long newQuery(TSQuery query, long pointer, String source);

    @FastNative
    static native void delete(long query);

    @FastNative
    static native int captureCount(long query);

    @FastNative
    static native int patternCount(long query);

    @FastNative
    static native int stringCount(long query);

    @FastNative
    static native int startByteForPattern(long query, int pattern);

    @FastNative
    static native TSQueryPredicateStep[] predicatesForPattern(long query, int pattern);

    @FastNative
    static native boolean patternRooted(long query, int pattern);

    @FastNative
    static native boolean patternNonLocal(long query, int pattern);

    @FastNative
    static native boolean patternGuaranteedAtStep(long query, int byteOffset);

    @FastNative
    static native String captureNameForId(long query, int id);

    @FastNative
    static native String stringValueForId(long query, int id);

    @FastNative
    static native int captureQuantifierForId(long query, int pattern, int capture);
  }
}