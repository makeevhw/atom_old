package com.devdays.atomgame;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Random;


public class GameMap {

    public final int DIRECTION_UP = 0, DIRECTION_DOWN = 1, DIRECTION_RIGHT = 2, DIRECTION_LEFT = 3;
    protected final int ATOM_CODE = 1, FREE_SPACE = 0, MOVE_IN = 2, MOVE_OUT = 3, DOUBLE_LASERS = 4;
    protected int mWidth, mHeight, mCurrentLevel;

    protected ArrayList<int[]> mMoveCollector; // arrlist of all moves like {x1, y1, x2, y2, color}
    protected int[][] mCurrentLevelMap;
    protected int mNumberofShoots;
    private Random mRnd;
    public ArrayList<int[]> mSolutionAtomArray;

    public GameMap(int n, int m) {
        mRnd = new Random();
        mMoveCollector = new ArrayList<int[]>();
        mSolutionAtomArray = new ArrayList<>();
        //mHeight = mMapCollector.get(mCurrentLevel).length;
        //mWidth = mMapCollector.get(mCurrentLevel)[0].length;
        //mHeight = m - 2; // redo later for map generating
        //mWidth = n - 2; // 2 for boarders
        mCurrentLevelMap = AddBoardersToLevel(generateMap(n - 2, m - 2));

    }

