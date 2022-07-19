package com.itsaky.androidide.treesitter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

public class TreeCursorTest extends TestBase {

  @Test
  public void testWalk() throws UnsupportedEncodingException {
    try (TSParser parser = new TSParser()) {
      parser.setLanguage(TSLanguages.python());
      try (TSTree tree =
          parser.parseString(
              "def foo(bar, baz):\n  print(bar)\n  print(baz)",
              TSInputEncoding.TSInputEncodingUTF16)) {
        try (TSTreeCursor cursor = tree.getRootNode().walk()) {
          assertEquals("module", cursor.getCurrentTreeCursorNode().getType());
          assertEquals("module", cursor.getCurrentNode().getType());
          assertTrue(cursor.gotoFirstChild());
          assertEquals("function_definition", cursor.getCurrentNode().getType());
          assertTrue(cursor.gotoFirstChild());

          assertEquals("def", cursor.getCurrentNode().getType());
          assertTrue(cursor.gotoNextSibling());
          assertEquals("identifier", cursor.getCurrentNode().getType());
          assertTrue(cursor.gotoNextSibling());
          assertEquals("parameters", cursor.getCurrentNode().getType());
          assertTrue(cursor.gotoNextSibling());
          assertEquals(":", cursor.getCurrentNode().getType());
          assertTrue(cursor.gotoNextSibling());
          assertEquals("block", cursor.getCurrentNode().getType());
          assertEquals("body", cursor.getCurrentFieldName());
          assertFalse(cursor.gotoNextSibling());

          assertTrue(cursor.gotoParent());
          assertEquals("function_definition", cursor.getCurrentNode().getType());
          assertTrue(cursor.gotoFirstChild());
        }
      }
    }
  }
}
