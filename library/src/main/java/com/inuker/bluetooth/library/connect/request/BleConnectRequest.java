package com.inuker.bluetooth.library.connect.request;

import android.bluetooth.BluetoothGatt;

import com.inuker.bluetooth.library.connect.gatt.ServiceDiscoverListener;
import com.inuker.bluetooth.library.connect.response.BluetoothResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.utils.BluetoothLog;

public class BleConnectRequest extends BleRequest implements ServiceDiscoverListener {

    public BleConnectRequest(String mac, BluetoothResponse response) {
        super(mac, response);
    }

    @Override
    protected int getDefaultRetryLimit() {
        // TODO Auto-generated method stub
        return 1;
    }

    @Override
    public int getTimeoutLimit() {
        return 30000;
    }

    @Override
    public void processRequest() {
        switch (getConnectStatus()) {
            case STATUS_DEVICE_CONNECTED:
                throw new IllegalStateException("status impossible");

            case STATUS_DEVICE_SERVICE_READY:
                onRequestFinished(REQUEST_SUCCESS);
                break;

            default:
                if (openBluetoothGatt()) {
                    registerGattResponseListener(this);
                } else {
                    onRequestFinished(REQUEST_FAILED);
                }
                break;
        }
    }

    @Override
    int getGattResponseListenerId() {
        return GATT_RESP_SERVICE_DISCOVER;
    }

    @Override
    public void onServicesDiscovered(int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            onRequestFinished(REQUEST_SUCCESS);
        } else {
            onRequestFinished(REQUEST_FAILED);
        }
    }

    @Override
    void onRequestFinished(int code) {
        if (code == REQUEST_SUCCESS) {
            mExtra.putParcelable(EXTRA_GATT_PROFILE, getGattProfile());
        }
        super.onRequestFinished(code);
    }
}
