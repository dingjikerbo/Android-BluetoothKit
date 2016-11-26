package com.inuker.bluetooth.library.receiver.listener;

/**
 * Created by liwentian on 2016/11/25.
 */

public interface BluetoothStateChangeListener extends BluetoothReceiverListener {

    void onBluetoothStateChanged(int prevState, int curState);
}
