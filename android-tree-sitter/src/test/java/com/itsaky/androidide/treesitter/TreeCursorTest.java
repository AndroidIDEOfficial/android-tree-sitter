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

import com.itsaky.androidide.treesitter.python.TSLanguagePython;
import com.itsaky.androidide.treesitter.string.UTF16StringFactory;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class TreeCursorTest extends TreeSitterTest {

  @Test
  public void testWalk() throws UnsupportedEncodingException {
    try (TSParser parser = TSParser.create()) {
      parser.setLanguage(TSLanguagePython.getInstance());
      final var source =
          UTF16StringFactory.newString("def foo(bar, baz):\n  print(bar)\n  print(baz)");
      try (TSTree tree = parser.parseString(source)) {
        try (TSTreeCursor cursor = tree.getRootNode().walk()) {
          assertThat(cursor.getCurrentTreeCursorNode().getType()).isEqualTo("module");
          assertThat(cursor.getCurrentNode().getType()).isEqualTo("module");
          assertThat(cursor.gotoFirstChild()).isTrue();
          assertThat(cursor.getCurrentNode().getType()).isEqualTo("function_definition");
          assertThat(cursor.gotoFirstChild()).isTrue();

          assertThat(cursor.getCurrentNode().getType()).isEqualTo("def");
          assertThat(cursor.gotoNextSibling()).isTrue();
          assertThat(cursor.getCurrentNode().getType()).isEqualTo("identifier");
          assertThat(cursor.gotoNextSibling()).isTrue();
          assertThat(cursor.getCurrentNode().getType()).isEqualTo("parameters");
          assertThat(cursor.gotoNextSibling()).isTrue();
          assertThat(cursor.getCurrentNode().getType()).isEqualTo(":");
          assertThat(cursor.gotoNextSibling()).isTrue();
          assertThat(cursor.getCurrentNode().getType()).isEqualTo("block");
          assertThat(cursor.getCurrentFieldName()).isEqualTo("body");
          assertThat(cursor.gotoNextSibling()).isFalse();

          assertThat(cursor.gotoParent()).isTrue();
          assertThat(cursor.getCurrentNode().getType()).isEqualTo("function_definition");
          assertThat(cursor.gotoFirstChild()).isTrue();
        }
      }
    }
  }
}
