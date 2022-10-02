package com.itsaky.androidide.treesitter;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

/**
 * @author Akash Yadav
 */
public class QueryTest extends TestBase {

  @Test
  public void implementationTest() throws Exception {
    try (final var parser = new TSParser()) {
      parser.setLanguage(TSLanguages.java());
      try (final var tree =
          parser.parseString("public class MyClass { int x = 0; public void myFunc(){} }")) {
        var query =
            new TSQuery(
                TSLanguages.java(), "(class_declaration name: (identifier) @MyClass)");
        var cursor = new TSQueryCursor();
        cursor.exec(query, tree.getRootNode());
        final var match = cursor.nextMatch();
        assertThat(match).isNotNull();
        assertThat(match.getCaptures()).hasLength(1);
        query.close();
        cursor.close();
      } catch (Throwable err) {
        throw new RuntimeException(err);
      }
    }
  }

  @Test
  public void testError() throws Exception {
    try (TSQuery query = new TSQuery(TSLanguages.java(), "(class_declaration")) {
      assertThat(query.pointer).isEqualTo(0);
      assertThat(query.getErrorOffset()).isEqualTo("(class_declaration".length());
      assertThat(query.getErrorType()).isEqualTo(TSQueryError.Syntax);
    }
  }
}
