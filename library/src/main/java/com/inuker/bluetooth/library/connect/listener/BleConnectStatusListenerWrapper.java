package com.inuker.bluetooth.library.connect.listener;

import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.BluetoothUtils;

/**
 * Created by liwentian on 2016/9/7.
 */
public class BleConnectStatusListenerWrapper implements BleConnectStatusListener {

    private BleConnectStatusListener mListener;

    public BleConnectStatusListenerWrapper(BleConnectStatusListener listener) {
        mListener = listener;
    }

    @Override
    public void onConnectStatusChanged(final int status) {
        BluetoothUtils.post(new Runnable() {

            @Override
            public void run() {
                if (mListener != null) {
                    try {
                        mListener.onConnectStatusChanged(status);
                    } catch (Throwable e) {
                        BluetoothLog.e(e);
                    }
                }
            }
        });
    }
}
