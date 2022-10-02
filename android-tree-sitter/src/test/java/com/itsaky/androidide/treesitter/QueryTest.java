package com.itsaky.androidide.treesitter;

import org.junit.Test;

/**
 * @author Akash Yadav
 */
public class QueryTest extends TestBase {

  @Test
  public void test() throws Exception {
    try (final var parser = new TSParser()) {
      parser.setLanguage(TSLanguages.java());
      try (final var tree = parser.parseString("public class MyClass { int x = 0; }");
          final var query = new TSQuery(tree.getLanguage(), "(class_body)");
          final var cursor = new TSQueryCursor()) {
        cursor.exec(query, tree.getRootNode());
      }
    }
  }
}
