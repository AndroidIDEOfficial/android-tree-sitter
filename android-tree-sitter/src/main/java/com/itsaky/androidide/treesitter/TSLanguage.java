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

import android.content.Context;
import com.itsaky.androidide.treesitter.util.TSObjectFactoryProvider;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * A tree-sitter language.
 *
 * @author Akash Yadav
 */
public class TSLanguage extends TSNativeObject {

  private static final Pattern LANG_NAME = Pattern.compile("^[a-zA-Z_]\\w*$");

  private final String name;

  /**
   * The pointer to the library handle if this language was loaded using
   * {@link TSLanguage#loadLanguage(String, String)}.
   */
  private long libHandle;

  /**
   * Create a new {@link TSLanguage} instance with the given name and pointer.
   *
   * @param name    The name of the language. This is used to efficiently remove externally loaded
   *                languages in {@link TSLanguageCache}.
   * @param pointer The pointer to the language implementation in C.
   */
  protected TSLanguage(String name, long pointer) {
    this(name, new long[]{pointer, 0});
  }

  protected TSLanguage(String name, long[] pointers) {
    super(0);

    if (pointers == null) {
      throw new IllegalArgumentException("Cannot create TSLanguage from null pointers");
    }

    if (pointers.length != 2) {
      throw new IllegalArgumentException("There must be exactly 2 elements the pointers array");
    }

    this.name = name;
    this.pointer = pointers[0];
    this.libHandle = pointers[1];
  }

  public static TSLanguage create(String name, long pointer) {
    return create(name, new long[]{pointer, 0});
  }

  public static TSLanguage create(String name, long[] pointers) {
    return TSObjectFactoryProvider.getFactory().createLanguage(name, pointers);
  }

  /**
   * Get the name of this language.
   *
   * @return The name of the language.
   * @see TSLanguage#TSLanguage(String, long)
   * @see TSLanguage#loadLanguage(String, String)
   */
  public String getName() {
    return name;
  }

  /**
   * Get the number of distinct node types in the language.
   */
  public int getSymbolCount() {
    checkAccess();
    return Native.symCount(this.pointer);
  }

  public int getFieldCount() {
    checkAccess();
    return Native.fldCount(this.pointer);
  }

  public String getSymbolName(int symbol) {
    checkAccess();
    return Native.symName(this.pointer, symbol);
  }

  public int getSymbolForTypeString(String name, boolean isNamed) {
    checkAccess();
    final var bytes = name.getBytes(StandardCharsets.UTF_8);
    return Native.symForName(this.pointer, bytes, bytes.length, isNamed);
  }

  public String getFieldNameForId(int id) {
    checkAccess();
    return Native.fldNameForId(this.pointer, id);
  }

  public int getFieldIdForName(String name) {
    checkAccess();
    final var bytes = name.getBytes(StandardCharsets.UTF_8);
    return Native.fldIdForName(this.pointer, bytes, bytes.length);
  }

  public TSSymbolType getSymbolType(int symbol) {
    checkAccess();
    return TSSymbolType.forId(Native.symType(this.pointer, symbol));
  }

  /**
   * Get the number of valid states in this language.
   */
  public int getStateCount() {
    checkAccess();
    return Native.stateCount(pointer);
  }

  /**
   * Get the next parse state. Combine this with lookahead iterators to generate completion
   * suggestions or valid symbols in error nodes.
   */
  public short getNextState(short stateId, short symbol) {
    checkAccess();
    return Native.nextState(pointer, stateId, symbol);
  }

  public int getLanguageVersion() {
    checkAccess();
    return Native.langVer(this.pointer);
  }

  /**
   * Returns whether this language is external i.e. loaded with
   * {@link TSLanguage#loadLanguage(String, String)}.
   *
   * @return <code>true</code> if this language is external, <code>false</code> otherwise.
   */
  public boolean isExternal() {
    return this.libHandle != 0;
  }

  @Override
  public void close() {
    if (isExternal()) {
      Native.dlclose(this.libHandle);
      this.libHandle = 0;

      // Language instance loading using loadLanguage(...) must not be reused
      TSLanguageCache.remove(this);
    }

    super.close();
  }

  @Override
  protected void closeNativeObj() {
    // no-op
  }

  /**
   * Loads the tree-sitter language and returns an instance of {@link TSLanguage}. This method will
   * load the library using <code>dlopen</code> and keep a reference to the library handle as long
   * the {@link TSLanguage#close()} is not called.
   * <p>
   * The native library is opened using <code>dlopen</code>. The {@link TSLanguage} instances
   * created with this method must be closed with {@link TSLanguage#close()}. This makes sure that
   * the underlying native library handle is closed as well.
   *
   * @param context The context used to retrive the
   *                {@link android.content.pm.ApplicationInfo#nativeLibraryDir nativeLibraryDir}.
   *                The name of the gramar's shared library is then appended to the native library
   *                directory. For example :
   *                <pre>nativeLibraryDir + "/libtree-sitter-" + lang + ".so"</pre>.
   * @param lang    The name of the language, without <code>tree-sitter-</code> prefix (e.g. 'java',
   *                'kotlin', etc).
   * @see TSLanguage#loadLanguage(String, String)
   */
  public static TSLanguage loadLanguage(Context context, String lang) {
    final var libraryPath =
      context.getApplicationInfo().nativeLibraryDir + "/libtree-sitter-" + lang + ".so";
    return loadLanguage(libraryPath, lang);
  }

  /**
   * Loads the tree-sitter language and returns an instance of {@link TSLanguage}. This method will
   * load the library using <code>dlopen</code> and keep a reference to the library handle as long
   * the {@link TSLanguage#close()} is not called.
   * <p>
   * The native library is opened using <code>dlopen</code>. The {@link TSLanguage} instances
   * created with this method must be closed with {@link TSLanguage#close()}. This makes sure that
   * the underlying native library handle is closed as well.
   *
   * @param libraryPath The absolute path to the shared library.
   * @param lang        The name of the language, without {@code tree-sitter-} prefix (e.g. 'java',
   *                    'kotlin', etc).
   * @return The {@link TSLanguage} instance for the given language name, or <code>null</code> if
   * the language cannot be loaded.
   * @throws IllegalArgumentException If the given language name is invalid.
   */
  public static TSLanguage loadLanguage(String libraryPath, String lang) {
    validateLangName(lang);

    var language = TSLanguageCache.get(lang);
    if (language != null) {
      return language;
    }

    final long[] pointers = Native.loadLanguage(libraryPath, "tree_sitter_" + lang);
    if (pointers == null) {
      return null;
    }

    language = TSLanguage.create(lang, pointers);
    TSLanguageCache.cache(lang, language);
    return language;
  }

  private static void validateLangName(String lang) {
    final var matcher = LANG_NAME.matcher(lang);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid language name: " + lang);
    }
  }

  private static class Native {

    private static native int symCount(long ptr);

    private static native int fldCount(long ptr);

    private static native int symForName(long ptr, byte[] name, int length, boolean named);

    private static native String symName(long lngPtr, int sym);

    private static native String fldNameForId(long ptr, int id);

    private static native int fldIdForName(long ptr, byte[] name, int length);

    private static native int symType(long ptr, int sym);

    private static native int langVer(long ptr);

    private static native long[] loadLanguage(String sharedLib, String func);

    private static native void dlclose(long libhandle);

    public static native int stateCount(long pointer);

    public static native short nextState(long pointer, short stateId, short symbol);
  }
}