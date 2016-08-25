package com.inuker.bluetooth.library.connect.gatt;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

/**
 * Created by liwentian on 2016/8/25.
 */
public interface IBluetoothGattResponse {

    void onConnectionStateChange(BluetoothGatt gatt, int status, int newState);

    void onServicesDiscovered(BluetoothGatt gatt, int status);

    void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);

    void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);

    void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, byte[] value);

    void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status);

    void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status);
}
