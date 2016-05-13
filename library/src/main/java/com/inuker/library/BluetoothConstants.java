package com.inuker.library;

import java.util.UUID;

/**
 * Created by liwentian on 2016/5/13.
 */
public class BluetoothConstants {

    public static final int DEFAULT_DURATION = 10000;

    public static final int SEARCH_TYPE_CLASSIC = 1;
    public static final int SEARCH_TYPE_BLE = 2;

    public static final int SUPPORTED_PROTOCOL_VERSION = 2;

    public static final int DEVICE_TYPE_CLASSIC = 1;
    public static final int DEVICE_TYPE_BLE = 2;

    public static final String ACTION_SEARCH_START = "ble_search_start";
    public static final String ACTION_SEARCH_STOP = "ble_search_stop";

    public static final int STATUS_CONNECTED = 0x10;
    public static final int STATUS_DISCONNECTED = 0x20;

    public static final String KEY_BYTES = "key_bytes";
    public static final String KEY_RSSI = "key_rssi";
    public static final String KEY_CODE = "key_code";
    public static final String KEY_DEVICE_ADDRESS = "key_device_address";
    public static final String KEY_CONNECT_STATUS = "key_connect_status";
    public static final String KEY_SERVICE_UUID = "key_service_uuid";
    public static final String KEY_CHARACTER_UUID = "key_character_uuid";
    public static final String KEY_CHARACTER_VALUE = "key_character_value";
    public static final String KEY_CHARACTER_WRITE_STATUS = "key_character_write_status";
    public static final String KEY_MISERVICE_CHARACTERS = "key_miservice_characters";

    public static final String ACTION_CHARACTER_WRITE = "com.xiaomi.smarthome.bluetooth.character_write";
    public static final String ACTION_CONNECT_STATUS_CHANGED = "com.xiaomi.smarthome.bluetooth.connect_status_changed";
    public static final String ACTION_CHARACTER_CHANGED = "com.xiaomi.smarthome.bluetooth.character_changed";

    public static final int GATT_ERROR = 133;

    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
}
