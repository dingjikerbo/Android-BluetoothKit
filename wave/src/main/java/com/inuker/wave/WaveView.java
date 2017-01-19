package com.inuker.wave;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by liwentian on 2017/1/19.
 */

public class WaveView extends SurfaceView implements SurfaceHolder.Callback {

    private static final float RATIO_PEEK = 0.6f;

    /**
     * 10ms刷新绘制一次
     */
    private static final int RENDER_CYCLE = 1;

    /**
     * 每次占用10个像素宽度
     */
    private static final int CYCLE_WIDTH = 4;

    private int mWidth, mHeight;

    private SurfaceHolder mHolder;
    private RenderThread mRender;

    private Bitmap mBitmap, mBitmapNext;
    private Canvas mCanvas, mCanvasNext;

    private Paint mPaint;

    private int mValue;

    /**
     * 最大振幅，即波峰的高度或者波谷的高度
     */
    private int mMax;

    private float mRatio;

    public WaveView(Context context) {
        super(context);
        init();
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setValue(int y) {
        if (y > mMax || y < -mMax) {
            throw new IllegalArgumentException();
        }
        mValue = (int) (mHeight / 2 + mRatio * y);
    }

    public void setMaxValue(int max) {
        mMax = max;
    }

    private void init() {
        Log.i("bush", "WaveView init");
        mHolder = getHolder();
        mRender = new RenderThread();
        mHolder.addCallback(this);
        setZOrderOnTop(true);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(5);
    }

    public void start() {
        post(new Runnable() {

            @Override
            public void run() {
                initBuffer();
            }
        });
    }

    private void initBuffer() {
        mWidth = getWidth();
        mHeight = getHeight();
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        if (mBitmapNext != null) {
            mBitmapNext.recycle();
            mBitmapNext = null;
        }
        mBitmapNext = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mCanvasNext = new Canvas(mBitmapNext);

        if (mMax <= 0) {
            mMax = (int) (mHeight * RATIO_PEEK / 2);
        }

        mRatio = mHeight * RATIO_PEEK / 2 / (mMax * 2);

        mRender.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.i("bush", "surfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.i("bush", "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.i("bush", "surfaceDestroyed");
    }

    private class RenderThread extends Thread {

        RenderThread() {
        }

        @Override
        public void run() {
            int x = 0, y = mHeight / 2, nextX = 0, nextY = 0;

            Bitmap bitmap = null;

            long start = System.currentTimeMillis();

            while (true) {
                start = System.currentTimeMillis();
                if (x < mWidth) {
                    nextX = x + CYCLE_WIDTH;
                    nextY = mValue;
                    mCanvas.drawLine(x, y, nextX, nextY, mPaint);
                    x = nextX;
                    y = nextY;
                    bitmap = mBitmap;
                } else {
                    mCanvasNext.drawBitmap(mBitmap, new Rect(CYCLE_WIDTH, 0, mWidth, mHeight),
                            new Rect(0, 0, mWidth - CYCLE_WIDTH, mHeight),
                            mPaint);
                    mCanvasNext.drawLine(mWidth - CYCLE_WIDTH, y, mWidth, mValue, mPaint);
                    x = mWidth;
                    y = mValue;
                    bitmap = mBitmapNext;
                }

                Log.i("bush", String.format("time1 = %dms", System.currentTimeMillis() - start));

                start = System.currentTimeMillis();

                Canvas canvas = null;
                try {
                    synchronized (mHolder) {
                        canvas = mHolder.lockCanvas();
                        canvas.drawColor(Color.BLACK);
                        canvas.drawBitmap(bitmap, 0, 0, mPaint);
                    }
                } finally {
                    if (canvas != null) {
                        mHolder.unlockCanvasAndPost(canvas);
                    }
                    Log.i("bush", String.format("time2 = %dms", System.currentTimeMillis() - start));
                }

                if (bitmap == mBitmapNext) {
                    swapCanvas();
                    mCanvasNext.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                }

//                try {
//                    Thread.sleep(RENDER_CYCLE);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }

        }

        private void swapCanvas() {
            Canvas canvas = mCanvas;
            mCanvas = mCanvasNext;
            mCanvasNext = canvas;

            Bitmap bitmap = mBitmap;
            mBitmap = mBitmapNext;
            mBitmapNext = bitmap;
        }
    }
}
