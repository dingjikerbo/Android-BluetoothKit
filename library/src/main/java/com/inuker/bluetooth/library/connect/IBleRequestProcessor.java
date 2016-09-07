package com.inuker.bluetooth.library.connect;

import com.inuker.bluetooth.library.IBluetoothBase;
import com.inuker.bluetooth.library.connect.gatt.GattResponseListener;
import com.inuker.bluetooth.library.model.BleGattProfile;

import java.util.UUID;

/**
 * Created by dingjikerbo on 16/8/25.
 */
public interface IBleRequestProcessor extends IBluetoothBase {

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

    boolean setCharacteristicNotification(UUID service, UUID character, boolean enable);

    boolean readRemoteRssi();

    void refreshCache();
}
