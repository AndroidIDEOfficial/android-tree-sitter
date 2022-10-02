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
      try (final var tree = parser.parseString("public class MyClass { int x = 0; }");
          final var query = new TSQuery(tree.getLanguage(), "(class_body)");
          final var cursor = new TSQueryCursor()) {
        cursor.exec(query, tree.getRootNode());
        TSQueryMatch match;
        while((match = cursor.nextMatch()) != null) {
          assertThat(match.getCaptures()).isNotEmpty();
        }
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
