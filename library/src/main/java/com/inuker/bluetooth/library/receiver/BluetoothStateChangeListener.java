package com.inuker.bluetooth.library.receiver;

/**
 * Created by liwentian on 2016/11/25.
 */

public interface BluetoothStateChangeListener {
    void onBluetoothStateChanged(int prevState, int curState);
}
