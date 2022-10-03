package com.itsaky.androidide.treesitter;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class TestBase {
  static {
    System.load(System.getProperty("user.dir") + "/../output/host/libts.so");
  }

  @Test
  public void test() {
    assertThat(TreeSitter.getLanguageVersion()).isEqualTo(14);
    assertThat(TreeSitter.getMinimumCompatibleLanguageVersion()).isEqualTo(13);
  }
}
