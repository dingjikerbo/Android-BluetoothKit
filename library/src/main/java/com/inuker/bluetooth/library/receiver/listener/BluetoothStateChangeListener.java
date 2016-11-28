package com.inuker.bluetooth.library.receiver.listener;

/**
 * Created by dingjikerbo on 2016/11/25.
 */

public abstract class BluetoothStateChangeListener implements BluetoothReceiverListener {

    public String getName() {
        return BluetoothStateChangeListener.class.getSimpleName();
    }

    public abstract void onBluetoothStateChanged(int prevState, int curState);
}
