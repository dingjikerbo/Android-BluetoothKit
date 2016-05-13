package com.inuker.library.connect;

import android.os.Bundle;

import com.inuker.library.BluetoothConstants;
import com.inuker.library.connect.response.BleConnectResponse;
import com.inuker.library.connect.response.BleReadResponse;
import com.inuker.library.connect.response.BleReadRssiResponse;
import com.inuker.library.connect.response.BleResponse;

/**
 * Created by liwentian on 2016/4/13.
 */
public class BleResponser implements BleResponse<Bundle> {

    public BleResponse mResponse;

    private BleResponser(BleResponse response) {
        mResponse = response;
    }

    public static BleResponser newInstance(BleResponse response) {
        return new BleResponser(response);
    }

    @Override
    public void onResponse(int code, Bundle bundle) {
        if (mResponse == null) {
            return;
        }

        if (mResponse instanceof BleConnectResponse) {
            mResponse.onResponse(code, bundle);
        }
        else if (mResponse instanceof BleReadResponse) {
            mResponse.onResponse(code, bundle != null ? bundle.getByteArray(BluetoothConstants.KEY_BYTES) : null);
        }
        else if (mResponse instanceof BleReadRssiResponse) {
            mResponse.onResponse(code, bundle != null ? bundle.getInt(BluetoothConstants.KEY_RSSI) : 0);
        }
        else {
            mResponse.onResponse(code, null);
        }
    }
}
