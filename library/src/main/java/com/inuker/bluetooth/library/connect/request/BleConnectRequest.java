package com.inuker.bluetooth.library.connect.request;

import android.bluetooth.BluetoothGatt;

import com.inuker.bluetooth.library.Code;
import com.inuker.bluetooth.library.connect.IBleRequestProcessor;
import com.inuker.bluetooth.library.connect.gatt.ServiceDiscoverListener;
import com.inuker.bluetooth.library.connect.response.BleResponse;

public class BleConnectRequest extends BleRequest implements ServiceDiscoverListener {

    public BleConnectRequest(BleResponse response) {
        super(response);
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
    public void process(IBleRequestProcessor processor) {
        super.process(processor);

        switch (getConnectStatus()) {
            case STATUS_DEVICE_CONNECTED:
                throw new IllegalStateException("status impossible");

            case STATUS_DEVICE_SERVICE_READY:
                notifyRequestResult(Code.REQUEST_SUCCESS, null);
                break;

            default:
                if (openBluetoothGatt()) {
                    registerGattResponseListener(this);
                } else {
                    notifyRequestResult(Code.REQUEST_FAILED, null);
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
            notifyRequestResult(Code.REQUEST_SUCCESS, null);
        } else {
            notifyRequestResult(Code.REQUEST_FAILED, null);
        }
    }
}
