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

import static java.nio.file.Paths.get;

import com.itsaky.androidide.treesitter.java.TSLanguageJava;
import com.itsaky.androidide.treesitter.string.UTF16String;
import com.itsaky.androidide.treesitter.string.UTF16StringFactory;
import com.itsaky.androidide.treesitter.xml.TSLanguageXml;

import org.junit.Test;

import java.nio.file.Paths;

/**
 * @author Akash Yadav
 */
public class QueryTest extends TreeSitterTest {

  @Test
  public void queryTest() throws Exception {
    try (final var parser = new TSParser()) {
      parser.setLanguage(TSLanguageJava.newInstance());
      try (final var tree =
          parser.parseString("public class MyClass { int x = 0; public void myFunc(){} }")) {
        var query =
            new TSQuery(tree.getLanguage(), "(class_declaration name: (identifier) @MyClass)");
        var cursor = new TSQueryCursor();
        cursor.exec(query, tree.getRootNode());

        assertThat(query.getCaptureCount()).isEqualTo(1);
        assertThat(query.getPatternCount()).isEqualTo(1);
        assertThat(query.getStringCount()).isEqualTo(0);

        var match = cursor.nextMatch();
        assertThat(match).isNotNull();
        assertThat(match.getCaptures()).isNotNull();
        assertThat(match.getCaptures()).hasLength(1);
        assertThat(cursor.nextMatch()).isNull();

        var captureNames = query.getCaptureNames();
        assertThat(captureNames).hasLength(query.getCaptureCount());
        captureNames = new String[query.getCaptureCount()];
        for (int i = 0; i < captureNames.length; i++) {
          captureNames[i] = query.getCaptureNameForId(0);
        }

        query.close();
        cursor.close();

        query = new TSQuery(tree.getLanguage(), "(method_declaration name: (identifier) @myFunc)");
        cursor = new TSQueryCursor();
        cursor.exec(query, tree.getRootNode());
        match = cursor.nextMatch();
        assertThat(match).isNotNull();
        assertThat(match.getCaptures()).isNotNull();
        assertThat(match.getCaptures()).hasLength(1);
        assertThat(cursor.nextMatch()).isNull();

        query.close();
        cursor.close();
      } catch (Throwable err) {
        throw new RuntimeException(err);
      }
    }
  }

  @Test
  public void testQuerySyntaxError() throws Exception {
    try (TSQuery query = new TSQuery(TSLanguageJava.newInstance(), "(class_declaration")) {
      assertThat(query.pointer).isEqualTo(0);
      assertThat(query.getErrorOffset()).isEqualTo("(class_declaration".length());
      assertThat(query.getErrorType()).isEqualTo(TSQueryError.Syntax);
    }
  }

  @Test
  public void testQueryNoResult() {
    try (final var parser = new TSParser()) {
      parser.setLanguage(TSLanguageJava.newInstance());
      try (final var tree = parser.parseString("public class MyClass {}")) {
        var query =
            new TSQuery(
                tree.getLanguage(), "(method_declaration name: (identifier) @NoDeclWithThisName)");
        var cursor = new TSQueryCursor();
        cursor.exec(query, tree.getRootNode());
        var match = cursor.nextMatch();
        assertThat(match).isNull();
        query.close();
        cursor.close();
      } catch (Throwable err) {
        throw new RuntimeException(err);
      }
    }
  }

  @Test
  public void testHighlightsFunctionality() {
    try (final var parser = new TSParser()) {
      parser.setLanguage(TSLanguageJava.newInstance());
      try (final var tree = parser.parseString(readString(get("./src/test/resources/View.java.txt")))) {
        var query =
          new TSQuery(
            tree.getLanguage(), readString(get("./src/test/resources/highlights-java.scm")));
        var cursor = new TSQueryCursor();
        cursor.exec(query, tree.getRootNode());
        TSQueryMatch match;
        while((match = cursor.nextMatch()) != null) {
          for (TSQueryCapture capture : match.getCaptures()) {
            assertThat(capture).isNotNull();
            final var captureName = query.getCaptureNameForId(capture.getIndex());
            assertThat(captureName).isNotNull();
            assertThat(captureName.trim()).isNotEmpty();
          }
        }
        query.close();
        cursor.close();
      } catch (Throwable err) {
        throw new RuntimeException(err);
      }
    }
  }

