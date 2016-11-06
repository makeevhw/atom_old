package com.devdays.atomgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Main Game class, it draws something, update data and call methods
 * from worker class "GameMap"
 * <p>
 * Something goes wrong, and this class become inexcusably enormous and wry
 */

public class AtomGameView extends SurfaceView implements SurfaceHolder.Callback {

    protected TextView textViewShoots, textViewAtoms, textViewRequest, textViewHeader;
    protected ArrayList<Cell> mChosenAtomsArray; // {cellX, cellY }
    SoundPlayer mSoundPlayer;
    private GameMap mGameMap;
    private Paint mPaint;
    private float[] mLineHorizontalData, mLineVerticalData; //need for drawing

    private boolean mIsMoved = false;
    private boolean mLosed = false;
    private boolean mIsHighlighted = false;
    private int mMinMapSize = 6;
    private int mMaxGameSize = 18;

    private int atomHaveChosedCounter = 0;
    private int mFirstTouchX, mFirstTouchY;
    private float mBorderLeftX, mBorderRightX;
    private float mBorderTopY, mBorderBottomY;
    private int pixForBlockX, pixForBlockY;

    private int mLevelMode;
    private int mMapCustomSize = 0; // represent n for map
    private Bitmap bitmapAtomBluePic = null;
    private float lazerStrokeWidth;

    private float cellPixelSize = 50 * getContext().getResources().getDisplayMetrics().scaledDensity; //todo oh no, oh god no

    //private final float scale = getContext().getResources().getDisplayMetrics().density;

    public AtomGameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        getHolder().addCallback(this); // for what?
        mPaint = new Paint();
        mSoundPlayer = new SoundPlayer(context, true); // true by default
        mChosenAtomsArray = new ArrayList<>(); // todo redo

