/*
 *  This file is part of AndroidIDE.
 *
 *  AndroidIDE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  AndroidIDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/\>.
 */

package com.itsaky.androidide.treesitter;

import static com.google.common.truth.Truth.assertThat;
import static com.itsaky.androidide.treesitter.TestUtils.readString;

import com.itsaky.androidide.treesitter.java.TSLanguageJava;
import com.itsaky.androidide.treesitter.json.TSLanguageJson;
import com.itsaky.androidide.treesitter.kotlin.TSLanguageKotlin;
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
      try (TSTree tree = parser.parseString("print(\"hi\")")) {
        assertThat(tree.getRootNode().getNodeString())
            .isEqualTo(
                "(module (expression_statement (call function: (identifier) arguments: (argument_list (string string_content: (string_content))))))");
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
          parser.parseString(readString(Paths.get("./src/test/resources/CodeEditor.java.txt")))) {
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
          parser.parseString(readString(Paths.get("./src/test/resources/View.java.txt")))) {
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

      // the start byte below is invalid as it is in the middle of a character
      // It should fail in this case
      parser.setIncludedRanges(
          new TSRange[] {new TSRange(21, 65, new TSPoint(0, 21), new TSPoint(0, 65))});
      final var source = "public class Main { class Inner { public static void main() {} } }";
      try (final var tree = parser.parseString(source)) {
        assertThat(tree).isNotNull();

        final var root = tree.getRootNode();
        assertThat(root).isNotNull();

        // errorneous type
        assertThat(root.getChild(0).getType()).isEqualTo("ERROR");
      }
    }
  }

  @Test
  public void testJsonGrammar() {
    try (final var parser = new TSParser()) {
      parser.setLanguage(TSLanguageJson.newInstance());

      final var source = "{\n" +
        "    \"string\": \"value\",\n" +
        "    \"boolean\": true,\n" +
        "    \"number\": 1234,\n" +
        "    \"null\": null,\n" +
        "\n" +
        "    \"object\": {\n" +
        "\n" +
        "    },\n" +
        "\n" +
        "    \"array\": [\n" +
        "        \"array_element\"\n" +
        "    ]\n" +
        "}";

      try (final var tree = parser.parseString(source)) {
        final var rootNode = tree.getRootNode();
        assertThat(rootNode).isNotNull();
        assertThat(rootNode.getChildCount()).isGreaterThan(0);
      }
    }
  }

  @Test
  public void testKotlinGrammar() {
    try (final var parser = new TSParser()) {
      parser.setLanguage(TSLanguageKotlin.newInstance());

      final var source = "class Main {\n" +
        "    fun main() {\n" +
        "        println(\"Hello World\")\n" +
        "    }\n" +
        "}";

      try (final var tree = parser.parseString(source)) {
        final var rootNode = tree.getRootNode();
        assertThat(rootNode).isNotNull();
        assertThat(rootNode.getChildCount()).isGreaterThan(0);
        assertThat(rootNode.getNodeString()).isEqualTo("(source_file (class_declaration (type_identifier) (class_body (function_declaration (simple_identifier) (function_body (statements (call_expression (simple_identifier) (call_suffix (value_arguments (value_argument (string_literal)))))))))))");
      }
    }
  }
}
