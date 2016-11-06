package com.devdays.atomgame;

//container
public class Line {
    int x1, x2, y1, y2;
    boolean isOutput;
    int color;
    Line pair; /// pair for lazer-line

    public Line(int x1, int y1, int x2, int y2, int color, Line pair) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.color = color;
        this.pair = pair;
        this.isOutput = false;
    }

    public Line(int x1, int y1, int x2, int y2, int color) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.color = color;
        this.pair = null;
        this.isOutput = false;
    }

    public void setPair(Line pair) {
        this.pair = pair;
    }
}
