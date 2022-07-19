package com.itsaky.androidide.treesitter;

public class TestBase {
  static {
    System.load(System.getProperty("user.dir") + "/../output/host/libts.so");
  }
}
