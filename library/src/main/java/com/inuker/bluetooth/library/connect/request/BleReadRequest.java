package com.inuker.bluetooth.library.connect.request;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import com.inuker.bluetooth.library.BluetoothConstants;
import com.inuker.bluetooth.library.connect.IBleRequestProcessor;
import com.inuker.bluetooth.library.connect.gatt.ReadCharacterListener;
import com.inuker.bluetooth.library.connect.response.BluetoothResponse;
import com.inuker.bluetooth.library.utils.ByteUtils;

import java.util.UUID;


public class BleReadRequest extends BleRequest implements ReadCharacterListener {

    public BleReadRequest(UUID service, UUID character, BluetoothResponse response) {
        super(response);
        mServiceUUID = service;
        mCharacterUUID = character;
    }

    @Override
    public void process(IBleRequestProcessor processor) {
        super.process(processor);

        switch (getConnectStatus()) {
            case STATUS_DEVICE_SERVICE_READY:
                if (readCharacteristic(mServiceUUID, mCharacterUUID)) {
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
    int getGattResponseListenerId() {
        return GATT_RESP_CHARACTER_READ;
    }

    @Override
    public void onCharacteristicRead(BluetoothGattCharacteristic characteristic, int status) {
        byte[] value = ByteUtils.getNonEmptyByte(characteristic.getValue());

        if (status == BluetoothGatt.GATT_SUCCESS) {
            putByteArrayExtra(EXTRA_BYTE_VALUE, value);
            notifyRequestResult(REQUEST_SUCCESS, null);
        } else {
            notifyRequestResult(REQUEST_FAILED, null);
        }
    }
}
