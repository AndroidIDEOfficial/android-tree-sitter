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

package com.itsaky.androidide.treesitter;

import static com.google.common.truth.Truth.assertThat;

import com.itsaky.androidide.treesitter.java.TSLanguageJava;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * @author Akash Yadav
 */

@RunWith(RobolectricTestRunner.class)
public class LanguageCacheTest extends TreeSitterTest {

  @Test
  public void testCacheAndRetrieval() {
    final var lang = TSLanguageJava.getInstance();
    assertThat(lang).isEqualTo(TSLanguageCache.get("java"));
    assertThat(lang).isEqualTo(TSLanguageCache.get(lang.getNativeObject()));
  }

  @Test
  public void testLangFromParser() {
    var lang = TSLanguageJava.getInstance();
    try (final var parser = TSParser.create()) {
      parser.setLanguage(lang);
      assertThat(parser.getLanguage()).isEqualTo(lang);
    }
  }

  @Test
  public void testLangFromQuery() {
    var lang = TSLanguageJava.getInstance();
    try (final var parser = TSParser.create()) {
      parser.setLanguage(lang);
      try (final var tree = parser.parseString("public class Main {}")) {
        assertThat(tree.getLanguage()).isEqualTo(lang);
        assertThat(TSLanguageCache.get(lang.getNativeObject())).isEqualTo(tree.getLanguage());
      }
    }
  }

  @Test
  public void testExternalLanguageCache() {
    String libraryPath = System.getProperty("user.dir") + "/src/test/resources/libtree-sitter-c";
    var lang = TSLanguage.loadLanguage(libraryPath, "c");
    assertThat(lang).isNotNull();
    assertThat(lang.canAccess()).isTrue();
    assertThat(TSLanguageCache.get("c")).isEqualTo(lang);
    assertThat(TSLanguageCache.get(lang.getNativeObject())).isEqualTo(lang);
    lang.close();

    assertThat(lang.canAccess()).isFalse();
    assertThat(TSLanguageCache.get("c")).isNull();
  }

  @Test
  public void testExternalLanguageCloseWithCloseExternal() {
    String libraryPath = System.getProperty("user.dir") + "/src/test/resources/libtree-sitter-c";
    var lang = TSLanguage.loadLanguage(libraryPath, "c");
    assertThat(lang).isNotNull();
    assertThat(lang.canAccess()).isTrue();
    assertThat(TSLanguageCache.get("c")).isEqualTo(lang);
    assertThat(TSLanguageCache.get(lang.getNativeObject())).isEqualTo(lang);

    TSLanguageCache.closeExternal();
    assertThat(lang.canAccess()).isFalse();
    assertThat(TSLanguageCache.get("c")).isNull();
  }
}
