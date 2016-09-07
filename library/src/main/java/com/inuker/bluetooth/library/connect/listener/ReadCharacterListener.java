package com.inuker.bluetooth.library.connect.listener;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by dingjikerbo on 2016/8/25.
 */
public interface ReadCharacterListener extends GattResponseListener {
    void onCharacteristicRead(BluetoothGattCharacteristic characteristic, int status, byte[] value);
}
