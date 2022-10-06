package com.itsaky.androidide.treesitter;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

/**
 * @author Akash Yadav
 */
public class QueryTest extends TreeSitterTest {

  @Test
  public void queryTest() throws Exception {
    try (final var parser = new TSParser()) {
      parser.setLanguage(TSLanguages.java());
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
    try (TSQuery query = new TSQuery(TSLanguages.java(), "(class_declaration")) {
      assertThat(query.pointer).isEqualTo(0);
      assertThat(query.getErrorOffset()).isEqualTo("(class_declaration".length());
      assertThat(query.getErrorType()).isEqualTo(TSQueryError.Syntax);
    }
  }

  @Test
  public void testQueryNoResult() {
    try (final var parser = new TSParser()) {
      parser.setLanguage(TSLanguages.java());
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
}
