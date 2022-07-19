package com.itsaky.androidide.treesitter;

/**
 * TSPoint
 */
public class TSPoint {

    public int row, column;

    public TSPoint(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public String toString() {
        return "TSPoint(Row: " + this.row + ", Column: " + this.column + ")";
    }
}
