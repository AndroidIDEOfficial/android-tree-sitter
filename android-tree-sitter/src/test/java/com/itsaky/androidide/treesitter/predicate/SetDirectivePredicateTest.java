/*
 *  This file is part of android-tree-sitter.
 *
 *  android-tree-sitter library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  android-tree-sitter library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *  along with android-tree-sitter.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.itsaky.androidide.treesitter.predicate;

import static com.google.common.truth.Truth.assertThat;

import com.itsaky.androidide.treesitter.TSParser;
import com.itsaky.androidide.treesitter.TSQuery;
import com.itsaky.androidide.treesitter.TSQueryCursor;
import com.itsaky.androidide.treesitter.TSQueryError;
import com.itsaky.androidide.treesitter.TreeSitterTest;
import com.itsaky.androidide.treesitter.java.TSLanguageJava;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * @author Akash Yadav
 */
@RunWith(RobolectricTestRunner.class)
public class SetDirectivePredicateTest extends TreeSitterTest {

  @Test
  public void test_simple_set_directive() {
    final var lang = TSLanguageJava.getInstance();
    try (final var parser = TSParser.create()) {
      parser.setLanguage(lang);
      String javaSource = "public class Main { void a() {} }";
      String querySource = "(method_declaration name: (identifier)+ @method_name (#set! \"a\" \"b\"))";

      try (final var tree = parser.parseString(javaSource); final var query = TSQuery.create(lang,
        querySource); final var cursor = TSQueryCursor.create()) {

        assertThat(query.canAccess()).isTrue();
        assertThat(query.getErrorType()).isEqualTo(TSQueryError.None);

        cursor.addPredicateHandler(new SetDirectiveHandler());
        cursor.exec(query, tree.getRootNode());

        final var match = cursor.nextMatch();
        assertThat(
          cursor.nextMatch()).isNull(); // single match is expected as there is only 1 method

        final var metadata = match.getMetadata();
        assertThat(metadata).isNotNull();
        assertThat(metadata.getString("a")).isEqualTo("b");
        assertThat(metadata.containsKey("method_name")).isFalse();
      }
    }
  }

  @Test
  public void test_simple_set_directive_for_capture() {
    final var lang = TSLanguageJava.getInstance();
    try (final var parser = TSParser.create()) {
      parser.setLanguage(lang);
      String javaSource = "public class Main { void a() {} }";
      String querySource = "(method_declaration name: (identifier)+ @method_name (#set! @method_name \"a\" \"b\"))";

      try (final var tree = parser.parseString(javaSource); final var query = TSQuery.create(lang,
        querySource); final var cursor = TSQueryCursor.create()) {

        assertThat(query.canAccess()).isTrue();
        assertThat(query.getErrorType()).isEqualTo(TSQueryError.None);

        cursor.addPredicateHandler(new SetDirectiveHandler());
        cursor.exec(query, tree.getRootNode());

        final var match = cursor.nextMatch();
        assertThat(
          cursor.nextMatch()).isNull(); // single match is expected as there is only 1 method

        final var metadata = match.getMetadata();
        assertThat(metadata).isNotNull();
        assertThat(metadata.containsKey("method_name")).isTrue();

        final var captureMeta = metadata.getCaptureMetadata("method_name");
        assertThat(captureMeta).isNotNull();
        assertThat(captureMeta.getString("a")).isEqualTo("b");
      }
    }
  }

  @Test
  public void test_simple_set_directive_with_unquoted_key() {
    final var lang = TSLanguageJava.getInstance();
    try (final var parser = TSParser.create()) {
      parser.setLanguage(lang);
      String javaSource = "public class Main { void a() {} }";
      String querySource = "(method_declaration name: (identifier)+ @method_name (#set! a \"b\"))";

      try (final var tree = parser.parseString(javaSource); final var query = TSQuery.create(lang,
        querySource); final var cursor = TSQueryCursor.create()) {

        assertThat(query.canAccess()).isTrue();
        assertThat(query.getErrorType()).isEqualTo(TSQueryError.None);

        cursor.addPredicateHandler(new SetDirectiveHandler());
        cursor.exec(query, tree.getRootNode());

        final var match = cursor.nextMatch();
        assertThat(
          cursor.nextMatch()).isNull(); // single match is expected as there is only 1 method

        final var metadata = match.getMetadata();
        assertThat(metadata).isNotNull();
        assertThat(metadata.getString("a")).isEqualTo("b");
        assertThat(metadata.containsKey("method_name")).isFalse();
      }
    }
  }

  @Test
  public void test_simple_set_directive_with_unquoted_key_value() {
    final var lang = TSLanguageJava.getInstance();
    try (final var parser = TSParser.create()) {
      parser.setLanguage(lang);
      String javaSource = "public class Main { void a() {} }";
      String querySource = "(method_declaration name: (identifier)+ @method_name (#set! a b))";

      try (final var tree = parser.parseString(javaSource); final var query = TSQuery.create(lang,
        querySource); final var cursor = TSQueryCursor.create()) {

        assertThat(query.canAccess()).isTrue();
        assertThat(query.getErrorType()).isEqualTo(TSQueryError.None);

        cursor.addPredicateHandler(new SetDirectiveHandler());
        cursor.exec(query, tree.getRootNode());

        final var match = cursor.nextMatch();
        assertThat(
          cursor.nextMatch()).isNull(); // single match is expected as there is only 1 method

        final var metadata = match.getMetadata();
        assertThat(metadata).isNotNull();
        assertThat(metadata.getString("a")).isEqualTo("b");
        assertThat(metadata.containsKey("method_name")).isFalse();
      }
    }
  }

  @Test
  public void test_simple_set_directive_with_unquoted_numeric_key_value() {
    final var lang = TSLanguageJava.getInstance();
    try (final var parser = TSParser.create()) {
      parser.setLanguage(lang);
      String javaSource = "public class Main { void a() {} }";
      String querySource = "(method_declaration name: (identifier)+ @method_name (#set! 5 6))";

      try (final var tree = parser.parseString(javaSource); final var query = TSQuery.create(lang,
        querySource); final var cursor = TSQueryCursor.create()) {

        assertThat(query.canAccess()).isTrue();
        assertThat(query.getErrorType()).isEqualTo(TSQueryError.None);

        cursor.addPredicateHandler(new SetDirectiveHandler());
        cursor.exec(query, tree.getRootNode());

        final var match = cursor.nextMatch();
        assertThat(
          cursor.nextMatch()).isNull(); // single match is expected as there is only 1 method

        final var metadata = match.getMetadata();
        assertThat(metadata).isNotNull();
        assertThat(metadata.getString("5")).isEqualTo("6");
        assertThat(metadata.containsKey("method_name")).isFalse();
      }
    }
  }
}
