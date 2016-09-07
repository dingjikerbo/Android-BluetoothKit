package com.inuker.bluetooth.library.connect.listener;

import com.inuker.bluetooth.library.IBluetoothBase;

/**
 * Created by liwentian on 2016/9/6.
 */
public interface BleConnectStatusListener extends IBluetoothBase {
    void onConnectStatusChanged(int status);
}
