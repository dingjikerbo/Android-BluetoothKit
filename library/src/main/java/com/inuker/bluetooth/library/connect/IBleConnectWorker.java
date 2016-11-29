package com.inuker.bluetooth.library.connect;

import com.inuker.bluetooth.library.connect.listener.GattResponseListener;
import com.inuker.bluetooth.library.model.BleGattProfile;

import java.util.UUID;

/**
 * Created by dingjikerbo on 2016/11/16.
 */

public interface IBleConnectWorker {

    boolean openGatt();

    void closeGatt();

    boolean discoverService();

    int getCurrentStatus();

    void registerGattResponseListener(GattResponseListener listener);

    void clearGattResponseListener(GattResponseListener listener);

    boolean refreshDeviceCache();

    boolean readCharacteristic(UUID service, UUID characteristic);

    boolean writeCharacteristic(UUID service, UUID character, byte[] value);

    boolean readDescriptor(UUID service, UUID characteristic, UUID descriptor);

    boolean writeDescriptor(UUID service, UUID characteristic, UUID descriptor, byte[] value);

    boolean writeCharacteristicWithNoRsp(UUID service, UUID character, byte[] value);

    boolean setCharacteristicNotification(UUID service, UUID character, boolean enable);

    boolean setCharacteristicIndication(UUID service, UUID character, boolean enable);

    boolean readRemoteRssi();

    BleGattProfile getGattProfile();
}
