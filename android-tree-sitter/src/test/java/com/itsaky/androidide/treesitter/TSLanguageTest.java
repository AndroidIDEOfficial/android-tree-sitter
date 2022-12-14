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
