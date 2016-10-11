package com.inuker.bluetooth.library.connect.request;

import android.bluetooth.BluetoothGatt;

import com.inuker.bluetooth.library.connect.listener.ReadRssiListener;
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse;
import com.inuker.bluetooth.library.connect.response.BluetoothResponse;

/**
 * Created by dingjikerbo on 2015/12/23.
 */
public class BleReadRssiRequest extends BleRequest implements ReadRssiListener {

    public BleReadRssiRequest(String mac, BleGeneralResponse response) {
        super(mac, response);
    }

    @Override
    int getGattResponseListenerId() {
        return GATT_RESP_READ_RSSI;
    }

    @Override
    void processRequest() {
        switch (getConnectStatus()) {
            case STATUS_DEVICE_SERVICE_READY:
                if (readRemoteRssi()) {
                    registerGattResponseListener(this);
                } else {
                    onRequestFinished(REQUEST_FAILED);
                }
                break;

            default:
                onRequestFinished(REQUEST_FAILED);
                break;
        }
    }

    @Override
    public void onReadRemoteRssi(int rssi, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            putIntExtra(EXTRA_RSSI, rssi);
            onRequestFinished(REQUEST_SUCCESS);
        } else {
            onRequestFinished(REQUEST_FAILED);
        }
    }
}

