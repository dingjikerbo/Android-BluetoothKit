package com.inuker.bluetooth.library;

import android.os.Bundle;

import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleReadRssiResponse;
import com.inuker.bluetooth.library.connect.response.BleResponse;
import com.inuker.bluetooth.library.utils.BluetoothConstants;

/**
 * Created by liwentian on 2016/3/10.
 */
public class XmBleResponse implements BleResponse<Bundle> {

    public BleResponse mResponse;

    public XmBleResponse(BleResponse response) {
        mResponse = response;
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
