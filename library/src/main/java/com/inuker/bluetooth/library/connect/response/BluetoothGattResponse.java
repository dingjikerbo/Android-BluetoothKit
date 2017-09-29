package com.inuker.bluetooth.library.connect.response;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import com.inuker.bluetooth.library.connect.listener.IBluetoothGattResponse;

/**
 * Created by dingjikerbo on 2016/8/25.
 */
public class BluetoothGattResponse extends BluetoothGattCallback {

    private IBluetoothGattResponse response;

    public BluetoothGattResponse(IBluetoothGattResponse response) {
        this.response = response;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        response.onConnectionStateChange(status, newState);
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        response.onServicesDiscovered(status);
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        response.onCharacteristicRead(characteristic, status, characteristic.getValue());
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        response.onCharacteristicWrite(characteristic, status, characteristic.getValue());
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        response.onCharacteristicChanged(characteristic, characteristic.getValue());
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        response.onDescriptorWrite(descriptor, status);
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        response.onDescriptorRead(descriptor, status, descriptor.getValue());
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        response.onReadRemoteRssi(rssi, status);
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        response.onMtuChanged(mtu, status);
    }
}
