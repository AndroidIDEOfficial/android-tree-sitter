package com.itsaky.androidide.treesitter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

public class TSInputEditTest extends TestBase {

  @Test
  public void testInputEdit() throws UnsupportedEncodingException {
    try (TSParser parser = new TSParser()) {
      parser.setLanguage(TSLanguages.java());

      // Parse code
      TSTree tree =
          parser.parseString(
              "class Hello {\npublic void hello() {}}", TSInputEncoding.TSInputEncodingUTF16);

      // Empty edit
      TSNode editedNode = tree.getRootNode().getChild(0).getChild(2).getChild(1).getChild(4);
      TSPoint newEndPoint =
          new TSPoint(editedNode.getEndPoint().row, editedNode.getEndPoint().column);
      TSInputEdit edit =
          new TSInputEdit(
              editedNode.getStartByte(),
              editedNode.getEndByte(),
              editedNode.getEndByte(),
              editedNode.getStartPoint(),
              editedNode.getEndPoint(),
              newEndPoint);

      String oldSExp = tree.getRootNode().getNodeString();

      tree.edit(edit);

      tree =
          parser.parseString(
              tree,
              "class Hello {\npublic void hello() {\nint x = 3; }}",
              TSInputEncoding.TSInputEncodingUTF16);

      String newSExp = tree.getRootNode().getNodeString();
      assertEquals(oldSExp, newSExp);
    }
  }
}
