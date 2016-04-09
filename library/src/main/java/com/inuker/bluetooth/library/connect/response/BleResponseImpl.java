package com.inuker.bluetooth.library.connect.response;

import android.os.Bundle;
import android.os.RemoteException;

import com.inuker.bluetooth.library.IBleResponse;

/**
 * Created by dingjikerbo on 16/4/8.
 */
public class BleResponseImpl extends IBleResponse.Stub {

    private BleResponse response;

    private BleResponseImpl(BleResponse response) {
        this.response = response;
    }

    public static BleResponseImpl newInstance(BleResponse response) {
        return new BleResponseImpl(response);
    }

    @Override
    public void onResponse(int code, Bundle data) throws RemoteException {
        if (response instanceof BleConnectResponse) {
            response.onResponse(code, null);
        }
    }
}
