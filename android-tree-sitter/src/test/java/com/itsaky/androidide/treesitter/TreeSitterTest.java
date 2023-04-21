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

import org.junit.Test;

public class TreeSitterTest {
  static {
    String hostDir = System.getProperty("user.dir") + "/../build/host";
    System.load(hostDir + "/libandroid-tree-sitter.so");
    System.load(hostDir + "/libtree-sitter-java.so");
    System.load(hostDir + "/libtree-sitter-json.so");
    System.load(hostDir + "/libtree-sitter-kotlin.so");
    System.load(hostDir + "/libtree-sitter-log.so");
    System.load(hostDir + "/libtree-sitter-xml.so");
    System.load(hostDir + "/libtree-sitter-python.so");
  }

  @Test
  public void test() {
    assertThat(TreeSitter.getLanguageVersion()).isEqualTo(14);
    assertThat(TreeSitter.getMinimumCompatibleLanguageVersion()).isEqualTo(13);
  }
}