package com.itsaky.androidide.treesitter;

import static com.google.common.truth.Truth.assertThat;

import com.itsaky.androidide.treesitter.python.TSLanguagePython;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class NodeTest extends TreeSitterTest {

  @Test
  public void multiTest() throws UnsupportedEncodingException {
    try (TSParser parser = new TSParser()) {
      parser.setLanguage(TSLanguagePython.newInstance());
      final var sourceToParse = "def foo(bar, baz):\n  print(bar)\n  print(baz)";
      try (TSTree tree = parser.parseString(sourceToParse, TSInputEncoding.TSInputEncodingUTF8)) {
        var root = tree.getRootNode();
        assertThat(root.getTree().getPointer()).isEqualTo(tree.getPointer());

        var symbol = root.getSymbol();
        assertThat(tree.getLanguage().getSymbolName(symbol)).isEqualTo("module");
        assertThat(root.getFieldNameForChild(0)).isNull();

        var start = root.getStartPoint();
        assertThat(start.row).isEqualTo(0);
        assertThat(start.column).isEqualTo(0);

//        var end = root.getEndPoint();
//        assertThat(end.row).isEqualTo(2);
//        assertThat(end.column).isEqualTo(12);

        var type = root.getType();
        assertThat("module").isEqualTo(type);

        var startByte = root.getStartByte();
        assertThat(0).isEqualTo(startByte);

        var endByte = root.getEndByte();
        assertThat(sourceToParse.getBytes(StandardCharsets.UTF_8))
            .hasLength(endByte);

        var children = root.getChildCount();
        assertThat(children).isEqualTo(1);

        var namedChildren = root.getNamedChildCount();
        assertThat(namedChildren).isEqualTo(1);

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
        start = function.getStartPoint();
        assertThat(start.row).isEqualTo(0);
        assertThat(start.column).isEqualTo(0);
        assertThat(function.isEqualTo(function)).isTrue();
        assertThat(function.getFieldNameForChild(1)).isEqualTo("name");

//        end = function.getEndPoint();
//        assertThat(end.row).isEqualTo(2);
//        assertThat(end.column).isEqualTo(12);

        type = function.getType();
        assertThat("function_definition").isEqualTo(type);

        children = function.getChildCount();
        assertThat(children).isEqualTo(5);

        isNull = function.isNull();
        assertThat(isNull).isFalse();

        var def = function.getChild(0);
        assertThat(def.isNull()).isFalse();
        assertThat(def.getType()).isEqualTo("def");
        assertThat(def.getNextSibling().getType()).isEqualTo("identifier");
        assertThat(def.getNextNamedSibling().isEqualTo(def.getNextSibling())).isTrue();
        assertThat(def.getNextSibling().getNextSibling().getType()).isEqualTo("parameters");
        assertThat(def.getNextSibling().getNextSibling().getNextSibling().getType()).isEqualTo(":");
        assertThat(
                def.getNextSibling()
                    .getNextSibling()
                    .getNextSibling()
                    .getPreviousSibling()
                    .getType())
            .isEqualTo("parameters");

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
