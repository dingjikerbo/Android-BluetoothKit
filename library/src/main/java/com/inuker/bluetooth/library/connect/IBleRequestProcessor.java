package com.inuker.bluetooth.library.connect;

import android.bluetooth.BluetoothProfile;

import com.inuker.bluetooth.library.connect.listener.GattResponseListener;
import com.inuker.bluetooth.library.model.BleGattProfile;

import java.util.UUID;

/**
 * Created by dingjikerbo on 16/8/25.
 */
public interface IBleRequestProcessor {

    int STATUS_DEVICE_CONNECTED = BluetoothProfile.STATE_CONNECTED;
    int STATUS_DEVICE_DISCONNECTED = BluetoothProfile.STATE_DISCONNECTED;
    int STATUS_DEVICE_SERVICE_READY = 0x13;

    void registerGattResponseListener(int responseId, GattResponseListener listener);

    void unregisterGattResponseListener(int responseId);

    void notifyRequestResult();

    int getConnectStatus();

    BleGattProfile getGattProfile();

    boolean openBluetoothGatt();

    void disconnect();

    void closeBluetoothGatt();

    boolean readCharacteristic(UUID service, UUID character);

    boolean writeCharacteristic(UUID service, UUID character, byte[] value);

    boolean writeCharacteristicWithNoRsp(UUID service, UUID character, byte[] value);

    boolean setCharacteristicNotification(UUID service, UUID character, boolean enable);

    boolean setCharacteristicIndication(UUID service, UUID character, boolean enable);

    boolean readRemoteRssi();

    void refreshCache();
}
