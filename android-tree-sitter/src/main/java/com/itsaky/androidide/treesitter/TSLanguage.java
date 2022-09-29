package com.itsaky.androidide.treesitter;

import java.nio.charset.StandardCharsets;

/**
 * A tree-sitter language.
 *
 * @author Akash Yadav
 */
public class TSLanguage {

  private final long pointer;

  /**
   * Create a new {@link TSLanguage} instance with the given pointer.
   *
   * @param pointer The pointer to the language implementation in C.
   */
  public TSLanguage(long pointer) {
    this.pointer = pointer;
  }

  /** Get the number of distinct node types in the language. */
  public int getSymbolCount() {
    return TSLanguageNative.symCount(this.pointer);
  }

  public int getFieldCount() {
    return TSLanguageNative.fldCount(this.pointer);
  }

  public String getSymbolName(int symbol) {
    return TSLanguageNative.symName(this.pointer, symbol);
  }

  public int getSymbolForTypeString(String name, boolean isNamed) {
    final var bytes = name.getBytes(StandardCharsets.UTF_8);
    return TSLanguageNative.symForName(this.pointer, bytes, bytes.length, isNamed);
  }

  public String getFieldNameForId(int id) {
    return TSLanguageNative.fldNameForId(this.pointer, id);
  }

  public int getFieldIdForName(String name) {
    final var bytes = name.getBytes(StandardCharsets.UTF_8);
    return TSLanguageNative.fldIdForName(this.pointer, bytes, bytes.length);
  }

  public TSSymbolType getSymbolType(int symbol) {
    return TSSymbolType.forId(TSLanguageNative.symType(this.pointer, symbol));
  }

  public int getLanguageVersion() {
    return TSLanguageNative.langVer(this.pointer);
  }

  private static class TSLanguageNative {
    private static native int symCount(long ptr);

    private static native int fldCount(long ptr);

    private static native int symForName(long ptr, byte[] name, int length, boolean named);

    private static native String symName(long lngPtr, int sym);

    private static native String fldNameForId(long ptr, int id);

    private static native int fldIdForName(long ptr, byte[] name, int length);

    private static native int symType(long ptr, int sym);

    private static native int langVer(long ptr);
  }
}
