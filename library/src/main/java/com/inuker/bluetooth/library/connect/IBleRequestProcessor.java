package com.inuker.bluetooth.library.connect;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.os.Bundle;

import com.inuker.bluetooth.library.connect.gatt.GattResponseListener;

import java.util.UUID;

/**
 * Created by dingjikerbo on 16/8/25.
 */
public interface IBleRequestProcessor {

    UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    int STATUS_DEVICE_CONNECTED = BluetoothProfile.STATE_CONNECTED;
    int STATUS_DEVICE_DISCONNECTED = BluetoothProfile.STATE_DISCONNECTED;
    int STATUS_DEVICE_SERVICE_READY = 0x13;

    void registerGattResponseListener(int responseId, GattResponseListener listener);

    void unregisterGattResponseListener(int responseId);

    void notifyRequestResult(int code, Bundle data);

    int getConnectStatus();

    boolean openBluetoothGatt();

    void closeBluetoothGatt();

    boolean readCharacteristic(UUID service, UUID character);

    boolean writeCharacteristic(UUID service, UUID character, byte[] value);

    boolean setCharacteristicNotification(UUID service, UUID character, boolean enable);

    boolean readRssi();
}
