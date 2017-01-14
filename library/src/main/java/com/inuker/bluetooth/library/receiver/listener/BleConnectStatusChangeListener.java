package com.inuker.bluetooth.library.receiver.listener;

/**
 * Created by dingjikerbo on 16/11/26.
 */

public abstract class BleConnectStatusChangeListener extends BluetoothReceiverListener {

    protected abstract void onConnectStatusChanged(String mac, int status);

    @Override
    public void onInvoke(Object... args) {
        String mac = (String) args[0];
        int status = (int) args[1];
        onConnectStatusChanged(mac, status);
    }

    @Override
    public String getName() {
        return BleConnectStatusChangeListener.class.getSimpleName();
    }
}
