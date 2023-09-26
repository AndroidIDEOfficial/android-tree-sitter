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

import java.util.Objects;

/**
 * Lookahead iterators can be useful to generate suggestions and improve syntax error diagnostics.
 * To get symbols valid in an ERROR node, use the lookahead iterator on its first leaf node state.
 * For `MISSING` nodes, a lookahead iterator created on the previous non-extra leaf node may be
 * appropriate.
 * <p>
 * Repeatedly using {@link #next()} and {@link #getCurrentSymbol()} will generate valid symbols in
 * the given parse state. Newly created lookahead iterators will contain the `ERROR` symbol.
 *
 * @author Akash Yadav
 */
public class TSLookaheadIterator extends TSNativeObject {

  private TSLanguage language;

  /**
   * Create a new lookahead iterator for the given language and parse state.
   * <p>
   * This returns <code>null</code> if state is invalid for the language.
   *
   * @param language The {@link TSLanguage} for this lookahead iterator.
   * @param stateId  The parse state.
   * @return The {@link TSLookaheadIterator} or <code>null</code> if there was an error creating the
   * iterator.
   */
  public static TSLookaheadIterator newInstance(TSLanguage language, short stateId) {
    language.checkAccess();
    final var pointer = Native.newIterator(language.pointer, stateId);
    if (pointer == 0) {
      return null;
    }

    return new TSLookaheadIterator(language, pointer);
  }

  /**
   * Creates a new {@link TSLookaheadIterator} instance with the given pointer.
   *
   * @param pointer The pointer to the native object. Subclasses can initialize this
   *                {@link TSLookaheadIterator} with pointer set to 0 and then set the pointer
   *                lazily.
   */
  private TSLookaheadIterator(TSLanguage language, long pointer) {
    super(pointer);
    Objects.requireNonNull(language);
    this.language = language;
  }

  /**
   * Advance the lookahead iterator to the next symbol.
   * <p>
   * This returns `true` if there is a new symbol and `false` otherwise.
   */
  public boolean next() {
    checkAccess();
    return Native.next(pointer);
  }

  /**
   * Get the current symbol of the lookahead iterator;
   */
  public short getCurrentSymbol() {
    checkAccess();
    return Native.currentSymbol(pointer);
  }

  /**
   * Get the current symbol's name of the lookahead iterator;
   */
  public String getCurrentSymbolName() {
    checkAccess();
    return Native.currentSymbolName(pointer);
  }

  /**
   * Reset the lookahead iterator to another state.
   * <p>
   * This returns `true` if the iterator was reset to the given state and `false` otherwise.
   */
  public boolean resetState(short stateId) {
    checkAccess();
    return Native.resetState(pointer, stateId);
  }

  /**
   * Reset the lookahead iterator.
   * <p>
   * This returns `true` if the language was set successfully and `false` otherwise.
   */
  public boolean reset(TSLanguage language, short stateId) {
    checkAccess();
    language.checkAccess();
    final var result = Native.reset(pointer, language.pointer, stateId);
    if (result) {
      this.language = language;
    }

    return result;
  }

  /**
   * Get the current language of the lookahead iterator.
   */
  public TSLanguage getLanguage() {
    checkAccess();
    return language;
  }

  @Override
  protected void closeNativeObj() {
    Native.delete(pointer);
  }

  private static final class Native {

    public static native long newIterator(long language, short stateId);

    public static native void delete(long pointer);

    public static native boolean next(long pointer);

    public static native short currentSymbol(long pointer);

    public static native String currentSymbolName(long pointer);

    public static native boolean resetState(long pointer, short stateId);

    public static native boolean reset(long pointer, long pointer1, short stateId);
  }
}
