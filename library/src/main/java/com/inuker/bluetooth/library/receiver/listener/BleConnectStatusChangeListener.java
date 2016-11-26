package com.inuker.bluetooth.library.receiver.listener;

import com.inuker.bluetooth.library.connect.listener.IBleConnectStatusListener;

/**
 * Created by dingjikerbo on 16/11/26.
 */

public abstract class BleConnectStatusChangeListener implements BluetoothReceiverListener, IBleConnectStatusListener {

    public String getName() {
        return BleConnectStatusChangeListener.class.getSimpleName();
    }
}
