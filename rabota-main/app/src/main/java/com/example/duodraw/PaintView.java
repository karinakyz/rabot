package com.example.duodraw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

//наследуем от класса View
public class PaintView extends View {

    public static int BRUSH_SIZE = 20;
    public static final int DEFAULT_COLOR = Color.RED;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE = 4;
    private static int currentColor;
    private float mX, mY;
    private Path mPath;
    private Paint mPaint;
    private static ArrayList<FingerPath> paths = new ArrayList<>();
    private static int backgroundColor = DEFAULT_BG_COLOR;
    private static int strokeWidth;
    private static boolean emboss;
    private static boolean blur;
    private MaskFilter mEmboss;
    private MaskFilter mBlur;
    private Bitmap mBitmap;//класс, который передаёт рисунок в Canvas
    private Canvas mCanvas; //форма для рисования
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);

    public PaintView(Context context) {
        this(context, null);
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(DEFAULT_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);

        mEmboss = new EmbossMaskFilter(new float[] {1, 1, 1}, 0.7f, 8, 4.5f); //размер тени
        mBlur = new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL);//настройка размытия
    }

    public void init(DisplayMetrics metrics) {
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);//размер экрана, цветовой код
        mCanvas = new Canvas(mBitmap);//передаём рисунок

        currentColor = DEFAULT_COLOR;
        strokeWidth = BRUSH_SIZE;//размер кисточки
    }

//функции, которые передают настройки кисти
    public static void size_normal() {
        strokeWidth = 10;
    }
    public static void size_big() {
        strokeWidth = 15;
    }
    public static void size_small() {
        strokeWidth = 5;
    }
    public static void color_green() {
        currentColor = Color.GREEN;
    }
    public static void color_red() {
        currentColor = Color.RED;
    }
    public static void color_black() {
        currentColor = Color.BLACK;
    }

    public static void normal() {
        emboss = false;
        blur = false;
    }

    public static void emboss() {
        emboss = true;
        blur = false;
    }

    public static void blur() {
        emboss = false;
        blur = true;
    }

    public static void clear() {
        backgroundColor = DEFAULT_BG_COLOR;
        paths.clear();
        normal();
    }

    @Override
    protected void onDraw(Canvas canvas) { //функция, в которой ресуется
        canvas.save();
        mCanvas.drawColor(backgroundColor);

        for (FingerPath fp : paths) {
            mPaint.setColor(fp.color);
            mPaint.setStrokeWidth(fp.strokeWidth);
            mPaint.setMaskFilter(null);

            if (fp.emboss)
                mPaint.setMaskFilter(mEmboss);
            else if (fp.blur)
                mPaint.setMaskFilter(mBlur);

            mCanvas.drawPath(fp.path, mPaint);

        }

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }

    private void touchStart(float x, float y) { //координаты кисти
        mPath = new Path();
        FingerPath fp = new FingerPath(currentColor, emboss, blur, strokeWidth, mPath);
        paths.add(fp);

        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2); //передаются координаты
            mX = x;
            mY = y;
        }
    }

    private void touchUp() {
        mPath.lineTo(mX, mY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {  //функция, которая записывает координаты прикосновения к экрану
        float x = event.getX();
        float y = event.getY();

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE :
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP :
                touchUp();
                invalidate();
                break;
        }

        return true;
    }
}