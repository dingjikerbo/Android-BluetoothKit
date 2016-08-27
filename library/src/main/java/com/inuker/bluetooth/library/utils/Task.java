package com.inuker.bluetooth.library.utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

/**
 * Created by dingjikerbo on 2016/8/24.
 */
public abstract class Task extends AsyncTask<Void, Void, Void> {

    public abstract void doInBackground();

    private static Handler mHandler;

    @Override
    protected Void doInBackground(Void... params) {
        doInBackground();
        return null;
    }

    private static Handler getHandler() {
        if (mHandler == null) {
            synchronized (Task.class) {
                if (mHandler == null) {
                    mHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
        return mHandler;
    }

    public void executeDelayed(final Executor executor, long delayInMillis) {
        getHandler().postDelayed(new Runnable() {

            @Override
            public void run() {
                executeOnExecutor(executor != null ? executor : AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }, delayInMillis);
    }

    public void execute(final Executor executor) {
        getHandler().post(new Runnable() {

            @Override
            public void run() {
                executeOnExecutor(executor != null ? executor : AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
    }

    public static void execute(Task task, Executor executor) {
        if (task != null) {
            task.execute(executor);
        }
    }

    public static void executeDelayed(Task task, Executor executor, long delayInMillis) {
        if (task != null) {
            task.executeDelayed(executor, delayInMillis);
        }
    }

    public static void executeDelayed(final FutureTask task, final Executor executor, long delayInMillis) {
        if (task != null && executor != null) {
            getHandler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    executor.execute(task);
                }
            }, delayInMillis);
        }
    }
}
