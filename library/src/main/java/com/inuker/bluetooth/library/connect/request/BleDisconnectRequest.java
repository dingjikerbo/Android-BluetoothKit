package com.inuker.bluetooth.library.connect.request;


public class BleDisconnectRequest extends BleRequest {

    public BleDisconnectRequest() {
        super(null);
        mRequestType = REQUEST_TYPE_DISCONNECT;
    }
}
