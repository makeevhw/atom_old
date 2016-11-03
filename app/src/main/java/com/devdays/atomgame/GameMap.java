package com.devdays.atomgame;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Random;


public class GameMap {
    final int DIRECTION_UP = 0, DIRECTION_DOWN = 1, DIRECTION_RIGHT = 2, DIRECTION_LEFT = 3;
    private final int ATOM_CODE = 1, FREE_SPACE = 0, MOVE_IN = 2, MOVE_OUT = 3, DOUBLE_LASERS = 4;
    ArrayList<int[]> mSolutionAtomArray;
    int mNumberOfAtoms;
    ArrayList<int[]> mMoveCollector; // arrlist of all moves like {x1, y1, x2, y2, color}
    int[][] mCurrentLevelMap;
    int mNumberofShoots;
    ArrayList<Hintsegments> hs = new ArrayList<>();
    private Random mRnd;
    private float mHColor = 131071.65535f; // oh

    public GameMap(int n, int m) {
        mRnd = new Random();
        mMoveCollector = new ArrayList<int[]>();
        mSolutionAtomArray = new ArrayList<>();

        mCurrentLevelMap = addBoardersToLevel(generateMap(n - 2, m - 2)); // 2 fo borders
        initSolutionArrayAndAtomNumber();
        mNumberOfAtoms = getNumberOfAtoms();


    }


