package com.inuker.bluetooth.library.receiver.listener;

/**
 * Created by liwentian on 2017/1/13.
 */

public interface BluetoothBondListener {
    void onBondStateChanged(String mac, int bondState);
}
