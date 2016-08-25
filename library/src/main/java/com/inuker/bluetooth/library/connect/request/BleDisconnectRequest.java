package com.inuker.bluetooth.library.connect.request;


import com.inuker.bluetooth.library.Code;
import com.inuker.bluetooth.library.connect.IBleRequestProcessor;

public class BleDisconnectRequest extends BleRequest {

    public BleDisconnectRequest() {
        super(null);
        mRequestType = REQUEST_TYPE_DISCONNECT;
    }

    @Override
    public void process(IBleRequestProcessor processor) {
        super.process(processor);
        closeBluetoothGatt();
        notifyRequestResult(Code.REQUEST_SUCCESS, null);
    }
}
