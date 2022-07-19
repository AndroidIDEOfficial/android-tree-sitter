package com.itsaky.androidide.treesitter;

public class TreeSitter {

    public static native TSNode nodeChild(TSNode node, int child);

    public static native int nodeChildCount(TSNode node);

    public static native int nodeEndByte(TSNode node);

    public static native int nodeStartByte(TSNode node);

    public static native TSPoint nodeStartPoint(TSNode node);

    public static native TSPoint nodeEndPoint(TSNode node);

    public static native String nodeString(TSNode node);

    public static native String nodeType(TSNode node);

    public static native long parserNew();

    public static native void parserDelete(long parser);

    public static native void parserSetLanguage(long parser, long language);

    public static native long parserParseBytes(
        long parser,
        byte[] source,
        int length
    );

    public static native long parserIncrementalParseBytes(
        long parser,
        long old_tree,
        byte[] source,
        int length
    );

    // TODO refactor this
    public static native void treeEdit(
        long tree,
        TSInputEdit inputEdit
    );

    public static native long treeCursorNew(TSNode node);

    public static native TSTreeCursorNode treeCursorCurrentTreeCursorNode(
        long cursor
    );

    public static native String treeCursorCurrentFieldName(long cursor);

    public static native TSNode treeCursorCurrentNode(long cursor);

    public static native void treeCursorDelete(long cursor);

    public static native boolean treeCursorGotoFirstChild(long cursor);

    public static native boolean treeCursorGotoNextSibling(long cursor);

    public static native boolean treeCursorGotoParent(long cursor);

    public static native void treeDelete(long tree);

    public static native TSNode treeRootNode(long tree);

    // Query
    public static native long tsQueryNew(long language, String source);
}
