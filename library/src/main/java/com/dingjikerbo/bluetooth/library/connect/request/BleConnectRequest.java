package com.dingjikerbo.bluetooth.library.connect.request;

import android.os.Bundle;

import com.dingjikerbo.bluetooth.library.response.BleConnectResponse;

public class BleConnectRequest extends BleRequest<Bundle> {

    public BleConnectRequest(BleConnectResponse response) {
        super(response);
        mRequestType = REQUEST_TYPE_CONNECT;
    }

    @Override
    protected int getDefaultRetryLimit() {
        // TODO Auto-generated method stub
        return 3;
    }
}
