package com.itsaky.androidide.treesitter;

public class TSTree implements AutoCloseable {

    private long pointer;


    TSTree(long pointer) {
        this.pointer = pointer;
    }

    @Override
    public void close() {
        TreeSitter.treeDelete(pointer);
    }

    public TSNode getRootNode() {
        return TreeSitter.treeRootNode(pointer);
    }

    public void edit(TSInputEdit edit) {
        TreeSitter.treeEdit(
            pointer,
            edit
        );
    }

    public long getPointer() {
        return pointer;
    }

}
