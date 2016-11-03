package com.devdays.atomgame;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by Yorov on 03.11.2016.
 */


public class Level {

    private static final int EASY = 0;
    private static final int EPIC = 1;
    private static final int LEGENDARY = 2;
    private Atom[] atoms;
    private int atoms_count;
    private int[][] matrix;
    private int laser_count;

    public Level(int n, int level) {
        laser_count = 0;
        atoms = new Atom[n * n];
        atoms_count = 0;

        matrix = generateMap(n, level);
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public int getLaser_count() {
        return laser_count;
    }

    public int get_atoms_count() {
        return atoms_count;
    }

    private int[][] generateMap(int n, int level) {

        if (level == EASY) {
            return easy_map(n);
        }

        if (level == EPIC) {
            return epic_map(n);
        }

        if (level == LEGENDARY) {
            return legendary_map(n);
        }

        return null;
    }

    private int[][] easy_map(int n) {

        int easy[][] = new int[n][n];

        for (int i = 0; i < n; ++i)
            Arrays.fill(easy[i], 0);

        int i = randInt(0, n, true);
        int j = randInt(0, n, true);
        easy[i][j] = 1;
        atoms[atoms_count] = new Atom(i, j, false);
        ++atoms_count;

        for (int k = 0; k < atoms_count; ++k) {
            add_atom(0, atoms[k].i, true, 0, atoms[k].j, true, EASY);
            add_atom(0, atoms[k].i, true, atoms[k].j, n, false, EASY);
            add_atom(atoms[k].i, n, false, 0, atoms[k].j, true, EASY);
            add_atom(atoms[k].i, n, false, atoms[k].j, n, false, EASY);
        }

        for (int k = 0; k < atoms_count; ++k) {
            easy[atoms[k].i][atoms[k].j] = 1;
        }

        laser_count = n + Math.round(n / 2);

        return easy;

    }

    private int[][] epic_map(int n) {

        int epic[][] = new int[n][n];

        for (int i = 0; i < n; ++i)
            Arrays.fill(epic[i], 0);

        int i = randInt(0, n, true);
        int j = randInt(0, n, true);
        epic[i][j] = 1;
        atoms[atoms_count] = new Atom(i, j, false);
        ++atoms_count;

        for (int k = 0; k < atoms_count; ++k) {
            add_atom(0, atoms[k].i, true, 0, atoms[k].j, true, EPIC);
            add_atom(0, atoms[k].i, true, atoms[k].j, n, true, EPIC);
            add_atom(atoms[k].i, n, true, 0, atoms[k].j, true, EPIC);
            add_atom(atoms[k].i, n, true, atoms[k].j, n, true, EPIC);
        }

        for (int k = 0; k < atoms_count; ++k) {
            epic[atoms[k].i][atoms[k].j] = 1;
        }

        laser_count = atoms_count + Math.min(atoms_count, 11);
        return epic;

    }

    private int[][] legendary_map(int n) {

        int leg_map[][] = new int[n][n];

        for (int i = 0; i < n; ++i)
            Arrays.fill(leg_map[i], 0);

        int i = randInt(0, n, true);
        int j = randInt(0, n, true);
        leg_map[i][j] = 1;
        atoms[atoms_count] = new Atom(i, j, false);
        ++atoms_count;

        for (int k = 0; k < atoms_count; ++k) {
            add_atom(0, atoms[k].i, true, 0, atoms[k].j, true, LEGENDARY + 1);
            add_atom(0, atoms[k].i, true, atoms[k].j, n, true, LEGENDARY + 1);
            add_atom(atoms[k].i, n, true, 0, atoms[k].j, true, LEGENDARY + 1);
            add_atom(atoms[k].i, n, true, atoms[k].j, n, true, LEGENDARY + 1);
        }

        for (int k = 0; k < atoms_count; ++k) {
            leg_map[atoms[k].i][atoms[k].j] = 1;
        }

        laser_count = 4 * n;
        return leg_map;

    }

    private void add_atom(int ileft, int iright, boolean ilborder, int jleft, int jright, boolean jlborder, int level) {

        if (ilborder) {
            if (!(iright > ileft)) return;
        }
        if (!ilborder) {
            if (!(iright > ileft + 1)) return;
        }

        if (jlborder) {
            if (!(jright > jleft)) return;
        }
        if (!jlborder) {
            if (!(jright > jleft + 1)) return;
        }

        int i, j;
        int k = 0;

        while (k < (iright - ileft) * (jright - jleft)) {
            i = randInt(ileft, iright, ilborder);
            j = randInt(jleft, jright, jlborder);

            int count = 0;
            int type = 1;
            for (int m = 0; m < atoms_count; ++m) {
                if (atoms[m].i == i || atoms[m].j == j) {
                    if (!atoms[m].haspair) {
                        if (count == level) {
                            type = 0;
                            break;
                        } else {
                            ++count;
                            type = 2;
                        }
                    } else {
                        type = 0;
                        break;
                    }
                }
            }

            if (type == 0) {
                ++k;
                continue;
            }

            if (type == 1) {
                atoms[atoms_count] = new Atom(i, j, false);
                ++atoms_count;
                return;
            }

            if (type == 2) {

                for (int m = 0; m < atoms_count; ++m) {
                    if (atoms[m].i == i || atoms[m].j == j) {
                        atoms[m].haspair = true;
                    }
                }

                atoms[atoms_count] = new Atom(i, j, true);
                ++atoms_count;
                return;
            }

        }

    }

    private int randInt(int min, int max, boolean include_left_border) {
        Random rand = new Random();

        if (include_left_border) {
            return rand.nextInt(max - min) + min;
        }

        return rand.nextInt(max - min - 1) + min + 1;
    }

    class Atom {
        int i;
        int j;
        boolean haspair;

        Atom(int i, int j, boolean haspair) {
            this.i = i;
            this.j = j;
            this.haspair = haspair;
        }

    }

}

