package com.inuker.bluetooth.library;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by dingjikerbo on 16/4/8.
 */
public class BluetoothService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return BluetoothManager.getInstance();
    }
}
