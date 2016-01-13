package com.dingjikerbo.bluetooth;

import android.app.Application;

import com.dingjikerbo.bluetooth.library.BluetoothManager;

/**
 * Created by liwentian on 2016/1/13.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        BluetoothManager.init(this);
    }
}
