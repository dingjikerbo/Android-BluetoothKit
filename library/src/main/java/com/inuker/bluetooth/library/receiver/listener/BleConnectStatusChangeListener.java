package com.inuker.bluetooth.library.receiver.listener;

/**
 * Created by dingjikerbo on 16/11/26.
 */

public interface BleConnectStatusChangeListener extends BluetoothReceiverListener {
    void onConnectStatusChanged(String mac, int status);
}
