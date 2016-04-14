package com.inuker.bluetooth.library;

import android.content.Context;
import android.text.TextUtils;

import com.inuker.bluetooth.library.connect.BLEConnectManager;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleReadRssiResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;

import java.util.UUID;


public class BluetoothManager extends BaseManager {

    public static void initial(Context context) {
        BaseManager.setContext(context);
    }

    public static void connect(String mac, BleConnectResponse response) {
        if (TextUtils.isEmpty(mac)) {
            return;
        }

        BLEConnectManager.connect(mac, new BleResponseWrapper(response));
    }

    public static void disconnect(String mac) {
        BLEConnectManager.disconnect(mac);
    }

    public static void read(String mac, UUID service, UUID character, final BleReadResponse response) {
        BLEConnectManager.read(mac, service, character, new BleResponseWrapper(response));
    }

    public static void write(String mac, UUID service, UUID character, byte[] bytes, BleWriteResponse response) {
        BLEConnectManager.write(mac, service, character, bytes, new BleResponseWrapper(response));
    }

    public static void notify(String mac, UUID service, UUID character, BleNotifyResponse response) {
        BLEConnectManager.notify(mac, service, character, new BleResponseWrapper(response));
    }

    public static void unnotify(String mac, UUID service, UUID character) {
        BLEConnectManager.unnotify(mac, service, character);
    }

    public static void readRemoteRssi(String mac, final BleReadRssiResponse response) {
        BLEConnectManager.readRemoteRssi(mac, new BleResponseWrapper(response));
    }
}
