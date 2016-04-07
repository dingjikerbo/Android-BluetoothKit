package com.inuker.bluetooth.library.connect.response;

import android.os.Bundle;

/**
 * Created by dingjikerbo on 16/4/8.
 */
public class BleResponseDispatcher implements BleResponse<Bundle> {

    public BleResponse response;

    private BleResponseDispatcher(BleResponse response) {
        this.response = response;
    }

    public static BleResponseDispatcher newInstance(BleResponse response) {
        return new BleResponseDispatcher(response);
    }

    @Override
    public void onResponse(int code, Bundle data) {

    }
}



