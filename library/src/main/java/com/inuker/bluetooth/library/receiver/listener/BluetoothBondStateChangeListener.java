package com.inuker.bluetooth.library.receiver.listener;

/**
 * Created by liwentian on 2017/1/13.
 */

public abstract class BluetoothBondStateChangeListener implements BluetoothReceiverListener {

    @Override
    public String getName() {
        return BluetoothBondStateChangeListener.class.getSimpleName();
    }

    public abstract void onBondStateChanged(String mac, int bondState);
}
