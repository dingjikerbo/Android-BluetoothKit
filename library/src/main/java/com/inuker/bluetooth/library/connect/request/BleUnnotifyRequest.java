package com.inuker.bluetooth.library.connect.request;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattDescriptor;

import com.inuker.bluetooth.library.Code;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.connect.listener.WriteDescriptorListener;
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse;

import java.util.UUID;

/**
 * Created by dingjikerbo on 2015/11/10.
 */
public class BleUnnotifyRequest extends BleRequest implements WriteDescriptorListener {

    public BleUnnotifyRequest(UUID service, UUID character, BleGeneralResponse response) {
        super(response);
        mServiceUUID = service;
        mCharacterUUID = character;
    }

    @Override
    public void processRequest() {
        switch (getCurrentStatus()) {
            case Constants.STATUS_DEVICE_DISCONNECTED:
                onRequestCompleted(Code.REQUEST_FAILED);
                break;

            case Constants.STATUS_DEVICE_CONNECTED:
                closeNotify();
                break;

            case Constants.STATUS_DEVICE_SERVICE_READY:
                closeNotify();
                break;

            default:
                onRequestCompleted(Code.REQUEST_FAILED);
                break;
        }
    }

    private void closeNotify() {
        if (!setCharacteristicNotification(mServiceUUID, mCharacterUUID, false)) {
            onRequestCompleted(Code.REQUEST_FAILED);
        } else {
            startRequestTiming();
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGattDescriptor descriptor, int status) {
        stopRequestTiming();

        if (status == BluetoothGatt.GATT_SUCCESS) {
            onRequestCompleted(Code.REQUEST_SUCCESS);
        } else {
            onRequestCompleted(Code.REQUEST_FAILED);
        }
    }
}
