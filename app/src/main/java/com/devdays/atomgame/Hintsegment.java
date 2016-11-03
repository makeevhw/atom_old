package com.devdays.atomgame;


import java.util.HashSet;

public class Hintsegment {
    Cell startCell, endCell;
    HashSet<Cell> nodesOnPath;


    public Hintsegment(int x, int y, int[][] map, int[][] chosenMap) {
        //check all X-directions for incoming laser
    }

    class Cell {
        int x, y;

        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
