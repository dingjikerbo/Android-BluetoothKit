package com.inuker.bluetooth;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.IBluetoothClient;

/**
 * Created by dingjikerbo on 2016/8/27.
 */
public class ClientManager {

    private static IBluetoothClient mClient;

    public static IBluetoothClient getClient() {
        if (mClient == null) {
            synchronized (ClientManager.class) {
                if (mClient == null) {
                    mClient = BluetoothClient.getInstance(MyApplication.getInstance());
                }
            }
        }
        return mClient;
    }
}
