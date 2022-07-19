package com.itsaky.androidide.treesitter;

/**
 * InputEdit
 */
public class TSInputEdit {

    public int startByte;
    int oldEndByte;
    int newEndByte;
    public TSPoint start_point;
    TSPoint old_end_point;
    TSPoint new_end_point;

    public TSInputEdit(
        int startByte,
        int oldEndByte,
        int newEndByte,
        TSPoint start_point,
        TSPoint old_end_point,
        TSPoint new_end_point
    ) {
        this.startByte = startByte;
        this.oldEndByte = oldEndByte;
        this.newEndByte = newEndByte;
        this.start_point = start_point;
        this.old_end_point = old_end_point;
        this.new_end_point = new_end_point;
    }
}
