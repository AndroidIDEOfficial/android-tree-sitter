package com.itsaky.androidide.treesitter;

import static com.google.common.truth.Truth.assertThat;

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
    final var lang = TSLanguages.java();
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
