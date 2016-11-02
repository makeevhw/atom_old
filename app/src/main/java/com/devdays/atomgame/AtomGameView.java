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

    protected final int DIRECTION_UP = 0, DIRECTION_DOWN = 1, DIRECTION_RIGHT = 2, DIRECTION_LEFT = 3;
    protected final int ATOM_CODE = 1, FREE_SPACE = 0, MOVE_IN = 2, MOVE_OUT = 3, DOUBLE_LASERS = 4;
    protected int pixForBlockX, pixForBlockY;
    protected int mNumberOfAtoms;
    protected TextView textViewShoots, textViewAtoms, textViewRequest, textViewHeader;
    protected ArrayList<int[]> mChosenAtomsArray; // {cellX, cellY, x11, y11, x12, y12, x21, y21, x22, y22 }
    private int mCurrentLevelNumber;
    private GameMap mGameMap;
    private Paint mPaint;
    private float[] mLineHorizontalData, mLineVerticalData, mLinesCheckedData;
    private boolean mIsPressed = false;
    private int mTouchX, mTouchY;
    private Bitmap bitmapAtomBluePic = null;

    private boolean isThereAreNoShoots;
    private int mCurrentAtom = 0;
    private final float scale = getContext().getResources().getDisplayMetrics().density;
    private boolean mLosed = false;
    private ArrayList<int[]> mSolutionAtomArray;


    // todo todo todo вместо перерисовки заменить, где можно, дорисовкой
    // todo добавить кнопку закгрузки перехода к новому уровню
    // todo и добавить новые уровни
    // todo


    public AtomGameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        getHolder().addCallback(this); // for what?

        //todo for n, m


        //this.invalidate();
        int cellPixelSizePLEASE_DONT_USE_ME_IT_IS_GOVNOKOD = 100;
        //int n = (getContext().getResources().getDisplayMetrics().heightPixels - 200) / cellPixelSizePLEASE_DONT_USE_ME_IT_IS_GOVNOKOD;
        int m = getContext().getResources().getDisplayMetrics().widthPixels / cellPixelSizePLEASE_DONT_USE_ME_IT_IS_GOVNOKOD;


        mGameMap = new GameMap(m, m); // todo redo for n, m, or not, squares are cool
        mPaint = new Paint();


        mLineHorizontalData = new float[(mGameMap.getHeight() + 1) * 4];
        // количество линий о горизонтали на количество координат для каждой линии

        mLineVerticalData = new float[(mGameMap.getWidth() + 1) * 4]; // 4 is max lines for each two cells


        mChosenAtomsArray = new ArrayList<int[]>();
        mNumberOfAtoms = mGameMap.getNumberOfAtoms();
        //mLinesCheckedData = new float[mNumberOfAtoms * 8]; // todo WHY????


    }


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

    private void drawGrid(Canvas canvas) {

        //performDraw blocks - many lines
        //get size for cell

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
        mPaint.setStrokeWidth(1.0f);
        mPaint.setColor(Color.BLUE);
        canvas.drawLines(mLineHorizontalData, mPaint); // рисуем сетку по горизонтали
        //todo redo for cell blocks

        // рисуем сетку по вертикали
        for (int i = 0; i < mLineVerticalData.length; i += 4) {
            mLineVerticalData[i + offsetX1] = pixForBlockX * (i / 4); // x1
            mLineVerticalData[i + offsetY1] = 0; //y1
            mLineVerticalData[i + offsetX2] = pixForBlockX * (i / 4); // x2
            mLineVerticalData[i + offsetY2] = canvas.getHeight(); // y2
        }
        mPaint.setStrokeWidth(1.0f);
        mPaint.setColor(Color.BLUE);
        canvas.drawLines(mLineVerticalData, mPaint);
    }

    private void drawBorders(Canvas canvas) {
        // рисуем границы
        mPaint.setStrokeWidth(10.0f);
        mPaint.setColor(Color.BLACK);
        canvas.drawLine(
                mLineHorizontalData[4],
                mLineHorizontalData[5],
                mLineHorizontalData[6],
                mLineHorizontalData[7],
                mPaint);
        canvas.drawLine(
                mLineHorizontalData[mLineHorizontalData.length - 8],
                mLineHorizontalData[mLineHorizontalData.length - 7],
                mLineHorizontalData[mLineHorizontalData.length - 6],
                mLineHorizontalData[mLineHorizontalData.length - 5],
                mPaint);
        canvas.drawLine(
                mLineVerticalData[4],
                mLineVerticalData[5],
                mLineVerticalData[6],
                mLineVerticalData[7],
                mPaint);
        canvas.drawLine(
                mLineVerticalData[mLineVerticalData.length - 8],
                mLineVerticalData[mLineVerticalData.length - 7],
                mLineVerticalData[mLineVerticalData.length - 6],
                mLineVerticalData[mLineVerticalData.length - 5]
                , mPaint);
    }

    private void drawLazers(Canvas canvas) {
        mPaint.setStrokeWidth(15.0f); // рисуем ходы
        if (mGameMap.mMoveCollector.size() > 0) {
            for (int i = 0; i < mGameMap.mMoveCollector.size(); i++) {
                //for (int i = mGameMap.mMoveCollector.size() - 2; i < mGameMap.mMoveCollector.size(); i++) {


                mPaint.setColor(mGameMap.mMoveCollector.get(i)[4]);

                int
                        x1 = mGameMap.mMoveCollector.get(i)[0],
                        y1 = mGameMap.mMoveCollector.get(i)[1],
                        x2 = mGameMap.mMoveCollector.get(i)[2],
                        y2 = mGameMap.mMoveCollector.get(i)[3];

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
                        mGameMap.mMoveCollector.get(i)[0],
                        mGameMap.mMoveCollector.get(i)[1],
                        mGameMap.mMoveCollector.get(i)[2],
                        mGameMap.mMoveCollector.get(i)[3],
                        mGameMap.mMoveCollector.get(i)[4]);
            }
        }
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

    private void numberShootsRefresh() {
        if (mGameMap.mNumberofShoots < 1)
            textViewShoots.setTextColor(Color.RED);

        textViewShoots.setText(getResources().getString(R.string.number_of_shoots)
                + " " + mGameMap.mNumberofShoots + " ");
        textViewShoots.invalidate(); // refresh
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mIsPressed = true;
                return true;
            case MotionEvent.ACTION_MOVE:
                if (mIsPressed)
                    return true;
                else
                    return false;
            case MotionEvent.ACTION_UP: /// todo fix bug with moving and Xses
                mTouchX = (int) event.getX();
                mTouchY = (int) event.getY();
                mIsPressed = false;
                if (mTouchX < mLineVerticalData[4] && mTouchY > mLineHorizontalData[5]
                        && mTouchY < mLineHorizontalData[mLineHorizontalData.length - 7]) { // LEFT
                    if (isMoveAble(mTouchX, mTouchY, mGameMap.DIRECTION_RIGHT)) {
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
                    if (isMoveAble(mTouchX, mTouchY, mGameMap.DIRECTION_LEFT)) {
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
                    if (isMoveAble(mTouchX, mTouchY, mGameMap.DIRECTION_DOWN)) {
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
                    if (isMoveAble(mTouchX, mTouchY, mGameMap.DIRECTION_UP)) {
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
                    ChoseAtom(mTouchX, mTouchY);
                    //performDraw(); // only if remove
                } else
                    return false;
        }
        return false;
    }

    private void ChoseAtom(int touchX, int touchY) { // mark cell chosed, draw atom here
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
            if (mCurrentAtom < mNumberOfAtoms) {
                int[] arr = new int[2]; // добавляем номера ячеек в arraylist
                arr[0] = cellX;
                arr[1] = cellY;

                mChosenAtomsArray.add(arr);
                mCurrentAtom++;

                textViewAtoms.setText(getResources().getString(R.string.number_of_atoms) + " " +
                        (mNumberOfAtoms - mCurrentAtom) + " ");
                textViewAtoms.invalidate();
            }
        } else { // DECHOOSE
            mChosenAtomsArray.remove(numberToDechoose);
            mCurrentAtom--;
            textViewAtoms.setText(getResources().getString(R.string.number_of_atoms) + " " +
                    (mNumberOfAtoms - mCurrentAtom) + " ");
            textViewAtoms.invalidate();
        }
    }

    private boolean isMoveAble(int mTouchX, int mTouchY, int laserDirection) {
        int cellY, cellX;
        cellX = Math.min(mTouchX / (pixForBlockX /* + 1 */), mGameMap.getWidth() - 1); // бесподобный костыль
        cellY = Math.min(mTouchY / (pixForBlockY /* + 1 */), mGameMap.getHeight() - 1);

        if (
                (mGameMap.mCurrentLevelMap[cellY][cellX] == FREE_SPACE ||
                        mGameMap.mCurrentLevelMap[cellY][cellX] == MOVE_OUT) &&
                        mGameMap.mNumberofShoots > 0
                )
            return true;
        else
            return false;
    }


    private void performDraw() {
        Canvas canvas = getHolder().lockCanvas();
        draw(canvas);
        getHolder().unlockCanvasAndPost(canvas);
    }

    //
    private void drawArrow(Canvas canvas, float x0, float y0, float x1, float y1, int arrowColor) {

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(arrowColor);

        float deltaX = x1 - x0;
        float deltaY = y1 - y0;
        float frac = (float) 0.15;
        /*The variable
            frac : 0 < frac < 1
                determines the size of the arrow head.*/


        float point_x_1 = x0 + (float) ((1 - frac) * deltaX + frac * deltaY);
        float point_y_1 = y0 + (float) ((1 - frac) * deltaY - frac * deltaX);

        float point_x_2 = x1;
        float point_y_2 = y1;

        float point_x_3 = x0 + (float) ((1 - frac) * deltaX - frac * deltaY);
        float point_y_3 = y0 + (float) ((1 - frac) * deltaY + frac * deltaX);

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

    //print text, here
    public void findTextView() {
        textViewShoots = (TextView) (((MainActivity) this.getContext()).findViewById(R.id.textView1));
        textViewShoots.setText(getResources().getString(R.string.number_of_shoots) + " " + mGameMap.mNumberofShoots + " ");

        textViewAtoms = (TextView) ((MainActivity) this.getContext()).findViewById(R.id.textView2);
        textViewAtoms.setText(getResources().getString(R.string.number_of_atoms) + " " + mNumberOfAtoms + " ");

        textViewRequest = (TextView) ((MainActivity) this.getContext()).findViewById(R.id.textViewRequest);
        textViewRequest.setText(" ");

        textViewHeader = (TextView) ((MainActivity) this.getContext()).findViewById(R.id.textViewHeader);
        textViewHeader.setText(getResources().getString(R.string.level) + " " + mCurrentLevelNumber);
    }

    //performDraw something, just read, stay here
    public void CheckResult() {
        if (mCurrentAtom < mNumberOfAtoms) {
            textViewRequest.setTextColor(Color.RED);
            textViewRequest.setText("Not all atoms have been choosen. " + (mNumberOfAtoms - mCurrentAtom) +
                    " atoms left.");
        } else {
            boolean isCorrect = true;

            for (int i = 0; i < mChosenAtomsArray.size(); i++) {
                if (mGameMap.mCurrentLevelMap[mChosenAtomsArray.get(i)[0]][mChosenAtomsArray.get(i)[1]] != ATOM_CODE) {
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
}