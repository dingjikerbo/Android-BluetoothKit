package com.dingjikerbo.bluetooth.library;

import android.content.Context;
import android.text.TextUtils;

import com.dingjikerbo.bluetooth.library.connect.BLEConnectManager;
import com.dingjikerbo.bluetooth.library.connect.response.BleConnectResponse;
import com.dingjikerbo.bluetooth.library.connect.response.BleNotifyResponse;
import com.dingjikerbo.bluetooth.library.connect.response.BleReadResponse;
import com.dingjikerbo.bluetooth.library.connect.response.BleReadRssiResponse;
import com.dingjikerbo.bluetooth.library.connect.response.BleWriteResponse;
import com.dingjikerbo.bluetooth.library.utils.BluetoothUtils;

import java.util.UUID;

/**
 * Created by liwentian on 2015/10/29.
 */
public class BluetoothManager {

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
    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    public static Context getContext() {
        return mContext;
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

    public static void openBluetoothSilently() {
        BluetoothUtils.openBluetoothSilently();
    }

    public static void openBluetooth(Context context) {
        BluetoothUtils.openBluetooth(context);
    }

    public static boolean isBluetoothOpen() {
        return BluetoothUtils.isBluetoothEnabled();
    }

    public static class Code {
        public static final int REQUEST_SUCCESS = 0;
        public static final int REQUEST_FAILED = -1;
        public static final int REQUEST_CANCELED = -2;
        public static final int ILLEGAL_ARGUMENT = -3;
        public static final int BLE_NOT_SUPPORTED = -4;
        public static final int BLUETOOTH_DISABLED = -5;
        public static final int CONNECTION_NOT_READY = -6;
        public static final int REQUEST_TIMEDOUT = -7;
        public static final int TOKEN_NOT_MATCHED = -10;
    }
}
