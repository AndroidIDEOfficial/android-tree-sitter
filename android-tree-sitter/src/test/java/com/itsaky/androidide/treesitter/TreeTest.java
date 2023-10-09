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

import org.junit.Test;

import java.io.UnsupportedEncodingException;
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
        assertThat(tree.getLanguage().pointer).isEqualTo(TSLanguageJava.getInstance().pointer);
        assertThat(tree.copy().getRootNode().getNodeString())
            .isEqualTo(tree.getRootNode().getNodeString());
      }
    }
  }
}
