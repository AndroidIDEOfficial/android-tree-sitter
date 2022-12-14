package com.itsaky.androidide.treesitter.java;

import com.itsaky.androidide.treesitter.TSLanguage;

/**
 * Tree Sitter Java language.
 *
 * @author Akash Yadav
 */
public class TSLanguageJava {

  private TSLanguageJava() {
    throw new UnsupportedOperationException();
  }

  public static TSLanguage newInstance() {
    return new TSLanguage(Native.newInstance());
  }

  public static class Native {
    public static native long newInstance();
  }
}
