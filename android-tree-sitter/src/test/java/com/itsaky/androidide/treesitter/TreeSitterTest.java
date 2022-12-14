package com.itsaky.androidide.treesitter;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class TreeSitterTest {
  static {
    System.load(System.getProperty("user.dir") + "/../build/host/libandroid-tree-sitter.so");
  }

  @Test
  public void test() {
    assertThat(TreeSitter.getLanguageVersion()).isEqualTo(14);
    assertThat(TreeSitter.getMinimumCompatibleLanguageVersion()).isEqualTo(13);
  }
}
