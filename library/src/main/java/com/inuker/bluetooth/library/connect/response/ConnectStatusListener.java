package com.inuker.bluetooth.library.connect.response;

import com.inuker.bluetooth.library.IBluetoothBase;

/**
 * Created by liwentian on 2016/9/6.
 */
public interface ConnectStatusListener extends IBluetoothBase {
    void onConnectStatusChanged(int status);
}
