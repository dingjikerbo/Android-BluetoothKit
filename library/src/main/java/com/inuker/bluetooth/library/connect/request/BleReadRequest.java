package com.inuker.bluetooth.library.connect.request;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import com.inuker.bluetooth.library.connect.listener.ReadCharacterListener;
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse;
import com.inuker.bluetooth.library.connect.response.BluetoothResponse;

import java.util.UUID;


public class BleReadRequest extends BleRequest implements ReadCharacterListener {

    public BleReadRequest(String mac, UUID service, UUID character, BleGeneralResponse response) {
        super(mac, response);
        mServiceUUID = service;
        mCharacterUUID = character;
    }

    @Override
    void processRequest() {
        switch (getConnectStatus()) {
            case STATUS_DEVICE_SERVICE_READY:
                if (readCharacteristic(mServiceUUID, mCharacterUUID)) {
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
    int getGattResponseListenerId() {
        return GATT_RESP_CHARACTER_READ;
    }

    @Override
    public void onCharacteristicRead(BluetoothGattCharacteristic characteristic, int status, byte[] value) {
        if (!checkCharacteristic(characteristic)) {
            return;
        }

        if (status == BluetoothGatt.GATT_SUCCESS) {
            putByteArrayExtra(EXTRA_BYTE_VALUE, value);
            onRequestFinished(REQUEST_SUCCESS);
        } else {
            onRequestFinished(REQUEST_FAILED);
        }
    }

    private boolean checkCharacteristic(BluetoothGattCharacteristic characteristic) {
        return characteristic != null && mCharacterUUID.equals(characteristic.getUuid())
                && characteristic.getService() != null && mServiceUUID.equals(characteristic.getService().getUuid());
    }
}
