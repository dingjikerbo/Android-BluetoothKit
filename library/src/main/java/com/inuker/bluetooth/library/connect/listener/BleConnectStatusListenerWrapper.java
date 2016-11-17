package com.inuker.bluetooth.library.connect.listener;

import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.BluetoothUtils;

/**
 * Created by dingjikerbo on 2016/9/7.
 */
public class BleConnectStatusListenerWrapper extends BleConnectStatusListener {

    private BleConnectStatusListenerWrapper(BleConnectStatusListener listener) {
        super(listener);
    }

    public static BleConnectStatusListener from(BleConnectStatusListener listener) {
        return new BleConnectStatusListenerWrapper(listener);
    }

    @Override
    public void onConnectStatusChanged(final String mac, final int status) {
        BluetoothUtils.post(new Runnable() {

            @Override
            public void run() {
                if (listener != null) {
                    try {
                        listener.onConnectStatusChanged(mac, status);
                    } catch (Throwable e) {
                        BluetoothLog.e(e);
                    }
                }
            }
        });
    }
}
