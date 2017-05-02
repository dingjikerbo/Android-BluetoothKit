package com.inuker.bluetooth.library;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by dingjikerbo on 2016/10/26.
 */

public class BluetoothContext {

    private static Context mContext;
    private static Handler mHandler;

    public static void set(Context context) {
        mContext = context;
    }

    public static Context get() {
        return mContext;
    }

    public static void post(Runnable runnable) {
        postDelayed(runnable, 0);
    }

    public static void postDelayed(Runnable runnable, long delayInMillis) {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        mHandler.postDelayed(runnable, delayInMillis);
    }

    public static String getCurrentMethodName() {
        StackTraceElement e = Thread.currentThread().getStackTrace()[4];
        return e.getMethodName();
    }
}