        //generateMapWithParam(0);
    }

    /**
     * Generate new map with levelMode map, levelMode for Level class constructor
     *
     * @param levelMode from Level class, see static fields like EASY, LEGENDARY, etc
     */
    void generateMapWithParam(int levelMode) { // n parametr deleted
        int n;
        if (mMapCustomSize == 0)
            n = (int) (getContext().getResources().getDisplayMetrics().widthPixels / cellPixelSize);
        else
            n = mMapCustomSize;
        mGameMap = new GameMap(n, levelMode); // todo redo for n, m, or not, squares are cool
        mLineHorizontalData = new float[(mGameMap.getHeight() + 1) * 4];
        // количество линий о горизонтали на количество координат для каждой линии
        mLineVerticalData = new float[(mGameMap.getWidth() + 1) * 4]; // 4 is max lines for each two cells

        mLevelMode = levelMode;
        mMapCustomSize = n;
        textViewInit(); // hotfix8

        pixForBlockX = getWidth() / (mGameMap.getWidth()); // fake
        pixForBlockY = getHeight() / (mGameMap.getHeight());

        // scale by width, squares forever
        //setLayoutParams(new LinearLayout.LayoutParams(getLayoutParams().width,
        // (int) (getLayoutParams().height * ((double) pixForBlockX / pixForBlockY))));

        lazerStrokeWidth = pixForBlockX / 8.5f;
        Log.d("ATOM", "generateMapWithParam: lazerStrokeWidth = " + lazerStrokeWidth);
    }

    /**
     * generate horizontal lines for grid
     * use Canvas.drawLines for drawing lines from array
     *
     * @param canvas
     */
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

    /**
     * generate vertical lines array fro grid
     *
     * @param canvas
     */
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


    //////Many draw methods

    /**
     * draw all chose atoms on the grid
     *
     * @param canvas
     */
    void drawChoses(Canvas canvas) {
        int borderOffsetDivisor = 20;

        if (bitmapAtomBluePic == null) { // if just initialized
            reScaleAtomBitmap();
        }


        for (int i = 0; i < mChosenAtomsArray.size(); i++) {
            canvas.drawBitmap(bitmapAtomBluePic,
                    mChosenAtomsArray.get(i).x * pixForBlockX + pixForBlockX / borderOffsetDivisor,
                    mChosenAtomsArray.get(i).y * pixForBlockY + pixForBlockY / borderOffsetDivisor,
                    null);
        }
    }

    /**
     * Рисует сеточку, на которой мы отмечаем атомы
     *
     * @param canvas
     */
    private void drawGrid(Canvas canvas) {

        //performDraw blocks - many lines
        //get size for cell

        //if (!initialized) {
        initVerticalLines(canvas);
        initHorizontalLines(canvas);
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

    /**
     * Draw 4 lines
     * represents boarders for gameMap
     *
     * @param canvas
     */
    private void drawBorders(Canvas canvas) {
        // рисуем границы
        mPaint.setStrokeWidth(5.0f);
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

    /**
     * Draw single lazer, calls from drawLazers
     *
     * @param canvas
     * @param line        contain lazer parameters
     * @param strokeWidth
     */
    private void drawLazer(Canvas canvas, Line line, float strokeWidth, boolean isFat) {
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setColor(line.color);

        float x1 = line.x1;
        float y1 = line.y1;
        float x2 = line.x2;
        float y2 = line.y2;

        float deltaX = x2 - x1;
        float deltaY = y2 - y1;

        float cutFactor = 9.5f;

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

        float baseFrac = strokeWidth * 2;
        float frac = Math.min(1, (isFat ? baseFrac / 175 : baseFrac / 150));
        drawArrow(
                canvas,
                line.x1,
                line.y1,
                line.x2,
                line.y2,
                line.color,
                frac);
    }

    /**
     * Draw all lazers on the canvas, calls for redrawing all surfaceView
     *
     * @param canvas
     */
    private void drawLazers(Canvas canvas) {
        //mPaint.setStrokeWidth(15.0f); // рисуем ходы
        if (mGameMap.mLazersLines.size() > 0) {
            for (int i = 0; i < mGameMap.mLazersLines.size(); i++) {
                drawLazer(canvas, mGameMap.mLazersLines.get(i), lazerStrokeWidth, false);
            }
        }
    }

    /**
     * Magic method from stakoverflow
     * Draw arrow like a god
     *
     * @param canvas
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     * @param arrowColor
     * @param frac       : 0 < frac < 1 - init size of arrow head
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

    /**
     * Draw red circles, which represents answer
     *
     * @param canvas
     */
    private void drawSolution(Canvas canvas) {
        // рисуем оригинальные кресты

        //Paint p = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(8.0f);


        //todo redo for drawlines, faster
        for (int i = 0; i < mGameMap.mSolutionAtomArray.size(); i++) {

            float radius = Math.min(pixForBlockX / 2 - 5, pixForBlockY / 2 - 5);
            float x = pixForBlockX * mGameMap.mSolutionAtomArray.get(i).x + pixForBlockX / 2;
            float y = pixForBlockY * mGameMap.mSolutionAtomArray.get(i).y + pixForBlockY / 2;


            canvas.drawCircle(
                    x, // x
                    y, // y
                    radius, // radius
                    mPaint);
        }
    }

    private void drawFatLines(Line line1, Line line2) {
        Canvas canvas = getHolder().lockCanvas();


        draw(canvas);

        drawLazer(canvas, line1, lazerStrokeWidth * 1.7f, true);
        drawLazer(canvas, line2, lazerStrokeWidth * 1.7f, true);


        getHolder().unlockCanvasAndPost(canvas);
    }

    /**
     * Redraw the whole canvas
     */
    private void performDraw() {
        Canvas canvas = getHolder().lockCanvas();
        draw(canvas);
        getHolder().unlockCanvasAndPost(canvas);
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (mGameMap == null) {
            generateMapWithParam(0); // if nobody created
        }
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
        if (mGameMap == null) {
            generateMapWithParam(0);
        }
        pixForBlockX = getWidth() / (mGameMap.getWidth()); // fake

        pixForBlockY = getHeight() / (mGameMap.getHeight());


        // сейчас всегда масштабируем по ширине, хотим что-то типа квадрата, квадраты тру // WHY SO SERIOUS?
        setLayoutParams(new LinearLayout.LayoutParams(getLayoutParams().width,
                (int) (getLayoutParams().height * ((double) pixForBlockX / pixForBlockY))));


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        performDraw();
        //mSoundPlayer.init();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //mSoundPlayer.destroy();
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
                    mSoundPlayer.stopHighlightedSound();
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
                tryLazerOrChose(mTouchX, mTouchY);
        }
        return false;
    }

    /**
     * Trying to highlight lazer line and its pair if they exist
     * Determine cell index from params x and y
     * Plays sounds
     *
     * @param x x-coordinate of touch event
     * @param y y-coordinate of touch event
     * @return
     */
    private boolean tryHighlighting(int x, int y) {
        int cellX = Math.min(x / (pixForBlockX /* + 1 */), getWidth() - 1); // бесподобный костыль
        int cellY = Math.min(y / (pixForBlockY /* + 1 */), getHeight() - 1);
        Line line = null;
        Cell cl = new Cell(cellX, cellY);

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
            mSoundPlayer.playHighlightedSound();
            drawFatLines(line, line.pair);
            mIsHighlighted = true;
            return true;
        }
        return false;
    }


    /**
     * It try to draw lazers, if we touch to lazer cells
     * (and play sound)
     * Or mark cell as atom if it is not boarder cell
     * It also call markLikeAtomMethod.
     *
     * @param mTouchX x-coordinate of touch event
     * @param mTouchY y-coordinate of touch event
     * @return
     */
    private boolean tryLazerOrChose(int mTouchX, int mTouchY) {
        boolean attacked = false;

        if (mTouchX < mLineVerticalData[4] && mTouchY > mLineHorizontalData[5]
                && mTouchY < mLineHorizontalData[mLineHorizontalData.length - 7]) { // LEFT
            if (mGameMap.isMoveAble(mTouchX, mTouchY, pixForBlockX, pixForBlockY)) {
                mGameMap.MakeMove(
                        mTouchX,
                        mTouchY,
                        mGameMap.DIRECTION_RIGHT,
                        pixForBlockX,
                        pixForBlockY);
                mSoundPlayer.playLaserSound();
                numberShootsRefresh();
                performDraw();
                //attacked = true; // todo refactor this method completely
            } else {
                mSoundPlayer.playSoundError();
                return false;
            }
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
                mSoundPlayer.playLaserSound();
                numberShootsRefresh();
                performDraw();
                return true;
            } else {
                mSoundPlayer.playSoundError();
                return false;
            }
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
                mSoundPlayer.playLaserSound();
                numberShootsRefresh();
                performDraw();
                return true;
            } else {
                mSoundPlayer.playSoundError();
                return false;
            }
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
                mSoundPlayer.playLaserSound();
                numberShootsRefresh();
                performDraw();
                return true;
            } else {
                mSoundPlayer.playSoundError();
                return false;
            }
        } else if (mTouchX > mLineVerticalData[4] &&
                mTouchX < mLineVerticalData[mLineVerticalData.length - 8] &&  // FIELD
                mTouchY > mLineHorizontalData[5] &&
                mTouchY < mLineHorizontalData[mLineHorizontalData.length - 7]) {
            markLikeAtom(mTouchX, mTouchY);
            performDraw(); // only if remove
            return true;
        } else
            return false;
        return false;
    }

    /**
     * Draw or redraw atom bitmap on the field,
     * determine cell index from params.
     * Calls from tryLazerOrChose,
     * yeh, bad design...
     *
     * @param touchX x-coordinate of touch event
     * @param touchY y-coordinate of touch event
     */
    private void markLikeAtom(int touchX, int touchY) { // mark cell chosed, draw atom here
        int cellX = touchX / (pixForBlockX + 1); // начальные номера ячеек
        int cellY = touchY / (pixForBlockY + 1);
        int numberToDechoose = 0;
        boolean isAlreadyMarked = false;
        for (int i = 0; i < mChosenAtomsArray.size(); i++) {
            if (mChosenAtomsArray.get(i).x == cellX &&
                    mChosenAtomsArray.get(i).y == cellY) {
                isAlreadyMarked = true;
                numberToDechoose = i;
            }
        }

        if (!isAlreadyMarked) { // CHOOSE
            if (atomHaveChosedCounter < mGameMap.mNumberOfAtoms) {
                mSoundPlayer.playChoseSound();
                mChosenAtomsArray.add(new Cell(cellX, cellY));
                atomHaveChosedCounter++;

                textViewAtoms.setText(getResources().getString(R.string.number_of_atoms) + " " +
                        (mGameMap.mNumberOfAtoms - atomHaveChosedCounter) + " ");
                textViewAtoms.invalidate();
            } else { // not enough
                mSoundPlayer.playSoundError();
            }
        } else { // DECHOOSE update text
            mSoundPlayer.playDechoseSound();
            mChosenAtomsArray.remove(numberToDechoose);
            atomHaveChosedCounter--;
            textViewAtoms.setText(getResources().getString(R.string.number_of_atoms) + " " +
                    (mGameMap.mNumberOfAtoms - atomHaveChosedCounter) + " ");
            textViewAtoms.invalidate();
        }
    }

    /**
     * Updates lazer count viewing textView
     * Color it RED after lasercount achieving 0.
     */
    private void numberShootsRefresh() {
        if (mGameMap != null) {
            textViewShoots = (TextView) (((MainActivity) this.getContext()).findViewById(R.id.textView1));
            if (mGameMap.mLasersCount < 1)
                textViewShoots.setTextColor(Color.RED);

            textViewShoots.setText(getResources().getString(R.string.number_of_shoots)
                    + " " + mGameMap.mLasersCount + " ");
            textViewShoots.invalidate(); // refresh
        }
    }

    /**
     * Find textViews and assign them default value.
     */
    public void findTextView() {
        textViewShoots = (TextView) (((MainActivity) this.getContext()).findViewById(R.id.textView1));
        textViewShoots.setText(getResources().getString(R.string.number_of_shoots) + " " + 0 + " ");

        textViewAtoms = (TextView) ((MainActivity) this.getContext()).findViewById(R.id.textView2);
        textViewAtoms.setText(getResources().getString(R.string.number_of_atoms) + " " + 0 + " ");

        textViewRequest = (TextView) ((MainActivity) this.getContext()).findViewById(R.id.textViewRequest);
        textViewRequest.setText(" ");

        textViewHeader = (TextView) ((MainActivity) this.getContext()).findViewById(R.id.textViewHeader);
        textViewHeader.setText(getResources().getString(R.string.level) + " " + 0);

    }

    /**
     * Assign values to textView, usefull in some cases
     */
    private void textViewInit() {
        textViewShoots.setTextColor(Color.BLACK);
        textViewShoots.setText(getResources().getString(R.string.number_of_shoots) + " " + mGameMap.mLasersCount + " ");
        textViewAtoms.setText(getResources().getString(R.string.number_of_atoms) + " " + mGameMap.mNumberOfAtoms + " ");
        textViewRequest.setText(" ");
        textViewHeader.setText(getResources().getString(R.string.level) + " " +
                Level.levelName[mLevelMode]);
    }

    /**
     * Rescale bitmap for atom image, calls after resizing map.
     */
    public void reScaleAtomBitmap() {
        bitmapAtomBluePic = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.atome_blue2
        );

        bitmapAtomBluePic = Bitmap.createScaledBitmap(bitmapAtomBluePic,
                pixForBlockX * 9 / 10,
                pixForBlockX * 9 / 10,
                true);

        //transparent background make white
        for (int x = 0; x < bitmapAtomBluePic.getWidth(); x++) {
            for (int y = 0; y < bitmapAtomBluePic.getHeight(); y++) {
                if (bitmapAtomBluePic.getPixel(x, y) == Color.BLACK) {
                    bitmapAtomBluePic.setPixel(x, y, Color.WHITE);
                }
            }
        }
    }

    // something like api for buttons outside, Vadim look here

    /**
     * Calls from button's "Check results" onClick listener, update tex fields in demo.
     * May be useful, but should be replaced
     */
    public void checkResults() {
        if (atomHaveChosedCounter < mGameMap.mNumberOfAtoms) {
            mSoundPlayer.playSoundError();
            textViewRequest.setTextColor(Color.RED);
            textViewRequest.setText("Not all atoms have been choosen. " +
                    (mGameMap.mNumberOfAtoms - atomHaveChosedCounter) +
                    " atoms left.");
        } else {
            boolean isCorrect = true;


            for (int i = 0; i < mChosenAtomsArray.size(); i++) {
                int yIndex = mChosenAtomsArray.get(i).y;
                int xIndex = mChosenAtomsArray.get(i).x;
                if (
                        mGameMap.mCurrentLevelMap[yIndex][xIndex] != mGameMap.ATOM_CODE) {
                    isCorrect = false;
                    break;
                }
            }

            if (isCorrect) {
                mSoundPlayer.playSoundWin();
                textViewRequest.setTextColor(Color.GREEN);
                textViewRequest.setText("All atoms collected. EPIC WIN !");

            } else {
                mSoundPlayer.playSoundLose();
                textViewRequest.setTextColor(Color.RED);
                textViewRequest.setText("There are mistakes. EPIC FAIL !");
                mLosed = true;
                performDraw();
            }

        }

    }

    /**
     * Reload game with new size n
     *
     * @param n size of new map, INCLUDES boarders
     */
    public void resetWithNewSize(int n) {
        mMapCustomSize = Math.min(Math.max(mMinMapSize, n), mMaxGameSize);
        resetGame();
        reScaleAtomBitmap();
    }

    /**
     * Simply reload game with the same size
     */
    public void resetGame() {
        mChosenAtomsArray = new ArrayList<>();
        mIsMoved = false;
        atomHaveChosedCounter = 0;
        mLosed = false;
        mIsHighlighted = false;
        generateMapWithParam(mLevelMode);
        performDraw(); // it even regenerate many things

    }

    /**
     * Reload game with ++size
     */
    public void incMapSize() {
        resetWithNewSize(mMapCustomSize + 1);
    }

    /**
     * Reload game with --size
     */
    public void decMapSize() {
        int newMapSize = Math.max(3, mMapCustomSize - 1);
        resetWithNewSize(newMapSize);
    }

}