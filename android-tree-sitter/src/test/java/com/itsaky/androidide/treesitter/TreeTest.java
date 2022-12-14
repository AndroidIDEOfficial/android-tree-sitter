package com.itsaky.androidide.treesitter;

import static com.google.common.truth.Truth.assertThat;

import com.itsaky.androidide.treesitter.java.TSLanguageJava;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

/**
 * @author Akash Yadav
 */
public class TreeTest extends TreeSitterTest {

  @Test
  public void testTreeCopy() throws UnsupportedEncodingException {
    try (final var parser = new TSParser()) {
      parser.setLanguage(TSLanguageJava.newInstance());
      try (final var tree =
          parser.parseString(
              "class Main { void main() {} }", TSInputEncoding.TSInputEncodingUTF8)) {
        assertThat(tree.getLanguage().pointer).isEqualTo(TSLanguageJava.newInstance().pointer);
        assertThat(tree.copy().getRootNode().getNodeString())
            .isEqualTo(tree.getRootNode().getNodeString());
      }
    }
  }
}
