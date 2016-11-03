package com.devdays.atomgame;

class Cell {
    int x, y;
    boolean isOutputLine; //for highlighting
    String representationForHashcode;

    public Cell(int x, int y, boolean isOutput) {
        this.x = x;
        this.y = y;
        this.isOutputLine = isOutput;
        representationForHashcode = String.valueOf(x) + '#' + String.valueOf(y) + '*' + String.valueOf(isOutputLine);
    }

    public void setIsOutpuLine(boolean b) {
        isOutputLine = b;
        representationForHashcode = String.valueOf(x) + '#' + String.valueOf(y) + '*' + String.valueOf(isOutputLine);
    }

    //for hashmap
    @Override
    public int hashCode() {
        //return hashCode(x) + hashCode(y) + Object.hashCode(isOutputLine);
        return representationForHashcode.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this.representationForHashcode.equals(((Cell) o).representationForHashcode);
    }


}