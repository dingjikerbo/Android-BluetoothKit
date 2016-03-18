package com.inuker.bluetooth.library.utils;

import android.bluetooth.BluetoothProfile;

import com.inuker.bluetooth.library.BluetoothManager;

import java.util.UUID;

public class BluetoothConstants {

    public static final int DEFAULT_DURATION = 10000;

    public static final int SEARCH_TYPE_CLASSIC = 1;
    public static final int SEARCH_TYPE_BLE = 2;

    public static final int SUPPORTED_PROTOCOL_VERSION = 2;

    public static final String EXTRA_ACTION = "extra_action";
    public static final String EXTRA_DID = "extra_did";
    public static final String EXTRA_EVENT = "extra_event";
    public static final String EXTRA_CONDITION = "extra_condition";
    public static final String EXTRA_FROM = "extra_from";

    public static final String FROM_MATCH = "from_match";
    public static final String FROM_DEVICE_LIST = "from_device_list";
    public static final String FROM_PLUS = "from_plus";
    public static final String FROM_BIND = "from_bind";

    public static final String ACTION_SEARCH_START = "ble_search_start";
    public static final String ACTION_SEARCH_STOP = "ble_search_stop";

    public static final long DOT_CYCLE = 600L;

    public static final int MIN_SCAN_GAP = 1000;

    public static final String[] DOTS = {
            "", ".", "..", "..."
    };

    public static final int INUKER_UUID = 0xFE95;

    public static final UUID MISERVICE = UUIDUtils
            .makeUUID(BluetoothConstants.INUKER_UUID);

    /**
     * 蓝牙安全连接token通知
     */
    public static final UUID CHARACTER_TOKEN = UUIDUtils.makeUUID(0x01);

    /**
     * 蓝牙快连时发送wifi ssid
     */
    public static final UUID CHARACTER_WIFIAPSSID = UUIDUtils.makeUUID(0x5);

    /**
     * 蓝牙快连时发送wifi密码
     */
    public static final UUID CHARACTER_WIFIAPPW = UUIDUtils.makeUUID(0x6);

    /**
     * 蓝牙安全连接时事件通知
     */
    public static final UUID CHARACTER_EVENT = UUIDUtils.makeUUID(0x10);

    /**
     * 蓝牙快连时发送用户账号ID
     */
    public static final UUID CHARACTER_WIFIUID = UUIDUtils.makeUUID(0x11);

    /**
     * 蓝牙快连时设备状态通知
     */
    public static final UUID CHARACTER_WIFISTATUS = UUIDUtils.makeUUID(0x12);

    public static final String ACTION_CONNECT_STATUS_CHANGED = BluetoothManager.ACTION_CONNECT_STATUS_CHANGED;
    public static final String ACTION_CHARACTER_CHANGED = BluetoothManager.ACTION_CHARACTER_CHANGED;

    public static final int STATUS_DEVICE_CONNECTED = BluetoothProfile.STATE_CONNECTED;
    public static final int STATUS_DEVICE_CONNECTING = BluetoothProfile.STATE_CONNECTING;
    public static final int STATUS_DEVICE_DISCONNECTING = BluetoothProfile.STATE_DISCONNECTING;
    public static final int STATUS_DEVICE_DISCONNECTED = BluetoothProfile.STATE_DISCONNECTED;
    public static final int STATUS_DEVICE_SERVICE_READY = 0x13;

    public static final int STATUS_UNKNOWN = BluetoothManager.STATUS_UNKNOWN;
    public static final int STATUS_CONNECTED = BluetoothManager.STATUS_CONNECTED;
    public static final int STATUS_DISCONNECTED = BluetoothManager.STATUS_DISCONNECTED;

    public static final String KEY_DEVICE_ADDRESS = BluetoothManager.KEY_DEVICE_ADDRESS;
    public static final String KEY_CONNECT_STATUS = BluetoothManager.KEY_CONNECT_STATUS;
    public static final String KEY_SERVICE_UUID = BluetoothManager.KEY_SERVICE_UUID;
    public static final String KEY_CHARACTER_UUID = BluetoothManager.KEY_CHARACTER_UUID;
    public static final String KEY_CHARACTER_VALUE = BluetoothManager.KEY_CHARACTER_VALUE;
    public static final String KEY_DEVICES = BluetoothManager.KEY_DEVICES;
    public static final String KEY_AP_SSID = "key_ap_ssid";
    public static final String KEY_AP_PASSWORD = "key_ap_password";
    public static final String KEY_PRODUCT_ID = "key_product_id";
    public static final String KEY_CODE = "key_code";
    public static final String KEY_BYTES = "key_bytes";
    public static final String KEY_UUIDS = "key_uuids";
    public static final String KEY_RSSI = "key_rssi";

    public static final int GATT_ERROR = 133;
    public static final int MSG_CONNECT = 0x10;
    public static final int MSG_READ = 0x20;
    public static final int MSG_WRITE = 0x30;
    public static final int MSG_DISCONNECT = 0x40;
    public static final int MSG_NOTIFY = 0x50;
    public static final int MSG_UNNOTIFY = 0x60;
    public static final int MSG_RESPONSE = 0x70;
    public static final int MSG_CLOSE = 0x80;
    public static final int MSG_READ_RSSI = 0x90;
    public static final int MSG_REQUEST_SUCCESS = 0x100;
    public static final int MSG_REQUEST_FAILED = 0x110;
    public static final int MSG_REQUEST_TIMEOUT = 0x120;
    public static final int MSG_GATT_FAILED = 0x130;
    public static final int MSG_SERVICE_READY = 0x140;
    public static final int MSG_SCHEDULE_NEXT = 0x150;
    public static final int MSG_DISCONNECTED = 0x160;
    public static final int NOTIFY_TIMEOUT = 5000;
    public static final boolean SUCCESS = true;
    public static final boolean FAILED = false;
    public static final int REQUEST_CODE_OPEN_BLUETOOTH = 1;
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
}
