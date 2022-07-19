package com.itsaky.androidide.treesitter;

import java.io.UnsupportedEncodingException;

public class TSParser implements AutoCloseable {
  private long pointer;

  TSParser(long pointer) {
    this.pointer = pointer;
  }

  public TSParser() {
    this(TreeSitter.parserNew());
  }

  public void setLanguage(long language) {
    TreeSitter.parserSetLanguage(pointer, language);
  }

  public TSTree parseString(String source) throws UnsupportedEncodingException {
    return parseString(source, TSInputEncoding.TSInputEncodingUTF8);
  }

  public TSTree parseString(String source, TSInputEncoding encoding) throws UnsupportedEncodingException {
    byte[] bytes = source.getBytes(encoding.getCharset());
    return new TSTree(TreeSitter.parserParseBytes(pointer, bytes, bytes.length, encoding.getFlag()));
  }

  public TSTree parseString(TSTree oldTree, String source) throws UnsupportedEncodingException {
    return parseString(oldTree, source, TSInputEncoding.TSInputEncodingUTF8);
  }

  public TSTree parseString(TSTree oldTree, String source, TSInputEncoding encoding) throws UnsupportedEncodingException {
    byte[] bytes = source.getBytes(encoding.getCharset());
    return new TSTree(TreeSitter.parserIncrementalParseBytes(pointer, oldTree.getPointer(), bytes, bytes.length, encoding.getFlag()));
  }


  @Override
  public void close() {
    TreeSitter.parserDelete(pointer);
  }
}
