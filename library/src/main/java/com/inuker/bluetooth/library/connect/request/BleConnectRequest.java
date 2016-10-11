package com.inuker.bluetooth.library.connect.request;

import android.bluetooth.BluetoothGatt;

import com.inuker.bluetooth.library.connect.listener.ServiceDiscoverListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOption;
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse;
import com.inuker.bluetooth.library.connect.response.BluetoothResponse;

public class BleConnectRequest extends BleRequest implements ServiceDiscoverListener {

    private BleConnectOption mOptions;

    public BleConnectRequest(String mac, BleConnectOption options, BleGeneralResponse response) {
        super(mac, response);
        mOptions = options;
    }

    @Override
    protected int getDefaultRetryLimit() {
        // TODO Auto-generated method stub
        return mOptions != null ? mOptions.getMaxRetry() : 1;
    }

    @Override
    public int getTimeoutLimit() {
        return mOptions != null ? mOptions.getTimeoutInMillis() : 60000;
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
