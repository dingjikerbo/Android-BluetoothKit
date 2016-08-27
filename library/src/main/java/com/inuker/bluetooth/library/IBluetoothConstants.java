package com.inuker.bluetooth.library;

import android.bluetooth.BluetoothProfile;

import java.util.UUID;

/**
 * Created by liwentian on 2016/8/27.
 */
public interface IBluetoothConstants {

    String EXTRA_MAC = "extra.mac";
    String EXTRA_SERVICE_UUID = "extra.service.uuid";
    String EXTRA_CHARACTER_UUID = "extra.character.uuid";
    String EXTRA_BYTE_VALUE = "extra.byte.value";
    String EXTRA_CODE = "extra.code";
    String EXTRA_STATUS = "extra.status";
    String EXTRA_STATE = "extra.state";
    String EXTRA_RSSI = "extra.rssi";
    String EXTRA_VERSION = "extra.version";

    int REQUEST_SUCCESS = 0;
    int REQUEST_FAILED = -1;
    int REQUEST_CANCELED = -2;
    int ILLEGAL_ARGUMENT = -3;
    int BLE_NOT_SUPPORTED = -4;
    int BLUETOOTH_DISABLED = -5;
    int CONNECTION_NOT_READY = -6;
    int REQUEST_TIMEDOUT = -7;
    int REQUEST_OVERFLOW = -8;
    int SERVICE_EXCEPTION = -9;

    int STATUS_CONNECTED = 0x10;
    int STATUS_DISCONNECTED = 0x20;

    String ACTION_CONNECT_STATUS_CHANGED = "action.connect_status_changed";
    String ACTION_CHARACTER_CHANGED = "action.character_changed";

    int STATUS_DEVICE_CONNECTED = BluetoothProfile.STATE_CONNECTED;
    int STATUS_DEVICE_DISCONNECTED = BluetoothProfile.STATE_DISCONNECTED;
    int STATUS_DEVICE_SERVICE_READY = 0x13;

    int GATT_ERROR = 133;

    UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
}
