package com.itsaky.androidide.treesitter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class NodeTest extends TestBase {

  @Test
  public void testGetChildren() throws UnsupportedEncodingException {
    try (TSParser parser = new TSParser()) {
      parser.setLanguage(TSLanguages.python());
      final var sourceToParse = "def foo(bar, baz):\n  print(bar)\n  print(baz)";
      try (TSTree tree =
          parser.parseString(
              "def foo(bar, baz):\n  print(bar)\n  print(baz)",
              TSInputEncoding.TSInputEncodingUTF16)) {
        var root = tree.getRootNode();
        var type = root.getType();
        assertEquals("module", type);

        var startByte = root.getStartByte();
        assertEquals(0, startByte);

        var endByte = root.getEndByte();
        assertEquals(sourceToParse.getBytes(StandardCharsets.UTF_8).length, endByte);

        var children = root.getChildCount();
        assertEquals(1, children);

        var namedChildren = root.getNamedChildCount();
        assertEquals(1, namedChildren);

        var isNamed = root.isNamed();
        assertTrue(isNamed);

        var isMissing = root.isMissing();
        assertFalse(isMissing);

        var isExtra = root.isExtra();
        assertFalse(isExtra);

        var hasChanges = root.hasChanges();
        assertFalse(hasChanges);

        var hasErrors = root.hasErrors();
        assertFalse(hasErrors);

        var function = root.getChild(0);
        type = function.getType();
        assertEquals("function_definition", type);

        children = function.getChildCount();
        assertEquals(5, children);

        var parent = function.getParent();
        type = parent.getType();
        assertEquals("module", type);
      }
    }
  }
}
