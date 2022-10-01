package com.itsaky.androidide.treesitter;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

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
        var symbol = root.getSymbol();
        assertThat(tree.getLanguage().getSymbolName(symbol)).isEqualTo("module");

        var start = root.getStartPoint();
        assertThat(0).isEqualTo(start.row);
        assertThat(0).isEqualTo(start.column);

        var end = root.getEndPoint();
        assertThat(2).isEqualTo(end.row);
        assertThat(12).isEqualTo(end.column);

        var type = root.getType();
        assertThat("module").isEqualTo(type);

        var startByte = root.getStartByte();
        assertThat(0).isEqualTo(startByte);

        var endByte = root.getEndByte();
        assertThat(sourceToParse.getBytes(java.nio.charset.StandardCharsets.UTF_8))
            .hasLength(endByte);

        var children = root.getChildCount();
        assertThat(1).isEqualTo(children);

        var namedChildren = root.getNamedChildCount();
        assertThat(1).isEqualTo(namedChildren);

        var isNamed = root.isNamed();
        assertThat(isNamed).isTrue();

        var isMissing = root.isMissing();
        assertThat(isMissing).isFalse();

        var isExtra = root.isExtra();
        assertThat(isExtra).isFalse();

        var hasChanges = root.hasChanges();
        assertThat(hasChanges).isFalse();

        var hasErrors = root.hasErrors();
        assertThat(hasErrors).isFalse();

        var isNull = root.isNull();
        assertThat(isNull).isFalse();

        var function = root.getChild(0);
        var fieldNameForChild = root.getFieldNameForChild(0);
        assertThat(fieldNameForChild).isNull();
        start = function.getStartPoint();
        assertThat(0).isEqualTo(start.row);
        assertThat(0).isEqualTo(start.column);

        end = function.getEndPoint();
        assertThat(2).isEqualTo(end.row);
        assertThat(12).isEqualTo(end.column);

        type = function.getType();
        assertThat("function_definition").isEqualTo(type);

        children = function.getChildCount();
        assertThat(5).isEqualTo(children);

        isNull = function.isNull();
        assertThat(isNull).isFalse();

        var body = function.getChildByFieldName("body");

        var parent = function.getParent();
        type = parent.getType();
        assertThat("module").isEqualTo(type);

        isNull = parent.isNull();
        assertThat(isNull).isFalse();
      }
    }
  }
}
