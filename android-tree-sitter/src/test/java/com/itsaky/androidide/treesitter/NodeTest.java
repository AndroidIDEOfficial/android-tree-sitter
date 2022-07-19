package com.itsaky.androidide.treesitter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

public class NodeTest extends TestBase {

  @Test
  public void testGetChildren() throws UnsupportedEncodingException {
    try (TSParser parser = new TSParser()) {
      parser.setLanguage(TSLanguages.python());
      try (TSTree tree =
          parser.parseString(
              "def foo(bar, baz):\n  print(bar)\n  print(baz)",
              TSInputEncoding.TSInputEncodingUTF16)) {
        TSNode root = tree.getRootNode();
        assertEquals(1, root.getChildCount());
        assertEquals("module", root.getType());
        assertEquals(0, root.getStartByte());
        assertEquals(44, root.getEndByte());

        TSNode function = root.getChild(0);
        assertEquals("function_definition", function.getType());
        assertEquals(5, function.getChildCount());
      }
    }
  }
}
