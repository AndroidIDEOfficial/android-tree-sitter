package com.itsaky.androidide.treesitter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.UnsupportedEncodingException;
import java.nio.file.*;
import org.junit.jupiter.api.Test;

public class ParserTest extends TestBase {

  @Test
  void testParse() throws UnsupportedEncodingException {
    try (TSParser parser = new TSParser()) {
      parser.setLanguage(TSLanguages.python());
      try (TSTree tree = parser.parseString("print(\"hi\")", TSInputEncoding.TSInputEncodingUTF16)) {
        assertEquals(
          "(module (expression_statement (call function: (identifier) arguments: (argument_list (string)))))",
          tree.getRootNode().getNodeString()
        );
      }
    }
  }

  @Test
  void testCodeEditor() throws Throwable {
    final long start = System.currentTimeMillis();
    try (TSParser parser = new TSParser()) {
      parser.setLanguage(TSLanguages.java());
      try (var tree = parser.parseString(Files.readString(Paths.get("./src/test/resources/CodeEditor.java.txt")), TSInputEncoding.TSInputEncodingUTF16)) {
        System.out.println(tree.getRootNode().getNodeString());
        System.out.println("\nParsed CodeEditor.java in: " + (System.currentTimeMillis() - start) + "ms");
      }
    }
  }

  @Test
  void testView() throws Throwable {
    final long start = System.currentTimeMillis();
    try (TSParser parser = new TSParser()) {
      parser.setLanguage(TSLanguages.java());
      try (var tree = parser.parseString(Files.readString(Paths.get("./src/test/resources/View.java.txt")), TSInputEncoding.TSInputEncodingUTF16)) {
        System.out.println(tree.getRootNode().getNodeString());
        System.out.println("\nParsed View.java in: " + (System.currentTimeMillis() - start) + "ms");
      }
    }
  }
}
