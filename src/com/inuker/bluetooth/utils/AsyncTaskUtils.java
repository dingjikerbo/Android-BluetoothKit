package com.inuker.bluetooth.utils;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

@SuppressLint("NewApi")
public abstract class AsyncTaskUtils {
    public static <Params, Progress, Result> void exe(AsyncTask<Params, Progress, Result> asyncTask, Params... params) {
        if (Build.VERSION.SDK_INT >= 11) {
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            asyncTask.execute(params);
        }
    }
}
