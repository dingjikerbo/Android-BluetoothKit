package com.inuker.bluetooth.library.utils;

import android.bluetooth.BluetoothProfile;

import com.inuker.bluetooth.library.BluetoothManager;

import java.util.UUID;

public class BluetoothConstants {

    public static final String ACTION_CONNECT_STATUS_CHANGED = BluetoothManager.ACTION_CONNECT_STATUS_CHANGED;
    public static final String ACTION_CHARACTER_CHANGED = BluetoothManager.ACTION_CHARACTER_CHANGED;

    public static final int STATUS_DEVICE_CONNECTED = BluetoothProfile.STATE_CONNECTED;
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
    public static final int MSG_READ_RSSI = 0x90;
    public static final boolean SUCCESS = true;
    public static final boolean FAILED = false;
    public static final int REQUEST_CODE_OPEN_BLUETOOTH = 1;
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
}