  @Test
  public void testOffsetsInUtf16String() throws Exception {
    try (final var parser = new TSParser()) {
      parser.setLanguage(TSLanguageJava.newInstance());
      final var source = UTF16StringFactory.newString(readString(Paths.get("./src/test/resources/CodeEditor.java.txt")));
      try (final var tree = parser.parseString(source)) {
        final var root = tree.getRootNode();
        assertThat(root.getStartByte()).isEqualTo(0);
        assertThat(root.getEndByte()).isEqualTo(source.byteLength());

        final var query = new TSQuery(parser.getLanguage(), readString(Paths.get("./src/test/resources/highlights-java.scm")));
        final var cursor = new TSQueryCursor();
        cursor.exec(query, root);

        TSQueryMatch match;
        while((match = cursor.nextMatch()) != null) {
          assertThat(match.getCaptures()).isNotEmpty();
          for (final var capture : match.getCaptures()) {
            assertThat(capture).isNotNull();
            assertThat(capture.getIndex()).isAtLeast(0);
            assertThat(capture.getIndex()).isAtMost(query.getCaptureCount() - 1);

            final var captureIndex = capture.getIndex();
            final var name = query.getCaptureNameForId(captureIndex);
            final var node = capture.getNode();

            assertThat(name).isNotNull();
            assertThat(name).isNotEmpty();

            assertThat(node).isNotNull();
            assertThat(node.getStartByte()).isAtLeast(0);
            assertThat(node.getEndByte()).isAtMost(source.byteLength());

            final var subseq = source.subseqBytes(node.getStartByte(), node.getEndByte());
            assertThat(subseq).isNotNull();
            assertThat(subseq.length()).isGreaterThan(0);
          }
        }

        query.close();
      }
    }
  }

  @Test
  public void testXmlBlocksQuery() throws Exception {
    try (final var parser = new TSParser()) {
      parser.setLanguage(TSLanguageXml.newInstance());
      final var source = UTF16StringFactory.newString(readString(Paths.get("./src/test/resources/test.xml")));
      try (final var tree = parser.parseString(source)) {
        final var root = tree.getRootNode();
        assertThat(root.getStartByte()).isEqualTo(0);
        assertThat(root.getEndByte()).isEqualTo(source.byteLength());

        final var query = new TSQuery(parser.getLanguage(), readString(Paths.get("./src/test/resources/blocks-xml.scm")));
        final var cursor = new TSQueryCursor();
        cursor.exec(query, root);

        TSQueryMatch match;
        while((match = cursor.nextMatch()) != null) {
          assertThat(match.getCaptures()).isNotEmpty();
          for (final var capture : match.getCaptures()) {
            assertThat(capture).isNotNull();
            assertThat(capture.getIndex()).isAtLeast(0);
            assertThat(capture.getIndex()).isAtMost(query.getCaptureCount() - 1);

            final var captureIndex = capture.getIndex();
            final var name = query.getCaptureNameForId(captureIndex);
            final var node = capture.getNode();

            assertThat(name).isNotNull();
            assertThat(name).isNotEmpty();

            assertThat(node).isNotNull();
            assertThat(node.getStartByte()).isAtLeast(0);
            assertThat(node.getEndByte()).isAtMost(source.byteLength());

            final var subseq = source.subseqBytes(node.getStartByte(), node.getEndByte());
            assertThat(subseq).isNotNull();
            assertThat(subseq.length()).isGreaterThan(0);
          }
        }

        query.close();
      }
    }
  }
}