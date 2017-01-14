package com.inuker.bluetooth.library.receiver.listener;

/**
 * Created by liwentian on 2017/1/13.
 */

public abstract class BluetoothBondStateChangeListener extends BluetoothReceiverListener {

    protected abstract void onBondStateChanged(String mac, int bondState);

    @Override
    public void onInvoke(Object... args) {
        String mac = (String) args[0];
        int bondState = (int) args[1];
        onBondStateChanged(mac, bondState);
    }

    @Override
    public String getName() {
        return BluetoothBondStateChangeListener.class.getSimpleName();
    }
}
