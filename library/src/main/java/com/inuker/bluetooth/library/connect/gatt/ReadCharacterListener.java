package com.inuker.bluetooth.library.connect.gatt;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by liwentian on 2016/8/25.
 */
public interface ReadCharacterListener extends GattResponseListener {
    void onCharacteristicRead(BluetoothGattCharacteristic characteristic, int status);
}
