package com.itsaky.androidide.treesitter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

/**
 * @author Akash Yadav
 */
public class TreeTest extends TestBase {

  @Test
  public void testTreeCopy() throws UnsupportedEncodingException {
    try (final var parser = new TSParser()) {
      parser.setLanguage(TSLanguages.java());
      try (final var tree =
          parser.parseString(
              "class Main { void main() {} }", TSInputEncoding.TSInputEncodingUTF8)) {
        assertEquals(tree.getRootNode().getNodeString(), tree.copy().getRootNode().getNodeString());
      }
    }
  }
}
