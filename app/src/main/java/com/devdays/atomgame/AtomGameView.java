package com.devdays.atomgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class AtomGameView extends SurfaceView implements SurfaceHolder.Callback {

    protected final int ATOM_CODE = 1; //, FREE_SPACE = 0, MOVE_IN = 2, MOVE_OUT = 3, DOUBLE_LASERS = 4;
    private final float scale = getContext().getResources().getDisplayMetrics().density;
    protected int pixForBlockX, pixForBlockY;
    protected TextView textViewShoots, textViewAtoms, textViewRequest, textViewHeader;
    protected ArrayList<int[]> mChosenAtomsArray; // {cellX, cellY }
    private int mCurrentLevelNumber = 0;
    private GameMap mGameMap;
    private Paint mPaint;
    private float[] mLineHorizontalData, mLineVerticalData; //todo redo with line class
    private boolean mIsMoved = false;
    private Bitmap bitmapAtomBluePic = null;
    private int atomHaveChosed = 0;
    private boolean mLosed = false;
    private float cellPixelSizePLEASE_DONT_USE_ME_IT_IS_GOVNOKOD = 70 * getContext().getResources().getDisplayMetrics().density; //150;
    private int mFirstTouchX, mFirstTouchY;
    private boolean initialized = false;
    private float mBorderLeftX, mBorderRightX, mBorderTopY, mBorderBottomY;
    private boolean mIsHighlighted = false;


    public AtomGameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        getHolder().addCallback(this); // for what?


        int m = (int) (getContext().getResources().getDisplayMetrics().widthPixels / cellPixelSizePLEASE_DONT_USE_ME_IT_IS_GOVNOKOD);


        mGameMap = new GameMap(m, m); // todo redo for n, m, or not, squares are cool
        mPaint = new Paint();


        mLineHorizontalData = new float[(mGameMap.getHeight() + 1) * 4];
        // количество линий о горизонтали на количество координат для каждой линии

        mLineVerticalData = new float[(mGameMap.getWidth() + 1) * 4]; // 4 is max lines for each two cells


        mChosenAtomsArray = new ArrayList<int[]>();

        //mLinesCheckedData = new float[mGameMap.mGameMap.mNumberOfAtoms * 8]; // todo WHY????


    }


    //////many draw methods
    void drawChoses(Canvas canvas) {
        // рисуем кресты/atoms

        /*
        mPaint.setStrokeWidth(9.0f);
        mPaint.setColor(0xff669900);


        //todo redo for drawlines, faster
        for (int i = 0; i < mChosenAtomsArray.size(); i++) {
            canvas.drawLine(
                    mChosenAtomsArray.get(i)[2],
                    mChosenAtomsArray.get(i)[3],
                    mChosenAtomsArray.get(i)[4],
                    mChosenAtomsArray.get(i)[5],
                    mPaint
                    );
            canvas.drawLine(
                    mChosenAtomsArray.get(i)[6],
                    mChosenAtomsArray.get(i)[7],
                    mChosenAtomsArray.get(i)[8],
                    mChosenAtomsArray.get(i)[9],
                    mPaint
                    );
        }
        */

        int offsetFromBorder = 5; //px

        if (bitmapAtomBluePic == null) {
            bitmapAtomBluePic = BitmapFactory.decodeResource(
                    getResources(),
                    R.drawable.atome_blue
            );

            bitmapAtomBluePic = Bitmap.createScaledBitmap(bitmapAtomBluePic,
                    pixForBlockX * 9 / 10,
                    pixForBlockX * 9 / 10,
                    true);
        }


        for (int i = 0; i < mChosenAtomsArray.size(); i++) {
            drawCellPicture(
                    canvas,
                    bitmapAtomBluePic,
                    mChosenAtomsArray.get(i)[0] * pixForBlockX + pixForBlockX / 20,
                    mChosenAtomsArray.get(i)[1] * pixForBlockY + pixForBlockY / 20
            );
        }
    }

    private void drawGrid(Canvas canvas) {

        //performDraw blocks - many lines
        //get size for cell

        //if (!initialized) {
        initVerticalLines(canvas);
        initHorizontalLines(canvas);
        initialized = true;
        //}

        mPaint.setStrokeWidth(1.0f);
        mPaint.setColor(Color.BLUE);
        canvas.drawLines(mLineHorizontalData, mPaint); // рисуем сетку по горизонтали
        //todo redo for cell blocks

        // рисуем сетку по вертикали

        mPaint.setStrokeWidth(1.0f);
        mPaint.setColor(Color.BLUE);
        canvas.drawLines(mLineVerticalData, mPaint);
    }

    private void initHorizontalLines(Canvas canvas) {
        final int offsetX1 = 0;
        final int offsetY1 = 1;
        final int offsetX2 = 2;
        final int offsetY2 = 3;

        for (int i = 0; i < mLineHorizontalData.length; i += 4) {
            mLineHorizontalData[i + offsetX1] = 0; // x1
            mLineHorizontalData[i + offsetY1] = pixForBlockY * (i / 4); //y1
            mLineHorizontalData[i + offsetX2] = canvas.getWidth(); // x2
            mLineHorizontalData[i + offsetY2] = pixForBlockY * (i / 4); // y2
        }

        mBorderTopY = mLineHorizontalData[5];
        mBorderBottomY = mLineHorizontalData[mLineHorizontalData.length - 7];
    }

    private void initVerticalLines(Canvas canvas) {
        final int offsetX1 = 0;
        final int offsetY1 = 1;
        final int offsetX2 = 2;
        final int offsetY2 = 3;

        for (int i = 0; i < mLineVerticalData.length; i += 4) {
            mLineVerticalData[i + offsetX1] = pixForBlockX * (i / 4); // x1
            mLineVerticalData[i + offsetY1] = 0; //y1
            mLineVerticalData[i + offsetX2] = pixForBlockX * (i / 4); // x2
            mLineVerticalData[i + offsetY2] = canvas.getHeight(); // y2
        }

        mBorderLeftX = mLineVerticalData[4];
        mBorderRightX = mLineVerticalData[mLineVerticalData.length - 8];

    }

    private void drawBorders(Canvas canvas) {
        // рисуем границы
        mPaint.setStrokeWidth(10.0f);
        mPaint.setColor(Color.BLACK);
        canvas.drawLine(
                mLineHorizontalData[4],
                mLineHorizontalData[5] + 3,
                mLineHorizontalData[6],
                mLineHorizontalData[7] + 3,
                mPaint);
        canvas.drawLine(
                mLineHorizontalData[mLineHorizontalData.length - 8],
                mLineHorizontalData[mLineHorizontalData.length - 7] - 3,
                mLineHorizontalData[mLineHorizontalData.length - 6],
                mLineHorizontalData[mLineHorizontalData.length - 5] - 3,
                mPaint);
        canvas.drawLine(
                mLineVerticalData[4] + 3,
                mLineVerticalData[5],
                mLineVerticalData[6] + 3,
                mLineVerticalData[7],
                mPaint);
        canvas.drawLine(
                mLineVerticalData[mLineVerticalData.length - 8] - 3,
                mLineVerticalData[mLineVerticalData.length - 7],
                mLineVerticalData[mLineVerticalData.length - 6] - 3,
                mLineVerticalData[mLineVerticalData.length - 5]
                , mPaint);
    }


    private void drawLazer(Canvas canvas, Line line, float strokeWidth) {
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setColor(line.color);

        int x1 = line.x1;
        int y1 = line.y1;
        int x2 = line.x2;
        int y2 = line.y2;

        int deltaX = x2 - x1;
        int deltaY = y2 - y1;

        int cutFactor = 10;
        if (deltaX > 0) { // left to right
            x2 = x2 - Math.abs(deltaX) / cutFactor;
        } else if (deltaX < 0) { // right to left
            x2 = x2 + Math.abs(deltaX) / cutFactor;
        }

        if (deltaY > 0) { // down
            y2 = y2 - Math.abs(deltaY) / cutFactor;
        } else if (deltaY < 0) { // up
            y2 = y2 + Math.abs(deltaY) / cutFactor;
        }

        canvas.drawLine(
                x1,
                y1,
                x2,
                y2,
                mPaint);

        drawArrow(
                canvas,
                line.x1,
                line.y1,
                line.x2,
                line.y2,
                line.color,
                strokeWidth / 100);
    }

    private void drawLazers(Canvas canvas) {
        //mPaint.setStrokeWidth(15.0f); // рисуем ходы
        if (mGameMap.mLazersLines.size() > 0) {
            for (int i = 0; i < mGameMap.mLazersLines.size(); i++) {
                drawLazer(canvas, mGameMap.mLazersLines.get(i), 15.0f);
            }
        }
    }

    /**
     * @param canvas
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     * @param arrowColor
     * @param frac       frac : 0 < frac < 1 - init size of arrow head
     */
    private void drawArrow(Canvas canvas, float x0, float y0, float x1, float y1, int arrowColor,
                           float frac) {

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(arrowColor);

        float deltaX = x1 - x0;
        float deltaY = y1 - y0;
        //float frac = fr;
        /*The variable
            frac : 0 < frac < 1
                determines the size of the arrow head.*/


        float point_x_1 = x0 + (1 - frac) * deltaX + frac * deltaY;
        float point_y_1 = y0 + (1 - frac) * deltaY - frac * deltaX;

        float point_x_2 = x1;
        float point_y_2 = y1;

        float point_x_3 = x0 + (1 - frac) * deltaX - frac * deltaY;
        float point_y_3 = y0 + (1 - frac) * deltaY + frac * deltaX;

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);

        path.moveTo(point_x_1, point_y_1);
        path.lineTo(point_x_2, point_y_2);
        path.lineTo(point_x_3, point_y_3);
        path.lineTo(point_x_1, point_y_1);
        path.lineTo(point_x_1, point_y_1);
        path.close();

        canvas.drawPath(path, mPaint);
    }

    private void drawSolution(Canvas canvas) {
        // рисуем оригинальные кресты

        //Paint p = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(8.0f);


        //todo redo for drawlines, faster
        for (int i = 0; i < mGameMap.mSolutionAtomArray.size(); i++) {

            float radius = Math.min(pixForBlockX / 2, pixForBlockY / 2);
            float x = pixForBlockX * mGameMap.mSolutionAtomArray.get(i)[0] + pixForBlockX / 2;
            float y = pixForBlockY * mGameMap.mSolutionAtomArray.get(i)[1] + pixForBlockY / 2;


            canvas.drawCircle(
                    x, // x
                    y, // y
                    radius, // radius
                    mPaint);
        }
    }

    void drawCellPicture(Canvas canvas, Bitmap bitmap, float left, float top) {
        canvas.drawBitmap(bitmap, left, top, null);
    }

    private void performDraw() {
        Canvas canvas = getHolder().lockCanvas();
        draw(canvas);
        getHolder().unlockCanvasAndPost(canvas);
    }

    private void drawFatLines(Line line1, Line line2) {
        Canvas canvas = getHolder().lockCanvas();


        draw(canvas);

        drawLazer(canvas, line1, 25.0f);
        drawLazer(canvas, line2, 25.0f);


        getHolder().unlockCanvasAndPost(canvas);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        pixForBlockX = canvas.getWidth() / (mGameMap.getWidth());
        pixForBlockY = canvas.getHeight() / (mGameMap.getHeight());

        canvas.drawColor(Color.WHITE);

        drawGrid(canvas);

        drawLazers(canvas);


        drawChoses(canvas);

        if (mLosed)
            drawSolution(canvas);

        drawBorders(canvas);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        pixForBlockX = getWidth() / (mGameMap.getWidth()); // fake
        pixForBlockY = getHeight() / (mGameMap.getHeight());

        // сейчас всегда масштабируем по ширине, хотим что-то типа квадрата, квадраты тру // WHY SO SERIOUS?
        setLayoutParams(new LinearLayout.LayoutParams(getLayoutParams().width,
                (int) (getLayoutParams().height * ((double) pixForBlockX / pixForBlockY))));


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        performDraw();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mFirstTouchX = (int) event.getX(); // для проверки на движения и усключения ложных тапов
                mFirstTouchY = (int) event.getY();
                mIsMoved = false;
                //todo add highlighting
                tryHighlighting(mFirstTouchX, mFirstTouchY);

                return true;
            case MotionEvent.ACTION_MOVE:
                mIsMoved = true;
                return mIsMoved;
            case MotionEvent.ACTION_UP:
                if (mIsHighlighted) {
                    performDraw();
                    mIsHighlighted = false;
                    return true;
                }
                int mTouchX = (int) event.getX();
                int mTouchY = (int) event.getY();
                if (Math.abs(mFirstTouchX - mTouchX) > 2 * pixForBlockX / 3 ||
                        Math.abs(mFirstTouchY - mTouchY) > 2 * pixForBlockY / 3)
                    return false; // ignore moving
                mIsMoved = false; // remove this filed?
                tryLazerAttack(mTouchX, mTouchY);
        }
        return false;
    }

    private boolean tryHighlighting(int x, int y) {
        int cellX = Math.min(x / (pixForBlockX /* + 1 */), getWidth() - 1); // бесподобный костыль
        int cellY = Math.min(y / (pixForBlockY /* + 1 */), getHeight() - 1);
        Line line = null;
        Cell cl = new Cell(cellX, cellY, false); // false by default

        //get lazer, highlight both
        if (y > mBorderTopY && y < mBorderBottomY) { // LEFT OR RIGHT
            if (x < mBorderLeftX) { // left
                if (y < cellY * pixForBlockY + pixForBlockY / 2) // output
                    cl.setIsOutpuLine(true);
                else  // input
                    cl.setIsOutpuLine(false);
            } else if (x > mBorderRightX) // right
                if (y < cellY * pixForBlockY + pixForBlockY / 2) // input
                    cl.setIsOutpuLine(false);
                else  // output
                    cl.setIsOutpuLine(true);

        } else if (x > mBorderLeftX && x < mBorderRightX) { // Top or bottom
            if (y < mBorderTopY) { // top
                if (x < cellX * pixForBlockX + pixForBlockX / 2) // inp
                    cl.setIsOutpuLine(false);
                else  // outp
                    cl.setIsOutpuLine(true);
            } else if (y > mBorderBottomY) // bottom
                if (x < cellX * pixForBlockX + pixForBlockX / 2) // outp
                    cl.setIsOutpuLine(true);
                else  // inp
                    cl.setIsOutpuLine(false);
        }

        line = mGameMap.mLazersLinesMap.get(cl);

        if (line != null) { // достать пару, отрисовать с 2х шириной линии
            // сохранить линни для, хотя не, перерисуем всё
            drawFatLines(line, line.pair);
            mIsHighlighted = true;
            return true;
        }
        return false;
    }

    private boolean tryLazerAttack(int mTouchX, int mTouchY) {
        if (mTouchX < mLineVerticalData[4] && mTouchY > mLineHorizontalData[5]
                && mTouchY < mLineHorizontalData[mLineHorizontalData.length - 7]) { // LEFT
            if (mGameMap.isMoveAble(mTouchX, mTouchY, pixForBlockX, pixForBlockY)) {
                mGameMap.MakeMove(
                        mTouchX,
                        mTouchY,
                        mGameMap.DIRECTION_RIGHT,
                        pixForBlockX,
                        pixForBlockY);
                numberShootsRefresh();
                performDraw();
                return true;
            } else
                return false;
        } else if (mTouchX > mLineVerticalData[mLineVerticalData.length - 8] &&
                mTouchY > mLineHorizontalData[5] // RIGHT
                && mTouchY < mLineHorizontalData[mLineHorizontalData.length - 7]) {
            if (mGameMap.isMoveAble(mTouchX, mTouchY, pixForBlockX, pixForBlockY)) {
                mGameMap.MakeMove(
                        mTouchX,
                        mTouchY,
                        mGameMap.DIRECTION_LEFT,
                        pixForBlockX,
                        pixForBlockY);
                performDraw();
                return true;
            } else
                return false;
        } else if (mTouchX > mLineVerticalData[4]
                && mTouchX < mLineVerticalData[mLineVerticalData.length - 8] // TOP
                && mTouchY < mLineHorizontalData[5]) {
            if (mGameMap.isMoveAble(mTouchX, mTouchY, pixForBlockX, pixForBlockY)) {
                mGameMap.MakeMove(
                        mTouchX,
                        mTouchY,
                        mGameMap.DIRECTION_DOWN,
                        pixForBlockX,
                        pixForBlockY);
                numberShootsRefresh();
                performDraw();
                return true;
            } else
                return false;
        } else if (
                mTouchX > mLineVerticalData[4]
                        && mTouchX < mLineVerticalData[mLineVerticalData.length - 8] // BOTTOM
                        && mTouchY > mLineHorizontalData[mLineHorizontalData.length - 7]
                ) {
            if (mGameMap.isMoveAble(mTouchX, mTouchY, pixForBlockX, pixForBlockY)) {
                mGameMap.MakeMove(
                        mTouchX,
                        mTouchY,
                        mGameMap.DIRECTION_UP,
                        pixForBlockX,
                        pixForBlockY);
                numberShootsRefresh();
                performDraw();
                return true;
            } else
                return false;
        } else if (mTouchX > mLineVerticalData[4] &&
                mTouchX < mLineVerticalData[mLineVerticalData.length - 8] &&  // FIELD
                mTouchY > mLineHorizontalData[5] &&
                mTouchY < mLineHorizontalData[mLineHorizontalData.length - 7]) {
            markLikeAtom(mTouchX, mTouchY);
            performDraw(); // only if remove
            return true;
        } else
            return false;
    }

    private void markLikeAtom(int touchX, int touchY) { // mark cell chosed, draw atom here
        int cellX = touchX / (pixForBlockX + 1); // начальные номера ячеек
        int cellY = touchY / (pixForBlockY + 1);
        int numberToDechoose = 0;
        boolean isAlreadyMarked = false;
        for (int i = 0; i < mChosenAtomsArray.size(); i++) {
            if ((mChosenAtomsArray.get(i))[0] == cellX &&
                    (mChosenAtomsArray.get(i))[1] == cellY) {
                isAlreadyMarked = true;
                numberToDechoose = i;
            }
        }

        if (!isAlreadyMarked) { // CHOOSE
            if (atomHaveChosed < mGameMap.mNumberOfAtoms) {
                int[] arr = new int[2]; // добавляем номера ячеек в arraylist
                arr[0] = cellX;
                arr[1] = cellY;

                mChosenAtomsArray.add(arr);
                atomHaveChosed++;

                textViewAtoms.setText(getResources().getString(R.string.number_of_atoms) + " " +
                        (mGameMap.mNumberOfAtoms - atomHaveChosed) + " ");
                textViewAtoms.invalidate();
            }
        } else { // DECHOOSE update text
            mChosenAtomsArray.remove(numberToDechoose);
            atomHaveChosed--;
            textViewAtoms.setText(getResources().getString(R.string.number_of_atoms) + " " +
                    (mGameMap.mNumberOfAtoms - atomHaveChosed) + " ");
            textViewAtoms.invalidate();
        }
    }

    private void numberShootsRefresh() {
        if (mGameMap.mNumberofShoots < 1)
            textViewShoots.setTextColor(Color.RED);

        textViewShoots.setText(getResources().getString(R.string.number_of_shoots)
                + " " + mGameMap.mNumberofShoots + " ");
        textViewShoots.invalidate(); // refresh
    }

    //print text, here
    public void findTextView() {
        textViewShoots = (TextView) (((MainActivity) this.getContext()).findViewById(R.id.textView1));
        textViewShoots.setText(getResources().getString(R.string.number_of_shoots) + " " + mGameMap.mNumberofShoots + " ");

        textViewAtoms = (TextView) ((MainActivity) this.getContext()).findViewById(R.id.textView2);
        textViewAtoms.setText(getResources().getString(R.string.number_of_atoms) + " " + mGameMap.mNumberOfAtoms + " ");

        textViewRequest = (TextView) ((MainActivity) this.getContext()).findViewById(R.id.textViewRequest);
        textViewRequest.setText(" ");

        textViewHeader = (TextView) ((MainActivity) this.getContext()).findViewById(R.id.textViewHeader);
        textViewHeader.setText(getResources().getString(R.string.level) + " " + mCurrentLevelNumber);
    }

    public void checkResults() {
        if (atomHaveChosed < mGameMap.mNumberOfAtoms) {
            textViewRequest.setTextColor(Color.RED);
            textViewRequest.setText("Not all atoms have been choosen. " + (mGameMap.mNumberOfAtoms - atomHaveChosed) +
                    " atoms left.");
        } else {
            boolean isCorrect = true;

            int xIndex = 0, yIndex = 1;
            for (int i = 0; i < mChosenAtomsArray.size(); i++) {
                if (
                        mGameMap.mCurrentLevelMap[mChosenAtomsArray.get(i)[yIndex]][mChosenAtomsArray.get(i)[xIndex]] != ATOM_CODE) {
                    isCorrect = false;
                    break;
                }
            }

            if (isCorrect) {
                textViewRequest.setTextColor(Color.GREEN);
                textViewRequest.setText("All atoms collected. EPIC WIN !");
            } else {
                textViewRequest.setTextColor(Color.RED);
                textViewRequest.setText("There are mistakes. EPIC FAIL !");
                mLosed = true;
                performDraw();
            }

        }

    }


}