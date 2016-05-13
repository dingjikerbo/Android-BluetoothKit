package com.inuker.library;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

public class BaseManager {

    private static Handler mHandler;

    private static Context mContext;

    static void setContext(Context context) {
        mContext = context.getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    public static void post(Runnable runnable) {
        postDelayed(runnable, 0);
    }

    public static void postDelayed(Runnable runnable, long delay) {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        mHandler.postDelayed(runnable, delay);
    }
}
