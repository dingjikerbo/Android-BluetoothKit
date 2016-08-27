package com.inuker.bluetooth;

import android.app.Application;

/**
 * Created by liwentian on 2016/8/27.
 */
public class MyApplication extends Application {

    private static MyApplication instance;

    public static Application getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
