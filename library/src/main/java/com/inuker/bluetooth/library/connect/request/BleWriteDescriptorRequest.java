package com.inuker.bluetooth.library.connect.request;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattDescriptor;

import com.inuker.bluetooth.library.Code;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.connect.listener.WriteDescriptorListener;
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse;

import java.util.UUID;

/**
 * Created by dingjikerbo on 2016/11/28.
 */

public class BleWriteDescriptorRequest extends BleRequest implements WriteDescriptorListener {

    private UUID mServiceUUID;
    private UUID mCharacterUUID;
    private UUID mDescriptorUUID;
    private byte[] mBytes;

    public BleWriteDescriptorRequest(UUID service, UUID character, UUID descriptor, byte[] bytes, BleGeneralResponse response) {
        super(response);
        mServiceUUID = service;
        mCharacterUUID = character;
        mDescriptorUUID = descriptor;
        mBytes = bytes;
    }

    @Override
    public void processRequest() {
        switch (getCurrentStatus()) {
            case Constants.STATUS_DEVICE_DISCONNECTED:
                onRequestCompleted(Code.REQUEST_FAILED);
                break;

            case Constants.STATUS_DEVICE_CONNECTED:
                startWrite();
                break;

            case Constants.STATUS_DEVICE_SERVICE_READY:
                startWrite();
                break;

            default:
                onRequestCompleted(Code.REQUEST_FAILED);
                break;
        }
    }

    private void startWrite() {
        if (!writeDescriptor(mServiceUUID, mCharacterUUID, mDescriptorUUID, mBytes)) {
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
