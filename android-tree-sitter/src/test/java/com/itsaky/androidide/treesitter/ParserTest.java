package com.itsaky.androidide.treesitter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RunWith(JUnit4.class)
public class ParserTest extends TestBase {

  @Test
  public void testParse() throws UnsupportedEncodingException {
    try (TSParser parser = new TSParser()) {
      parser.setLanguage(TSLanguages.python());
      try (TSTree tree =
          parser.parseString("print(\"hi\")", TSInputEncoding.TSInputEncodingUTF16)) {
        assertEquals(
            "(module (expression_statement (call function: (identifier) arguments: (argument_list (string)))))",
            tree.getRootNode().getNodeString());
      }
    }
  }

  @Test
  public void testCodeEditor() throws Throwable {
    final long start = System.currentTimeMillis();
    try (TSParser parser = new TSParser()) {
      parser.setLanguage(TSLanguages.java());
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
      parser.setLanguage(TSLanguages.java());
      try (var tree =
          parser.parseString(
              readString(Paths.get("./src/test/resources/View.java.txt")),
              TSInputEncoding.TSInputEncodingUTF16)) {
        System.out.println(tree.getRootNode().getNodeString());
        System.out.println("\nParsed View.java in: " + (System.currentTimeMillis() - start) + "ms");
      }
    }
  }

  private String readString(Path path) {
    try (final var reader = Files.newBufferedReader(path)) {
      final var sb = new StringBuilder();
      for (var line = reader.readLine(); line != null; line = reader.readLine()) {
        sb.append(line).append("\n");
      }
      return sb.toString();
    } catch (Throwable err) {
      throw new RuntimeException(err);
    }
  }
}
