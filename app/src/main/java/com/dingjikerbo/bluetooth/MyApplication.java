package com.dingjikerbo.bluetooth;

import android.app.Application;

import com.inuker.bluetooth.library.BluetoothManager;

/**
 * Created by liwentian on 2016/1/13.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        BluetoothManager.initial(this);
    }
}
