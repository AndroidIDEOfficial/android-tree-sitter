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
import com.itsaky.androidide.treesitter.string.UTF16StringFactory;
import com.itsaky.androidide.treesitter.xml.TSLanguageXml;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * @author Akash Yadav
 */
@RunWith(RobolectricTestRunner.class)
public class QueryTest extends TreeSitterTest {

  @Test
  public void queryTest() throws Exception {
    try (final var parser = TSParser.create()) {
      parser.setLanguage(TSLanguageJava.getInstance());
      try (final var tree = parser.parseString(
        "public class MyClass { int x = 0; public void myFunc(){} }")) {
        var query = TSQuery.create(tree.getLanguage(),
          "(class_declaration name: (identifier) @MyClass)");
        var cursor = TSQueryCursor.create();
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

        query = TSQuery.create(tree.getLanguage(),
          "(method_declaration name: (identifier) @myFunc)");
        cursor = TSQueryCursor.create();
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
    try (TSQuery query = TSQuery.create(TSLanguageJava.getInstance(), "(class_declaration")) {
      assertThat(query.getNativeObject()).isEqualTo(0);
      assertThat(query.getErrorOffset()).isEqualTo("(class_declaration".length());
      assertThat(query.getErrorType()).isEqualTo(TSQueryError.Syntax);
    }
  }

  @Test
  public void testQueryNoResult() {
    try (final var parser = TSParser.create()) {
      parser.setLanguage(TSLanguageJava.getInstance());
      try (final var tree = parser.parseString("public class MyClass {}")) {
        var query = TSQuery.create(tree.getLanguage(),
          "(method_declaration name: (identifier) @NoDeclWithThisName)");
        var cursor = TSQueryCursor.create();
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
    try (final var parser = TSParser.create()) {
      parser.setLanguage(TSLanguageJava.getInstance());
      try (final var tree = parser.parseString(
        readString(get("./src/test/resources/View.java.txt")))) {
        var query = TSQuery.create(tree.getLanguage(),
          readString(get("./src/test/resources/highlights-java.scm")));
        var cursor = TSQueryCursor.create();
        cursor.exec(query, tree.getRootNode());
        TSQueryMatch match;
        while ((match = cursor.nextMatch()) != null) {
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
    try (final var parser = TSParser.create()) {
      parser.setLanguage(TSLanguageJava.getInstance());
      final var source = UTF16StringFactory.newString(
        readString(Paths.get("./src/test/resources/CodeEditor.java.txt")));
      try (final var tree = parser.parseString(source)) {
        final var root = tree.getRootNode();
        assertThat(root.getStartByte()).isEqualTo(0);
        assertThat(root.getEndByte()).isEqualTo(source.byteLength());

        final var query = TSQuery.create(parser.getLanguage(),
          readString(Paths.get("./src/test/resources/highlights-java.scm")));
        final var cursor = TSQueryCursor.create();
        cursor.exec(query, root);

        TSQueryMatch match;
        while ((match = cursor.nextMatch()) != null) {
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
    try (final var parser = TSParser.create()) {
      parser.setLanguage(TSLanguageXml.getInstance());
      final var source = UTF16StringFactory.newString(
        readString(Paths.get("./src/test/resources/test.xml")));
      try (final var tree = parser.parseString(source)) {
        final var root = tree.getRootNode();
        assertThat(root.getStartByte()).isEqualTo(0);
        assertThat(root.getEndByte()).isEqualTo(source.byteLength());

        final var query = TSQuery.create(parser.getLanguage(),
          readString(Paths.get("./src/test/resources/blocks-xml.scm")));
        final var cursor = TSQueryCursor.create();
        cursor.exec(query, root);

        TSQueryMatch match;
        while ((match = cursor.nextMatch()) != null) {
          assertThat(match.getCaptures()).isNotEmpty();
          for (final var capture : match.getCaptures()) {
            assertThat(capture).isNotNull();
            assertThat(capture.getIndex()).isAtLeast(0);
            assertThat(capture.getIndex()).isAtMost(query.getCaptureCount() - 1);
            assertThat(match.getCapture(capture.getIndex())).isEqualTo(capture);

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
  public void testEmptyQuery() throws Exception {
    try (final var query = TSQuery.EMPTY) {
      assertThat(query.canAccess()).isFalse();
      assertThat(query.getPatternCount()).isEqualTo(0);
      assertThat(query.getCaptureCount()).isEqualTo(0);
      assertThat(query.getStringCount()).isEqualTo(0);
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testQueryConstructionError() throws Exception {
    TSQuery.create(null, "not-empty").close();
  }

  @Test
  public void testQueryQuantifierZero() {
    final var lang = TSLanguageJava.getInstance();
    try (final var parser = TSParser.create()) {
      parser.setLanguage(lang);
      String javaSource = "public class Main { void a() {} void b() {} void c() {} void d() {} void e() {} }";
      String querySource = "(class_declaration name: (identifier) @class_name)";

      try (final var tree = parser.parseString(javaSource); final var query = TSQuery.create(lang,
        querySource); final var cursor = TSQueryCursor.create()) {

        assertThat(query.canAccess()).isTrue();
        assertThat(query.getErrorType()).isEqualTo(TSQueryError.None);

        cursor.exec(query, tree.getRootNode());

        // pattern 0 -> method_declaration
        // capture 1 -> invalid capture id, result must be TSQuantifier.Zero
        final var quantifier = query.getCaptureQuantifierForId(0, 1);
        assertThat(quantifier).isNotNull();
        assertThat(quantifier).isEqualTo(TSQuantifier.Zero);
      }
    }
  }

  @Test
  public void testQueryQuantifierZeroOrOne() {
    final var lang = TSLanguageJava.getInstance();
    try (final var parser = TSParser.create()) {
      parser.setLanguage(lang);
      String javaSource = "public class Main { void a() {} void b() {} void c() {} void d() {} void e() {} }";
      String querySource = "(class_declaration name: (identifier)? @class_name)";

      try (final var tree = parser.parseString(javaSource); final var query = TSQuery.create(lang,
        querySource); final var cursor = TSQueryCursor.create()) {

        assertThat(query.canAccess()).isTrue();
        assertThat(query.getErrorType()).isEqualTo(TSQueryError.None);

        cursor.exec(query, tree.getRootNode());

        // pattern 0 -> method_declaration
        // capture 0 -> @class_name
        final var quantifier = query.getCaptureQuantifierForId(0, 0);
        assertThat(quantifier).isNotNull();
        assertThat(quantifier).isEqualTo(TSQuantifier.ZeroOrOne);
      }
    }
  }

  @Test
  public void testQueryQuantifierZeroOrMore() {
    final var lang = TSLanguageJava.getInstance();
    try (final var parser = TSParser.create()) {
      parser.setLanguage(lang);
      String javaSource = "public class Main { void a() {} void b() {} void c() {} void d() {} void e() {} }";
      String querySource = "(method_declaration name: (identifier)* @method_name)";

      try (final var tree = parser.parseString(javaSource); final var query = TSQuery.create(lang,
        querySource); final var cursor = TSQueryCursor.create()) {

        assertThat(query.canAccess()).isTrue();
        assertThat(query.getErrorType()).isEqualTo(TSQueryError.None);

        cursor.exec(query, tree.getRootNode());

        // pattern 0 -> method_declaration
        // capture 0 -> @method_name
        final var quantifier = query.getCaptureQuantifierForId(0, 0);
        assertThat(quantifier).isNotNull();
        assertThat(quantifier).isEqualTo(TSQuantifier.ZeroOrMore);
      }
    }
  }

  @Test
  public void testQueryQuantifierOne() {
    final var lang = TSLanguageJava.getInstance();
    try (final var parser = TSParser.create()) {
      parser.setLanguage(lang);
      String javaSource = "public class Main { void a() {} void b() {} void c() {} void d() {} void e() {} }";
      String querySource = "(method_declaration name: (_) @method_name)";

      try (final var tree = parser.parseString(javaSource); final var query = TSQuery.create(lang,
        querySource); final var cursor = TSQueryCursor.create()) {

        assertThat(query.canAccess()).isTrue();
        assertThat(query.getErrorType()).isEqualTo(TSQueryError.None);

        cursor.exec(query, tree.getRootNode());

        // pattern 0 -> method_declaration
        // capture 0 -> @method_name
        final var quantifier = query.getCaptureQuantifierForId(0, 0);
        assertThat(quantifier).isNotNull();
        assertThat(quantifier).isEqualTo(TSQuantifier.One);
      }
    }
  }

  @Test
  public void testQueryQuantifierOneOrMore() {
    final var lang = TSLanguageJava.getInstance();
    try (final var parser = TSParser.create()) {
      parser.setLanguage(lang);
      String javaSource = "public class Main { void a() {} void b() {} void c() {} void d() {} void e() {} }";
      String querySource = "(method_declaration name: (identifier)+ @method_name)";

      try (final var tree = parser.parseString(javaSource); final var query = TSQuery.create(lang,
        querySource); final var cursor = TSQueryCursor.create()) {

        assertThat(query.canAccess()).isTrue();
        assertThat(query.getErrorType()).isEqualTo(TSQueryError.None);

        cursor.exec(query, tree.getRootNode());

        var count = 0;
        while (cursor.nextMatch() != null) {
          ++count;
        }

        assertThat(count).isEqualTo(5); // 5 methods have been declared

        // pattern 0 -> method_declaration
        // capture 0 -> @method_name
        final var quantifier = query.getCaptureQuantifierForId(0, 0);
        assertThat(quantifier).isNotNull();
        assertThat(quantifier).isEqualTo(TSQuantifier.OneOrMore);
      }
    }
  }

  @Test
  public void testQueryCursorIterator() {
    final var lang = TSLanguageJava.getInstance();
    try (final var parser = TSParser.create()) {
      parser.setLanguage(lang);
      String javaSource = "public class Main { void a() {} void b() {} void c() {} void d() {} void e() {} }";
      String querySource = "(method_declaration name: (identifier)+ @method_name)";

      try (final var tree = parser.parseString(javaSource); final var query = TSQuery.create(lang,
        querySource); final var cursor = TSQueryCursor.create()) {

        assertThat(query.canAccess()).isTrue();
        assertThat(query.getErrorType()).isEqualTo(TSQueryError.None);

        cursor.exec(query, tree.getRootNode());

        var count = 0;
        var iterator = cursor.iterator();
        while (iterator.hasNext()) {
          iterator.next();
          ++count;
        }

        // 5 methods have been declared
        // so iterator should return exactly 5 elements
        assertThat(count).isEqualTo(5);

        try {
          iterator.next();
          throw new IllegalStateException("Iterator should not have thrown NoSuchElementException");
        } catch (NoSuchElementException e) {
          // ignored
        }
      }
    }
  }

  @Test(expected = NullPointerException.class)
  public void cursorShouldFailOnNullNode() {
    final var lang = TSLanguageJava.getInstance();
    String querySource = "(method_declaration name: (identifier)+ @method_name)";

    try (final var query = TSQuery.create(lang,
      querySource); final var cursor = TSQueryCursor.create()) {

      assertThat(query.canAccess()).isTrue();
      assertThat(query.getErrorType()).isEqualTo(TSQueryError.None);

      cursor.exec(query, null);

      throw new IllegalStateException("TSQueryCursor should have failed on null node");
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void cursorShouldFailOnEditedNode() {
    final var lang = TSLanguageJava.getInstance();
    try (final var parser = TSParser.create()) {
      parser.setLanguage(lang);
      String javaSource = "public class Main { void a() {} void b() {} void c() {} void d() {} void e() {} }";
      String querySource = "(method_declaration name: (identifier)+ @method_name)";

      try (final var tree = parser.parseString(javaSource); final var query = TSQuery.create(lang,
        querySource); final var cursor = TSQueryCursor.create()) {

        assertThat(query.canAccess()).isTrue();
        assertThat(query.getErrorType()).isEqualTo(TSQueryError.None);

        final var rootNode = tree.getRootNode();

        tree.edit(TSInputEdit.create(0, 0, 1, TSPoint.create(0, 0), TSPoint.create(0, 0),
          TSPoint.create(0, 1)));

        assertThat(rootNode.hasChanges()).isTrue();

        cursor.exec(query, rootNode);

        throw new IllegalStateException("TSQueryCursor should have failed on null node");
      }
    }
  }

  @Test
  public void cursorIteratorShouldFailOnEditedNode() {
    final var lang = TSLanguageJava.getInstance();
    try (final var parser = TSParser.create()) {
      parser.setLanguage(lang);
      String javaSource = "public class Main { void a() {} void b() {} void c() {} void d() {} void e() {} }";
      String querySource = "(method_declaration name: (identifier)+ @method_name)";

      try (final var tree = parser.parseString(javaSource); final var query = TSQuery.create(lang,
        querySource); final var cursor = TSQueryCursor.create()) {

        assertThat(query.canAccess()).isTrue();
        assertThat(query.getErrorType()).isEqualTo(TSQueryError.None);

        final var rootNode = tree.getRootNode();

        assertThat(rootNode.hasChanges()).isFalse();

        // execute query before edition
        cursor.exec(query, rootNode);

        // should return elements while not edited
        final var iterator = cursor.iterator();
        assertThat(iterator.hasNext()).isTrue();

        // edit tree
        tree.edit(TSInputEdit.create(0, 0, 1, TSPoint.create(0, 0), TSPoint.create(0, 0),
          TSPoint.create(0, 1)));

        // should NOT return elements after edition
        assertThat(iterator.hasNext()).isFalse();
      }
    }
  }

  @Test
  public void cursorShouldAllowEditedNodeIfExplicitlyOpted() {
    final var lang = TSLanguageJava.getInstance();
    try (final var parser = TSParser.create()) {
      parser.setLanguage(lang);
      String javaSource = "public class Main { void a() {} void b() {} void c() {} void d() {} void e() {} }";
      String querySource = "(method_declaration name: (identifier)+ @method_name)";

      try (final var tree = parser.parseString(javaSource); final var query = TSQuery.create(lang,
        querySource); final var cursor = TSQueryCursor.create()) {

        assertThat(query.canAccess()).isTrue();
        assertThat(query.getErrorType()).isEqualTo(TSQueryError.None);

        final var rootNode = tree.getRootNode();

        tree.edit(TSInputEdit.create(0, 0, 1, TSPoint.create(0, 0), TSPoint.create(0, 0),
          TSPoint.create(0, 1)));

        assertThat(rootNode.hasChanges()).isTrue();

        // executing the query not should throw exception if explicitly opted for changed node queries
        cursor.setAllowChangedNodes(true);
        cursor.exec(query, rootNode);
      }
    }
  }

  @Test
  public void cursorIteratorShouldAllowEditedNodeIfExplicitlyOpted() {
    final var lang = TSLanguageJava.getInstance();
    try (final var parser = TSParser.create()) {
      parser.setLanguage(lang);
      String javaSource = "public class Main { void a() {} void b() {} void c() {} void d() {} void e() {} }";
      String querySource = "(method_declaration name: (identifier)+ @method_name)";

      try (final var tree = parser.parseString(javaSource); final var query = TSQuery.create(lang,
        querySource); final var cursor = TSQueryCursor.create()) {

        assertThat(query.canAccess()).isTrue();
        assertThat(query.getErrorType()).isEqualTo(TSQueryError.None);

        final var rootNode = tree.getRootNode();

        assertThat(rootNode.hasChanges()).isFalse();

        // opt-in for changed node queries and execute the query
        cursor.setAllowChangedNodes(true);
        cursor.exec(query, rootNode);

        // should return elements while not edited
        final var iterator = cursor.iterator();
        assertThat(iterator.hasNext()).isTrue();

        // edit tree
        tree.edit(TSInputEdit.create(0, 0, 1, TSPoint.create(0, 0), TSPoint.create(0, 0),
          TSPoint.create(0, 1)));

        // should NOT return elements after edition
        assertThat(iterator.hasNext()).isTrue();
      }
    }
  }
}