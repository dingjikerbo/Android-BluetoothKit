package com.inuker.bluetooth.library.connect.request;


public class BleDisconnectRequest extends BleRequest {

    public BleDisconnectRequest() {
        super(null);
    }

    @Override
    void processRequest() {
        closeBluetoothGatt();
        onRequestFinished(REQUEST_SUCCESS);
    }
}
