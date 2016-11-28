package com.inuker.bluetooth.library.connect.listener;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

/**
 * Created by dingjikerbo on 2016/8/25.
 */
public interface IBluetoothGattResponse {

    void onConnectionStateChange(int status, int newState);

    void onServicesDiscovered(int status);

    void onCharacteristicRead(BluetoothGattCharacteristic characteristic, int status, byte[] value);

    void onCharacteristicWrite(BluetoothGattCharacteristic characteristic, int status, byte[] value);

    void onCharacteristicChanged(BluetoothGattCharacteristic characteristic, byte[] value);

    void onDescriptorRead(BluetoothGattDescriptor descriptor, int status, byte[] value);

    void onDescriptorWrite(BluetoothGattDescriptor descriptor, int status);

    void onReadRemoteRssi(int rssi, int status);
}
