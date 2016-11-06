package com.devdays.atomgame;

/**
 * Container for manipulating in other classes.
 * <p>
 * Cells can have the same x and y, but different isOutputLine field
 * in some cases.
 */
class Cell {
    int x, y;
    boolean isOutputLine; //for highlighting, to differ 2 types of lazers
    String representationForHashcode; // need for hashcode, kostil'

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.isOutputLine = false; // by default
        representationForHashcode = String.valueOf(x) + '#' +
                String.valueOf(y) + '*' + String.valueOf(isOutputLine);
    }

    public void setIsOutpuLine(boolean b) {
        isOutputLine = b;
        representationForHashcode = String.valueOf(x) + '#' +
                String.valueOf(y) + '*' + String.valueOf(isOutputLine);
    }

    //for hashmap key
    @Override
    public int hashCode() {
        //return hashCode(x) + hashCode(y) + Object.hashCode(isOutputLine);
        return representationForHashcode.hashCode();
    }

    @Override
    public boolean equals(Object that) {
        return this.representationForHashcode.equals(((Cell) that).representationForHashcode);
    }


}