    //Sobir's method
    int[][] generateMap(int n, int m) {
        // todo implement generator, now preset
        int level0[][] = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int temp = mRnd.nextInt(9);
                if (temp == 0) {
                    level0[i][j] = ATOM_CODE;
                    mSolutionAtomArray.add(new int[]{j + 1, i + 1}); // {x, y}//bad code, repair
                }
            }
        }

        //todo change for "hard" or "soft" level mode
        mNumberofShoots = 100 * (n + m) / 5; // hardcode
        return level0;
    }


    private int[][] AddBoardersToLevel(int[][] level) {
        int[][] levelWithBoarders = new int[level.length + 2][level[0].length + 2];

        // содержимое и боковые
        for (int i = 1; i < levelWithBoarders.length - 1; i++) {
            for (int j = 1; j < levelWithBoarders[0].length - 1; j++) { //содержимое
                levelWithBoarders[i][j] = level[i - 1][j - 1];
            }
        }
        return levelWithBoarders;
    }

    public int getHeight() {
        return mCurrentLevelMap.length;
    }

    public int getWidth() {
        return mCurrentLevelMap[0].length;
    }

    public void AddMoveLine(int x1, int y1, int x2, int y2, int color) {
        int[] arr = {x1, y1, x2, y2, color};
        mMoveCollector.add(arr);
    }

    //todo why we need this? or not need
    private int[][] cleanLevel(int[][] level) {
        for (int i = 0; i < level.length; i++) {
            level[i][0] = 0;
            level[i][level[0].length - 1] = 0;
        }
        for (int j = 0; j < level[0].length; j++) {
            level[0][j] = 0;
            level[level.length - 1][j] = 0;
        }
        return level;
    }


    public int getNumberOfAtoms() {
        int number = 0;
        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth(); j++) { //содержимое
                if (mCurrentLevelMap[i][j] == ATOM_CODE)
                    number++;
            }
        }
        return number;
    }

    /**
     * Проверяет, что луч вышел из путешествия по полю
     *
     * @param cellX
     * @param cellY
     * @param direction
     * @return
     */
    private boolean isExit(int cellX, int cellY, int direction) {
        if ((cellX == 0 && direction == DIRECTION_LEFT) ||
                (cellX == getWidth() - 1 && direction == DIRECTION_RIGHT) ||
                (cellY == 0 && direction == DIRECTION_UP) ||
                (cellY == getHeight() - 1 && direction == DIRECTION_DOWN))
            return true;
        else
            return false;
    }

    /**
     * Трассирует луч по атомам (все правила игры тут), добавляет пару лучей в arraylis
     *
     * @param touchX
     * @param touchY
     * @param laserDrection
     * @param pixForBlockX
     * @param pixForBlockY
     */
    public void MakeMove(int touchX, int touchY, int laserDrection, int pixForBlockX, int pixForBlockY) {

        int currentDirection = laserDrection;
        int currentColor = Color.argb(255, mRnd.nextInt(256), mRnd.nextInt(256), mRnd.nextInt(256));
        // проставить коорды в массиве
        // добавить в аррэйлист для отрисовки начала луча

        int cellY = 0;
        int cellX = 0;
        int pixelOffsetX = pixForBlockX / 4;
        int pixelOffsetY = pixForBlockY / 4;
        boolean isAtomCollided = false, doubleLasers = false;

        cellX = Math.min(touchX / (pixForBlockX /* + 1 */), getWidth() - 1); // бесподобный костыль
        cellY = Math.min(touchY / (pixForBlockY /* + 1 */), getHeight() - 1);


        //move_in не может быть, проверка перед вызовом метода
        if (mCurrentLevelMap[cellY][cellX] == FREE_SPACE) // не завпускали луч
            mCurrentLevelMap[cellY][cellX] = MOVE_IN; // показываем, что луч пущен
        else if (mCurrentLevelMap[cellY][cellX] == MOVE_OUT) { // если в этой ячейке выход луча, то
            pixelOffsetX = 3 * pixForBlockX / 4;
            pixelOffsetY = 3 * pixForBlockY / 4;
            mCurrentLevelMap[cellY][cellX] = DOUBLE_LASERS;
        }

        AddLineToDraws(cellX, cellY, pixelOffsetX, pixelOffsetY, laserDrection, currentColor, pixForBlockX, pixForBlockY);


        do { // трассируем путь
            switch (currentDirection) {
                case DIRECTION_RIGHT:
                    if (mCurrentLevelMap[cellY][cellX + 1] == ATOM_CODE) {//if next в атом
                        currentDirection = DIRECTION_DOWN; // rotate
                        cellX++;
                    } else {
                        cellX++; // go to next cell and check endlessness
                        if (mCurrentLevelMap[cellY][cellX] == MOVE_IN) {
                            mCurrentLevelMap[cellY][cellX] = DOUBLE_LASERS;
                            doubleLasers = true;
                        } else
                            mCurrentLevelMap[cellY][cellX] = MOVE_OUT;
                    }
                    break;

                case DIRECTION_LEFT:
                    if (mCurrentLevelMap[cellY][cellX - 1] == ATOM_CODE) {//если попадаем в атом
                        currentDirection = DIRECTION_UP;
                        cellX--;
                    } else {
                        cellX--;
                        if (mCurrentLevelMap[cellY][cellX] == MOVE_IN) {
                            mCurrentLevelMap[cellY][cellX] = DOUBLE_LASERS; // делаем шаг
                            doubleLasers = true;
                        } else
                            mCurrentLevelMap[cellY][cellX] = MOVE_OUT;
                    }
                    break;
                case DIRECTION_UP:
                    if (mCurrentLevelMap[cellY - 1][cellX] == ATOM_CODE) { //если попадаем в атом
                        currentDirection = DIRECTION_RIGHT;
                        cellY--;
                    } else {
                        if (mCurrentLevelMap[cellY][cellX] == MOVE_OUT)
                            mCurrentLevelMap[cellY][cellX] = FREE_SPACE; // убираем за собой
                        cellY--;
                        if (mCurrentLevelMap[cellY][cellX] == MOVE_IN) {
                            mCurrentLevelMap[cellY][cellX] = DOUBLE_LASERS;
                            doubleLasers = true;
                        } else
                            mCurrentLevelMap[cellY][cellX] = MOVE_OUT;
                    }
                    break;
                case DIRECTION_DOWN:
                    if (mCurrentLevelMap[cellY + 1][cellX] == ATOM_CODE) { //если попадаем в атом
                        currentDirection = DIRECTION_LEFT;
                        cellY++;
                    } else {
                        if (mCurrentLevelMap[cellY][cellX] == MOVE_OUT)
                            mCurrentLevelMap[cellY][cellX] = FREE_SPACE; // убираем за собой
                        cellY++;
                        if (mCurrentLevelMap[cellY][cellX] == MOVE_IN) {
                            mCurrentLevelMap[cellY][cellX] = DOUBLE_LASERS;
                            doubleLasers = true;
                        } else
                            mCurrentLevelMap[cellY][cellX] = MOVE_OUT;
                    }
                    break;

                default:
                    break;
            }

        } while (!isExit(cellX, cellY, currentDirection));


        if (doubleLasers) {
            pixelOffsetX = 3 * pixForBlockX / 4;
            pixelOffsetY = 3 * pixForBlockY / 4;
        } else {
            pixelOffsetX = pixForBlockX / 4;
            pixelOffsetY = pixForBlockY / 4;
        }

        // добавить в arraylist для отрисовки КОНЕЦ луча
        AddLineToDraws(
                cellX,
                cellY,
                pixelOffsetX,
                pixelOffsetY,
                currentDirection,
                currentColor,
                pixForBlockX,
                pixForBlockY
        );

        mNumberofShoots--;
    } //end MakeMove method

    private void AddLineToDraws(int cellX, int cellY, int pixelOffsetX, int pixelOffsetY,
                                int direction, int currentColor, int pixForBlockX, int pixForBlockY) {
        switch (direction) { // вход лазера
            case DIRECTION_RIGHT:
                AddMoveLine( // x-s left to right
                        cellX * pixForBlockX,
                        cellY * pixForBlockY + pixelOffsetY,
                        cellX * pixForBlockX + pixForBlockX,
                        cellY * pixForBlockY + pixelOffsetY,
                        currentColor);
                break;
            case DIRECTION_LEFT:
                AddMoveLine(
                        cellX * pixForBlockX + pixForBlockX,
                        cellY * pixForBlockY + pixelOffsetY,
                        cellX * pixForBlockX,
                        cellY * pixForBlockY + pixelOffsetY,
                        currentColor);
                break;
            case DIRECTION_UP:
                AddMoveLine(
                        cellX * pixForBlockX + pixelOffsetX,
                        cellY * pixForBlockY + pixForBlockY,
                        cellX * pixForBlockX + pixelOffsetX,
                        cellY * pixForBlockY,
                        currentColor);
                break;
            case DIRECTION_DOWN:
                AddMoveLine(
                        cellX * pixForBlockX + pixelOffsetX,
                        cellY * pixForBlockY,
                        cellX * pixForBlockX + pixelOffsetX,
                        cellY * pixForBlockY + pixForBlockY,
                        currentColor);
                break;
            default:
                break;
        }
    }

}
