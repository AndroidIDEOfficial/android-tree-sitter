package com.itsaky.androidide.treesitter;

public class TestBase {

  static {
    System.load(System.getenv("JAVA_TREE_SITTER"));
  }
}
