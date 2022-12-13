package com.itsaky.androidide.treesitter;

/**
 * @author Akash Yadav
 */
public class TSQueryPredicateStep {

  private Type cachedType = null;
  private int type = -1;
  private int valueId = -1;

  public Type getType() {
    if (cachedType == null) {
      cachedType = Type.forId(this.type);
    }
    return cachedType;
  }

  public int getValueId() {
    return valueId;
  }

  public enum Type {
    Done(0),
    Capture(1),
    String(2);

    private final int id;

    Type(int id) {
      this.id = id;
    }

    public static Type forId(int typeId) {
      for (Type value : values()) {
        if (value.id == typeId) {
          return value;
        }
      }
      return null;
    }
  }
}
