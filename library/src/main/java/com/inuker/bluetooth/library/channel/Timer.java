package com.inuker.bluetooth.library.channel;

import android.os.Handler;
import android.os.Looper;

import com.inuker.bluetooth.library.utils.BluetoothLog;

import java.util.concurrent.TimeoutException;

/**
 * Created by liwentian on 2017/4/6.
 */

public class Timer {

    private static Handler mHandler = new Handler(Looper.getMainLooper());

    private static TimerCallback mCallback;

    public static abstract class TimerCallback implements Runnable {

        private String name;

        public TimerCallback(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public final void run() {
            BluetoothLog.e(String.format("%s: Timer expired!!!", name));
            try {
                onTimerCallback();
            } catch (TimeoutException e) {
                BluetoothLog.e(e);
            }
            mCallback = null;
        }

        public abstract void onTimerCallback() throws TimeoutException;
    }

    public static synchronized void stop() {
        mHandler.removeCallbacksAndMessages(null);
        mCallback = null;
    }

    public static synchronized boolean isRunning() {
        return mCallback != null;
    }

    public static synchronized String getName() {
        return isRunning() ? mCallback.getName() : "";
    }

    public static synchronized void start(TimerCallback callback, long duration) {
        mHandler.removeCallbacksAndMessages(null);

        Looper looper = Looper.myLooper();
        if (looper == null) {
            looper = Looper.getMainLooper();
        }
        mHandler = new Handler(looper);
        mHandler.postDelayed(callback, duration);
        mCallback = callback;
    }
}
