package com.inuker.bluetooth.library.connect;

import android.bluetooth.BluetoothProfile;
import android.os.Bundle;

import com.inuker.bluetooth.library.IBluetoothConstants;
import com.inuker.bluetooth.library.connect.gatt.GattResponseListener;

import java.util.UUID;

/**
 * Created by dingjikerbo on 16/8/25.
 */
public interface IBleRequestProcessor extends IBluetoothConstants {

    void registerGattResponseListener(int responseId, GattResponseListener listener);

    void unregisterGattResponseListener(int responseId);

    void notifyRequestResult(int code, Bundle data);

    int getConnectStatus();

    boolean openBluetoothGatt();

    void closeBluetoothGatt();

    boolean readCharacteristic(UUID service, UUID character);

    boolean writeCharacteristic(UUID service, UUID character, byte[] value);

    boolean setCharacteristicNotification(UUID service, UUID character, boolean enable);

    boolean readRemoteRssi();
}
