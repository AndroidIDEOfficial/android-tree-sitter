package com.itsaky.androidide.treesitter;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

/**
 * @author Akash Yadav
 */
public class TreeTest extends TreeSitterTest {

  @Test
  public void testTreeCopy() throws UnsupportedEncodingException {
    try (final var parser = new TSParser()) {
      parser.setLanguage(TSLanguages.java());
      try (final var tree =
          parser.parseString(
              "class Main { void main() {} }", TSInputEncoding.TSInputEncodingUTF8)) {
        assertThat(tree.getLanguage().pointer).isEqualTo(TSLanguages.java().pointer);
        assertThat(tree.copy().getRootNode().getNodeString())
            .isEqualTo(tree.getRootNode().getNodeString());
      }
    }
  }
}
