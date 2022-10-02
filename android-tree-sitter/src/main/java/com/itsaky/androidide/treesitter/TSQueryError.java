package com.itsaky.androidide.treesitter;

/**
 * The type of a {@link TSQuery} error.
 *
 * @author Akash Yadav
 */
public enum TSQueryError {
  None(0),
  Syntax(1),
  NodeType(2),
  Field(3),
  Capture(4),
  Structure(5),
  Language(6);

  private final int type;

  TSQueryError(int type) {
    this.type = type;
  }

  public static TSQueryError valueOf(int type) {
    for (TSQueryError value : values()) {
      if (value.type == type) {
        return value;
      }
    }
    return null;
  }
}
