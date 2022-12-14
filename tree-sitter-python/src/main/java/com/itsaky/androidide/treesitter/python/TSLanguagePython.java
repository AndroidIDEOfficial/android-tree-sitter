package com.itsaky.androidide.treesitter.python;

import com.itsaky.androidide.treesitter.TSLanguage;

/**
 * Tree Sitter Python language.
 *
 * @author Akash Yadav
 */
public class TSLanguagePython {

  private TSLanguagePython() {
    throw new UnsupportedOperationException();
  }

  public static TSLanguage newInstance() {
    return new TSLanguage(Native.newInstance());
  }

  public static class Native {
    public static native long newInstance();
  }
}
