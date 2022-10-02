package com.itsaky.androidide.treesitter;

/**
 * @author Akash Yadav
 */
public class TSQueryMatch {
  private int id;
  private int patternIndex;
  private TSQueryCapture[] captures;

  public int getId() {
    return id;
  }

  public int getPatternIndex() {
    return patternIndex;
  }

  public TSQueryCapture[] getCaptures() {
    return captures;
  }

  public TSQueryCapture getCapture(int index) {
    return captures[index];
  }
}
