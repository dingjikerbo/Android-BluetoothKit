package com.inuker.bluetooth.library.connect.listener;

import com.inuker.bluetooth.library.IBluetoothBase;

/**
 * Created by dingjikerbo on 2016/9/6.
 */
public interface IBleConnectStatusListener extends IBluetoothBase {
    void onConnectStatusChanged(int status);
}
