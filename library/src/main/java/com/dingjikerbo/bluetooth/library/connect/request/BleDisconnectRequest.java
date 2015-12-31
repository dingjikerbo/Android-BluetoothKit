package com.dingjikerbo.bluetooth.library.connect.request;


public class BleDisconnectRequest extends BleRequest<Void> {

    public BleDisconnectRequest() {
        super(null);
        mRequestType = REQUEST_TYPE_DISCONNECT;
    }
}
