package com.itsaky.androidide.treesitter;

/**
 * Type of TSSymbol.
 *
 * @author Akash Yadav
 */
public enum TSSymbolType {
  REGULAR(0),
  ANONYMOUS(1),
  AUXILIARY(2);

  final int id;

  TSSymbolType(int id) {
    this.id = id;
  }

  /**
   * Get symbol type for the given type id.
   *
   * @param id The id.
   */
  public static TSSymbolType forId(int id) {
    for (TSSymbolType symbolType : values()) {
      if (symbolType.id == id) {
        return symbolType;
      }
    }
    return null;
  }
}
