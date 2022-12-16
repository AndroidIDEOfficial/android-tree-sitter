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

import com.itsaky.androidide.treesitter.java.TSLanguageJava;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Akash Yadav
 */
@RunWith(JUnit4.class)
public class TSLanguageTest extends TreeSitterTest {

  @Test
  public void testFunctionality() {
    final var lang = TSLanguageJava.newInstance();
    lang.getLanguageVersion();
    lang.getFieldCount();
    lang.getSymbolCount();
    lang.getFieldNameForId(1);
    lang.getFieldIdForName("operator");
    lang.getSymbolName(1);
    lang.getSymbolType(1);
    lang.getSymbolForTypeString("identifier", true);
    lang.getSymbolForTypeString("block", false);
  }
}