package com.itsaky.androidide.treesitter;

/**
 * @author Akash Yadav
 */
public class TSRange {
  private int startByte;
  private int endByte;
  private TSPoint startPoint;
  private TSPoint endPoint;

  public TSRange(int startByte, int endByte, TSPoint startPoint, TSPoint endPoint) {
    this.startByte = startByte;
    this.endByte = endByte;
    this.startPoint = startPoint;
    this.endPoint = endPoint;
  }

  public int getStartByte() {
    return startByte;
  }

  public int getEndByte() {
    return endByte;
  }

  public TSPoint getStartPoint() {
    return startPoint;
  }

  public TSPoint getEndPoint() {
    return endPoint;
  }
}
