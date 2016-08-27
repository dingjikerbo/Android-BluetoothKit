package com.inuker.bluetooth.library.connect.request;

import android.bluetooth.BluetoothGatt;

import com.inuker.bluetooth.library.connect.IBleRequestProcessor;
import com.inuker.bluetooth.library.connect.gatt.ReadRssiListener;
import com.inuker.bluetooth.library.connect.response.BluetoothResponse;

/**
 * Created by dingjikerbo on 2015/12/23.
 */
public class BleReadRssiRequest extends BleRequest implements ReadRssiListener {

    public BleReadRssiRequest(BluetoothResponse response) {
        super(response);
    }

    @Override
    int getGattResponseListenerId() {
        return GATT_RESP_READ_RSSI;
    }

    @Override
    public void process(IBleRequestProcessor processor) {
        super.process(processor);

        switch (getConnectStatus()) {
            case STATUS_DEVICE_SERVICE_READY:
                if (readRemoteRssi()) {
                    registerGattResponseListener(this);
                } else {
                    notifyRequestResult(REQUEST_FAILED, null);
                }
                break;

            default:
                notifyRequestResult(REQUEST_FAILED, null);
                break;
        }
    }

    @Override
    public void onReadRemoteRssi(int rssi, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            putIntExtra(EXTRA_RSSI, rssi);
            notifyRequestResult(REQUEST_SUCCESS, null);
        } else {
            notifyRequestResult(REQUEST_FAILED, null);
        }
    }
}

