package com.itsaky.androidide.treesitter;

import static com.google.common.truth.Truth.assertThat;
import static com.itsaky.androidide.treesitter.TSInputEncoding.TSInputEncodingUTF16;
import static com.itsaky.androidide.treesitter.TestUtils.readString;
import static java.nio.file.Paths.get;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

public class TreeCursorTest extends TreeSitterTest {

  @Test
  public void testWalk() throws UnsupportedEncodingException {
    try (TSParser parser = new TSParser()) {
      parser.setLanguage(TSLanguages.python());
      try (TSTree tree =
          parser.parseString(
              "def foo(bar, baz):\n  print(bar)\n  print(baz)", TSInputEncodingUTF16)) {
        try (TSTreeCursor cursor = tree.getRootNode().walk()) {
          assertThat(cursor.getCurrentTreeCursorNode().getType()).isEqualTo("module");
          assertThat(cursor.getCurrentNode().getType()).isEqualTo("module");
          assertThat(cursor.gotoFirstChild()).isTrue();
          assertThat(cursor.getCurrentNode().getType()).isEqualTo("function_definition");
          assertThat(cursor.gotoFirstChild()).isTrue();

          assertThat(cursor.getCurrentNode().getType()).isEqualTo("def");
          assertThat(cursor.gotoNextSibling()).isTrue();
          assertThat(cursor.getCurrentNode().getType()).isEqualTo("identifier");
          assertThat(cursor.gotoNextSibling()).isTrue();
          assertThat(cursor.getCurrentNode().getType()).isEqualTo("parameters");
          assertThat(cursor.gotoNextSibling()).isTrue();
          assertThat(cursor.getCurrentNode().getType()).isEqualTo(":");
          assertThat(cursor.gotoNextSibling()).isTrue();
          assertThat(cursor.getCurrentNode().getType()).isEqualTo("block");
          assertThat(cursor.getCurrentFieldName()).isEqualTo("body");
          assertThat(cursor.gotoNextSibling()).isFalse();

          assertThat(cursor.gotoParent()).isTrue();
          assertThat(cursor.getCurrentNode().getType()).isEqualTo("function_definition");
          assertThat(cursor.gotoFirstChild()).isTrue();
        }
      }
    }
  }
}
