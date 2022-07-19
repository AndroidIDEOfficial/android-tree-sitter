package com.itsaky.androidide.treesitter;

/**
 * TSQuery
 */
public class TSQuery {
    long pointer;

    public TSQuery(long language, String source) {
        this.pointer = TreeSitter.tsQueryNew(language, source);
    }
}
