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

import com.itsaky.androidide.treesitter.java.TSLanguageJava;
import java.io.UnsupportedEncodingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * @author Akash Yadav
 */
@RunWith(RobolectricTestRunner.class)
public class TreeTest extends TreeSitterTest {

  @Test
  public void testTreeCopy() throws UnsupportedEncodingException {
    try (final var parser = TSParser.create()) {
      parser.setLanguage(TSLanguageJava.getInstance());
      try (final var tree = parser.parseString("class Main { void main() {} }")) {
        assertThat(tree.getLanguage().getNativeObject()).isEqualTo(
          TSLanguageJava.getInstance().getNativeObject());
        assertThat(tree.copy().getRootNode().getNodeString()).isEqualTo(
          tree.getRootNode().getNodeString());
      }
    }
  }

  @Test
  public void testRootNodeWithOffset() {
    try (final var parser = TSParser.create()) {
      parser.setLanguage(TSLanguageJava.getInstance());
      try (final var tree = parser.parseString("class Main { void main() {} }")) {
        assertThat(tree.getLanguage().getNativeObject()).isEqualTo(
          TSLanguageJava.getInstance().getNativeObject());

        final var offset = 4;
        final var rootNode = tree.getRootNode();
        final var rootNodeWithOffset = tree.getRootNodeWithOffset(offset,
          TSPoint.create(0, offset));

        assertThat(rootNodeWithOffset.getStartByte()).isEqualTo(rootNode.getStartByte() + offset);
        assertThat(rootNodeWithOffset.getEndByte()).isEqualTo(rootNode.getEndByte() + offset);
        assertThat(rootNodeWithOffset.getChild(0).getStartByte()).isEqualTo(
          rootNode.getChild(0).getStartByte() + offset);
        assertThat(rootNodeWithOffset.getChild(0).getEndByte()).isEqualTo(
          rootNode.getChild(0).getEndByte() + offset);
        assertThat(rootNodeWithOffset.getChild(0).getChild(2).getStartByte()).isEqualTo(
          rootNode.getChild(0).getChild(2).getStartByte() + offset);
        assertThat(rootNodeWithOffset.getChild(0).getChild(2).getEndByte()).isEqualTo(
          rootNode.getChild(0).getChild(2).getEndByte() + offset);
      }
    }
  }
}
