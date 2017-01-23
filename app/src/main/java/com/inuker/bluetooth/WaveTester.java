package com.inuker.bluetooth;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.Random;

import static android.R.attr.y;

/**
 * Created by dingjikerbo on 17/1/23.
 */

public class WaveTester implements IWaveGenerator, Handler.Callback {

    private Random mRandom;

    private WaveResponse mResponse;

    private Handler mHandler;

    private float fx = 0;

    WaveTester() {
        mRandom = new Random();
        mHandler = new Handler(Looper.getMainLooper(), this);
    }

    @Override
    public void start(WaveResponse response) {
        mResponse = response;
        mHandler.sendEmptyMessageDelayed(1, 500);
    }

    @Override
    public void stop() {
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean handleMessage(Message msg) {
//        short x = (short) (mRandom.nextInt() & 0xffff);
//        short y = (short) (mRandom.nextInt() & 0xffff);
//        short z = (short) (mRandom.nextInt() & 0xffff);

        short x = (short) (Math.sin(fx) * Short.MAX_VALUE);
        short y = (short) (Math.sin(2 * fx) * Short.MAX_VALUE);
        short z = (short) (Math.sin(3 * fx) * Short.MAX_VALUE);

        fx += 0.1;

        mResponse.onRaw(x, y, z);
        mHandler.sendEmptyMessageDelayed(1, 100);
        return true;
    }
}
