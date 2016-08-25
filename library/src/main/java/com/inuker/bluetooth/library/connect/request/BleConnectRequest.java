package com.inuker.bluetooth.library.connect.request;

import android.bluetooth.BluetoothGatt;

import com.inuker.bluetooth.library.Code;
import com.inuker.bluetooth.library.connect.IBleRequestProcessor;
import com.inuker.bluetooth.library.connect.gatt.ServiceDiscoverListener;
import com.inuker.bluetooth.library.connect.response.BleResponse;

public class BleConnectRequest extends BleRequest implements ServiceDiscoverListener {

    public BleConnectRequest(BleResponse response) {
        super(response);
        mRequestType = REQUEST_TYPE_CONNECT;
    }

    @Override
    protected int getDefaultRetryLimit() {
        // TODO Auto-generated method stub
        return 2;
    }

    /**
     * 红米note 2上发现service特别慢，这里给超时延长点
     * @return
     */
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
