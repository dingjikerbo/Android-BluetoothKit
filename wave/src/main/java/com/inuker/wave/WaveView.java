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
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.View;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by liwentian on 2017/1/19.
 */

public class WaveView extends View {

    private int mWidth, mHeight;

    private Bitmap mBitmap;
    private Canvas mCanvas;

    private Paint mPaint;

    private Config mConfig;

    private List<Integer> mList;

    public WaveView(Context context) {
        super(context);
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setConfig(Config config) {
        mConfig = config;
    }

    public static class Config {

        /**
         * 背景色
         */
        private int backgroundColor = Color.BLACK;
        /**
         * 前景色
         */
        private int foregroundColor = Color.WHITE;
        /**
         * 线条宽
         */
        private int strokeWidth;
        /**
         * 分几段
         */
        private int sliceCount;
        /**
         * 纵向范围区间
         */
        private int rangeStart, rangeEnd;

        public int getBackgroundColor() {
            return backgroundColor;
        }

        public void setBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        public int getForegroundColor() {
            return foregroundColor;
        }

        public void setForegroundColor(int foregroundColor) {
            this.foregroundColor = foregroundColor;
        }

        public int getStrokeWidth() {
            return strokeWidth;
        }

        public void setStrokeWidth(int strokeWidth) {
            this.strokeWidth = strokeWidth;
        }

        public int getSliceCount() {
            return sliceCount;
        }

        public void setSliceCount(int sliceCount) {
            this.sliceCount = sliceCount;
        }

        public int getRangeStart() {
            return rangeStart;
        }

        public void setRangeStart(int rangeStart) {
            this.rangeStart = rangeStart;
        }

        public int getRangeEnd() {
            return rangeEnd;
        }

        public void setRangeEnd(int rangeEnd) {
            this.rangeEnd = rangeEnd;
        }

        public static class Builder {

            Config config = new Config();

            public Builder setBackgroundColor(int color) {
                config.backgroundColor= color;
                return this;
            }

            public Builder setForegroundColor(int color) {
                config.foregroundColor = color;
                return this;
            }

            public Builder setStrokeWidth(int width) {
                config.strokeWidth = width;
                return this;
            }

            public Builder setSliceCount(int count) {
                config.sliceCount = count;
                return this;
            }

            public Builder setRange(int start, int end) {
                config.rangeStart = start;
                config.rangeEnd = end;
                return this;
            }

            public Config build() {
                return config;
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setWaveSize(getWidth(), getHeight());
    }

    private void setWaveSize(int width, int height) {
        if (width != mWidth && height != mHeight && width > 0 && height > 0) {
            mWidth = width;
            mHeight = height;
            if (mBitmap != null) {
                mBitmap.recycle();
                mBitmap = null;
            }
            mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);

            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mList = new LinkedList<>();
        }
    }

    public void setValue(int y) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new RuntimeException();
        }
        if (y > mConfig.rangeEnd || y < mConfig.rangeStart) {
            throw new IllegalArgumentException();
        }
        float ratio = 1.0f * Math.abs(mConfig.rangeStart - mConfig.rangeEnd) / mHeight;

        mList.add((int) (mHeight / 2 + y / ratio));
        if (mList.size() > mConfig.sliceCount) {
            mList.remove(0);
        }

        mCanvas.drawColor(mConfig.backgroundColor);

        int preX = 0, preY = mList.get(0), curX, curY;

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mConfig.strokeWidth);
        mPaint.setColor(mConfig.foregroundColor);

        for (int i = 0; i < mList.size(); i++) {
            curX = i * (mWidth / mConfig.sliceCount);
            curY = mList.get(i);
            mCanvas.drawLine(preX, preY, curX, curY, mPaint);
            preX = curX;
            preY = curY;
        }

        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setWaveSize(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap != null) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            canvas.drawBitmap(mBitmap, 0, 0, mPaint);
        }
    }
}
