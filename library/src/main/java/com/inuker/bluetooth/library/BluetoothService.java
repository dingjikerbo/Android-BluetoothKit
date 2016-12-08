package com.inuker.bluetooth.library;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.inuker.bluetooth.library.utils.BluetoothLog;

/**
 * Created by dingjikerbo on 16/4/8.
 */
public class BluetoothService extends Service {

    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BluetoothLog.v(String.format("BluetoothService onCreate"));
        mContext = getApplicationContext();
        BluetoothContext.set(mContext);
    }

    @Override
    public IBinder onBind(Intent intent) {
        BluetoothLog.v(String.format("BluetoothService onBind"));
        return BluetoothServiceImpl.getInstance();
    }
}
