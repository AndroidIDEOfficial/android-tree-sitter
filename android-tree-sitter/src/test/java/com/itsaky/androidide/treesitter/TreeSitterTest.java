package com.itsaky.androidide.treesitter;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class TreeSitterTest {
  static {
    String hostDir = System.getProperty("user.dir") + "/../build/host";
    System.load(hostDir + "/libandroid-tree-sitter.so");
    System.load(hostDir + "/libtree-sitter-java.so");
  }

  @Test
  public void test() {
    assertThat(TreeSitter.getLanguageVersion()).isEqualTo(14);
    assertThat(TreeSitter.getMinimumCompatibleLanguageVersion()).isEqualTo(13);
  }
}
