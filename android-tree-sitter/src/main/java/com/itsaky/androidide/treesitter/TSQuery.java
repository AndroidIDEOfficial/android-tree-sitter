package com.itsaky.androidide.treesitter;

/**
 * TSQuery
 */
public class TSQuery {
    long pointer;

    public TSQuery(TSLanguage language, String source) {
        this.pointer = Native.newQuery(language.pointer, source);
    }

    private static class Native  {
        public static native long newQuery(long pointer, String source);
    }
}
