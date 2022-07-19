package com.itsaky.androidide.treesitter;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

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
    byte[] bytes = source.getBytes(StandardCharsets.UTF_16LE);
    return new TSTree(TreeSitter.parserParseBytes(pointer, bytes, bytes.length));
  }

  public TSTree parseString(TSTree oldTree, String source) throws UnsupportedEncodingException {
    byte[] bytes = source.getBytes(StandardCharsets.UTF_16LE);
    return new TSTree(TreeSitter.parserIncrementalParseBytes(pointer, oldTree.getPointer(), bytes, bytes.length));
  }


  @Override
  public void close() {
    TreeSitter.parserDelete(pointer);
  }
}