    private void initSolutionArrayAndAtomNumber() {
        mNumberOfAtoms = 0;
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                if (mCurrentLevelMap[y][x] == ATOM_CODE) {
                    mSolutionAtomArray.add(new int[]{x, y}); // {x, y}//bad code, repair
                    mNumberOfAtoms++;
                }
            }
        }
    }

    //Sobir's method
    int[][] generateMap(int n, int m) {
        int level0[][] = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int temp = mRnd.nextInt(9);
                if (temp == 0) {
                    level0[i][j] = ATOM_CODE;
                }
            }
        }

        //todo change for "hard" or "soft" level mode
        mNumberofShoots = 100 * (n + m) / 5; // hardcode, hackaton INFINITY_VAL implementation, big enough
        return level0;
    }

    private int[][] addBoardersToLevel(int[][] level) {
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

    public void addMoveLine(int x1, int y1, int x2, int y2, int color) {
        int[] arr = {x1, y1, x2, y2, color};
        mMoveCollector.add(arr);
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
        return (cellX == 0 && direction == DIRECTION_LEFT) ||
                (cellX == getWidth() - 1 && direction == DIRECTION_RIGHT) ||
                (cellY == 0 && direction == DIRECTION_UP) ||
                (cellY == getHeight() - 1 && direction == DIRECTION_DOWN);
    }

    protected boolean isMoveAble(int mTouchX, int mTouchY, int pixForBlockX, int pixForBlockY) {
        int cellY, cellX;
        cellX = Math.min(mTouchX / (pixForBlockX /* + 1 */), getWidth() - 1); // бесподобный костыль
        cellY = Math.min(mTouchY / (pixForBlockY /* + 1 */), getHeight() - 1);

        return (mCurrentLevelMap[cellY][cellX] == FREE_SPACE ||
                mCurrentLevelMap[cellY][cellX] == MOVE_OUT) &&
                mNumberofShoots > 0;
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
        int currentColor = nextColor(); //Color.argb(255, mRnd.nextInt(256), mRnd.nextInt(256), mRnd.nextInt(256));
        // проставить коорды в массиве
        // добавить в аррэйлист для отрисовки начала луча

        int cellY;
        int cellX;
        int pixelOffsetX = pixForBlockX / 4;
        int pixelOffsetY = pixForBlockY / 4;

        cellX = Math.min(touchX / (pixForBlockX /* + 1 */), getWidth() - 1); // бесподобный костыль
        cellY = Math.min(touchY / (pixForBlockY /* + 1 */), getHeight() - 1);


        //move_in не может быть, проверка перед вызовом метода
        if (mCurrentLevelMap[cellY][cellX] == FREE_SPACE) // не завпускали луч
            mCurrentLevelMap[cellY][cellX] = MOVE_IN; // показываем, что луч пущен
        else if (mCurrentLevelMap[cellY][cellX] == MOVE_OUT) { // если в этой ячейке выход луча, то
            mCurrentLevelMap[cellY][cellX] = DOUBLE_LASERS;
        }

        //accurate drawing lines
        switch (currentDirection) {
            case DIRECTION_LEFT:
                pixelOffsetY = pixForBlockY / 4;
                break;
            case DIRECTION_RIGHT:
                pixelOffsetY = 3 * pixForBlockY / 4;
                break;
            case DIRECTION_UP:
                pixelOffsetX = 3 * pixForBlockX / 4;
                break;
            case DIRECTION_DOWN:
                pixelOffsetX = pixForBlockX / 4;
                break;
            default:
                break;
        }


        AddLineToDraws(cellX, cellY,
                pixelOffsetX,
                pixelOffsetY,
                laserDrection,
                currentColor,
                pixForBlockX,
                pixForBlockY
        );


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
                        } else {
                            mCurrentLevelMap[cellY][cellX] = MOVE_OUT;
                        }
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
                        } else {
                            mCurrentLevelMap[cellY][cellX] = MOVE_OUT; // добегались
                        }
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
                        } else {
                            mCurrentLevelMap[cellY][cellX] = MOVE_OUT;
                        }
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
                        } else {
                            mCurrentLevelMap[cellY][cellX] = MOVE_OUT;
                        }
                    }
                    break;
                default:
                    break;
            }
        } while (!isExit(cellX, cellY, currentDirection));


        //exit
        //accurate drawing lines
        switch (currentDirection) {
            case DIRECTION_LEFT:
                pixelOffsetY = pixForBlockY / 4;
                break;
            case DIRECTION_RIGHT:
                pixelOffsetY = 3 * pixForBlockY / 4;
                break;
            case DIRECTION_UP:
                pixelOffsetX = 3 * pixForBlockX / 4;
                break;
            case DIRECTION_DOWN:
                pixelOffsetX = pixForBlockX / 4;
                break;
            default:
                break;
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

    private int nextColor() {
        //use golden ratio
        float val = 32767.16383f;

        mHColor *= val;
        mHColor %= 359;

        int resColor = Color.HSVToColor(
                new float[]{mHColor,
                        (0.7f + (mRnd.nextFloat() % 0.3f)),
                        (0.85f + (mRnd.nextFloat() % 0.15f))}
        );
        return resColor;
    }

    private void AddLineToDraws(int cellX, int cellY, int pixelOffsetX, int pixelOffsetY,
                                int direction, int currentColor, int pixForBlockX, int pixForBlockY) {
        switch (direction) { // вход лазера
            case DIRECTION_RIGHT:
                addMoveLine( // x-s left to right
                        cellX * pixForBlockX,
                        cellY * pixForBlockY + pixelOffsetY,
                        cellX * pixForBlockX + pixForBlockX,
                        cellY * pixForBlockY + pixelOffsetY,
                        currentColor);
                break;
            case DIRECTION_LEFT:
                addMoveLine(
                        cellX * pixForBlockX + pixForBlockX,
                        cellY * pixForBlockY + pixelOffsetY,
                        cellX * pixForBlockX,
                        cellY * pixForBlockY + pixelOffsetY,
                        currentColor);
                break;
            case DIRECTION_UP:
                addMoveLine(
                        cellX * pixForBlockX + pixelOffsetX,
                        cellY * pixForBlockY + pixForBlockY,
                        cellX * pixForBlockX + pixelOffsetX,
                        cellY * pixForBlockY,
                        currentColor);
                break;
            case DIRECTION_DOWN:
                addMoveLine(
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
