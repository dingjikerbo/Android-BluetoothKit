package com.inuker.bluetooth.library.connect.request;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import com.inuker.bluetooth.library.Code;
import com.inuker.bluetooth.library.connect.IBleRequestProcessor;
import com.inuker.bluetooth.library.connect.gatt.WriteCharacterListener;
import com.inuker.bluetooth.library.connect.response.BleResponse;

import java.util.UUID;

public class BleWriteRequest extends BleRequest implements WriteCharacterListener {

    public BleWriteRequest(UUID service, UUID character, byte[] bytes,
                           BleResponse response) {
        super(response);
        mRequestType = REQUEST_TYPE_WRITE;
        mServiceUUID = service;
        mCharacterUUID = character;
        mBytes = bytes;
    }

    public byte[] getBytes() {
        return mBytes;
    }

    @Override
    public void process(IBleRequestProcessor processor) {
        super.process(processor);

        switch (getConnectStatus()) {
            case STATUS_DEVICE_SERVICE_READY:
                if (writeCharacteristic(mServiceUUID, mCharacterUUID, mBytes)) {
                    registerGattResponseListener(this);
                } else {
                    notifyRequestResult(Code.REQUEST_FAILED, null);
                }
                break;

            default:
                notifyRequestResult(Code.REQUEST_FAILED, null);
                break;
        }
    }

    @Override
    int getGattResponseListenerId() {
        return 0;
    }

    @Override
    public void onCharacteristicWrite(BluetoothGattCharacteristic characteristic, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            notifyRequestResult(Code.REQUEST_SUCCESS, null);
        } else {
            notifyRequestResult(Code.REQUEST_FAILED, null);
        }
    }
}
