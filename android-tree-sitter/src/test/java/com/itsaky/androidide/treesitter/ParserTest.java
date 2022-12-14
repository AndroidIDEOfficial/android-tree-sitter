package com.itsaky.androidide.treesitter;

import static com.google.common.truth.Truth.assertThat;
import static com.itsaky.androidide.treesitter.TestUtils.readString;

import com.itsaky.androidide.treesitter.java.TSLanguageJava;
import com.itsaky.androidide.treesitter.python.TSLanguagePython;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;

@RunWith(JUnit4.class)
public class ParserTest extends TreeSitterTest {

  @Test
  public void testParse() throws UnsupportedEncodingException {
    try (TSParser parser = new TSParser()) {
      parser.setLanguage(TSLanguagePython.newInstance());
      try (TSTree tree =
          parser.parseString("print(\"hi\")", TSInputEncoding.TSInputEncodingUTF16)) {
        assertThat(tree.getRootNode().getNodeString())
            .isEqualTo(
                "(module (expression_statement (call function: (identifier) arguments: (argument_list (string)))))");
      }
    }
  }

  @Test
  public void testCodeEditor() throws Throwable {
    final long start = System.currentTimeMillis();
    try (TSParser parser = new TSParser()) {
      parser.setLanguage(TSLanguageJava.newInstance());
      assertThat(parser.getLanguage().pointer).isEqualTo(TSLanguageJava.newInstance().pointer);
      try (var tree =
          parser.parseString(
              readString(Paths.get("./src/test/resources/CodeEditor.java.txt")),
              TSInputEncoding.TSInputEncodingUTF16)) {
        System.out.println(tree.getRootNode().getNodeString());
        System.out.println(
            "\nParsed CodeEditor.java in: " + (System.currentTimeMillis() - start) + "ms");
      }
    }
  }

  @Test
  public void testView() throws Throwable {
    final long start = System.currentTimeMillis();
    try (TSParser parser = new TSParser()) {
      parser.setLanguage(TSLanguageJava.newInstance());
      try (var tree =
          parser.parseString(
              readString(Paths.get("./src/test/resources/View.java.txt")),
              TSInputEncoding.TSInputEncodingUTF16)) {
        System.out.println(tree.getRootNode().getNodeString());
        System.out.println("\nParsed View.java in: " + (System.currentTimeMillis() - start) + "ms");
      }
    }
  }

  @Test
  public void testTimeout() throws UnsupportedEncodingException {
    final var timeout = 1000L; // 1 millisecond
    final var start = System.currentTimeMillis();
    try (final var parser = new TSParser()) {
      parser.setLanguage(TSLanguageJava.newInstance());
      parser.setTimeout(timeout);
      assertThat(parser.getTimeout()).isEqualTo(timeout);
      try (final var tree =
          parser.parseString(readString(Paths.get("./src/test/resources/View.java.txt")))) {
        final var timeConsumed = System.currentTimeMillis() - start;
        assertThat(tree).isNull();
        System.out.println("Parsed in " + timeConsumed + "ms");
      }
    }
  }

  @Test
  public void testIncrementalParsing() throws UnsupportedEncodingException {
    try (final var parser = new TSParser()) {
      parser.setLanguage(TSLanguageJava.newInstance());
      parser.setIncludedRanges(
          new TSRange[] {new TSRange(21, 65, new TSPoint(0, 21), new TSPoint(0, 65))});
      final var source = "public class Main { class Inner { public static void main() {} } }";
      try (final var tree = parser.parseString(source)) {
        assertThat(tree).isNotNull();

        final var root = tree.getRootNode();
        assertThat(root).isNotNull();

        // errorneous type
        assertThat(root.getChild(0).getType()).isEqualTo("local_variable_declaration");
      }
    }
  }
}
