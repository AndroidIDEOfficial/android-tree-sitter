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

/**
 * @author Akash Yadav
 */
public class TreeTest extends TreeSitterTest {

  @Test
  public void testTreeCopy() throws UnsupportedEncodingException {
    try (final var parser = new TSParser()) {
      parser.setLanguage(TSLanguageJava.newInstance());
      try (final var tree =
          parser.parseString(
              "class Main { void main() {} }", TSInputEncoding.TSInputEncodingUTF8)) {
        assertThat(tree.getLanguage().pointer).isEqualTo(TSLanguageJava.newInstance().pointer);
        assertThat(tree.copy().getRootNode().getNodeString())
            .isEqualTo(tree.getRootNode().getNodeString());
      }
    }
  }
}