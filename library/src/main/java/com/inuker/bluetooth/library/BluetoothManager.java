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

/**
 * Created by liwentian on 2015/10/29.
 */
public class BluetoothManager extends BaseManager {

    public static final String ACTION_CONNECT_STATUS_CHANGED = "com.dingjikerbo.bluetooth.connect_status_changed";
    public static final String ACTION_CHARACTER_CHANGED = "com.dingjikerbo.bluetooth.character_changed";
    public static final String KEY_DEVICE_ADDRESS = "key_device_address";
    public static final String KEY_CONNECT_STATUS = "key_connect_status";
    public static final String KEY_SERVICE_UUID = "key_service_uuid";
    public static final String KEY_CHARACTER_UUID = "key_character_uuid";
    public static final String KEY_CHARACTER_VALUE = "key_character_value";
    public static final String KEY_DEVICES = "devices";
    public static final int STATUS_UNKNOWN = 0x5;
    public static final int STATUS_CONNECTED = 0x10;
    public static final int STATUS_DISCONNECTED = 0x20;

    public static void init(Context context) {
        BaseManager.init(context);
    }

    public static void connect(String mac, BleConnectResponse response) {
        if (TextUtils.isEmpty(mac)) {
            return;
        }

        BLEConnectManager.connect(mac, response);
    }

    public static void disconnect(String mac) {
        BLEConnectManager.disconnect(mac);
    }

    public static void read(String mac, UUID service, UUID character, BleReadResponse response) {
        BLEConnectManager.read(mac, service, character, response);
    }

    public static void write(String mac, UUID service, UUID character, byte[] bytes, BleWriteResponse response) {
        BLEConnectManager.write(mac, service, character, bytes, response);
    }

    public static void notify(String mac, UUID service, UUID character, BleNotifyResponse response) {
        BLEConnectManager.notify(mac, service, character, response);
    }

    public static void unnotify(String mac, UUID service, UUID character) {
        BLEConnectManager.unnotify(mac, service, character);
    }

    public static void readRemoteRssi(String mac, BleReadRssiResponse response) {
        BLEConnectManager.readRemoteRssi(mac, response);
    }
}
