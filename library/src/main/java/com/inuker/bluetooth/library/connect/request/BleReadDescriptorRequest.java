package com.inuker.bluetooth.library.connect.request;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattDescriptor;

import com.inuker.bluetooth.library.Code;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.connect.listener.ReadDescriptorListener;
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse;

import java.util.UUID;

/**
 * Created by dingjikerbo on 2016/11/28.
 */

public class BleReadDescriptorRequest extends BleRequest implements ReadDescriptorListener {

    private UUID mServiceUUID;
    private UUID mCharacterUUID;
    private UUID mDescriptorUUID;

    public BleReadDescriptorRequest(UUID service, UUID character, UUID descriptor, BleGeneralResponse response) {
        super(response);
        mServiceUUID = service;
        mCharacterUUID = character;
        mDescriptorUUID = descriptor;
    }

    @Override
    public void processRequest() {
        switch (getCurrentStatus()) {
            case Constants.STATUS_DEVICE_DISCONNECTED:
                onRequestCompleted(Code.REQUEST_FAILED);
                break;

            case Constants.STATUS_DEVICE_CONNECTED:
                startRead();
                break;

            case Constants.STATUS_DEVICE_SERVICE_READY:
                startRead();
                break;

            default:
                onRequestCompleted(Code.REQUEST_FAILED);
                break;
        }
    }

    private void startRead() {
        if (!readDescriptor(mServiceUUID, mCharacterUUID, mDescriptorUUID)) {
            onRequestCompleted(Code.REQUEST_FAILED);
        } else {
            startRequestTiming();
        }
    }

    @Override
    public void onDescriptorRead(BluetoothGattDescriptor descriptor, int status, byte[] value) {
        stopRequestTiming();

        if (status == BluetoothGatt.GATT_SUCCESS) {
            putByteArray(Constants.EXTRA_BYTE_VALUE, value);
            onRequestCompleted(Code.REQUEST_SUCCESS);
        } else {
            onRequestCompleted(Code.REQUEST_FAILED);
        }
    }
}
