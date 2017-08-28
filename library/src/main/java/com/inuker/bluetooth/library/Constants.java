package com.inuker.bluetooth.library;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;

import java.util.UUID;

/**
 * Created by dingjikerbo on 2016/10/21.
 */

public class Constants {

    public static final String EXTRA_MAC = "extra.mac";
    public static final String EXTRA_SERVICE_UUID = "extra.service.uuid";
    public static final String EXTRA_CHARACTER_UUID = "extra.character.uuid";
    public static final String EXTRA_DESCRIPTOR_UUID = "extra.descriptor.uuid";
    public static final String EXTRA_BYTE_VALUE = "extra.byte.value";
    public static final String EXTRA_CODE = "extra.code";
    public static final String EXTRA_STATUS = "extra.status";
    public static final String EXTRA_STATE = "extra.state";
    public static final String EXTRA_RSSI = "extra.rssi";
    public static final String EXTRA_VERSION = "extra.version";
    public static final String EXTRA_REQUEST = "extra.request";
    public static final String EXTRA_SEARCH_RESULT = "extra.search.result";
    public static final String EXTRA_GATT_PROFILE = "extra.gatt.profile";
    public static final String EXTRA_OPTIONS = "extra.options";
    public static final String EXTRA_TYPE = "extra.type";
    public static final String EXTRA_MTU = "extra.mtu";

    /**
     * CallBluetoothApi response code
     */
    public static final int REQUEST_SUCCESS = Code.REQUEST_SUCCESS;
    public static final int REQUEST_FAILED = Code.REQUEST_FAILED;
    public static final int REQUEST_CANCELED = Code.REQUEST_CANCELED;
    public static final int ILLEGAL_ARGUMENT = Code.ILLEGAL_ARGUMENT;
    public static final int BLE_NOT_SUPPORTED = Code.BLE_NOT_SUPPORTED;
    public static final int BLUETOOTH_DISABLED = Code.BLUETOOTH_DISABLED;
    public static final int SERVICE_UNREADY = Code.SERVICE_UNREADY;
    public static final int REQUEST_TIMEDOUT = Code.REQUEST_TIMEDOUT;
    public static final int REQUEST_OVERFLOW = Code.REQUEST_OVERFLOW;
    public static final int REQUEST_DENIED = Code.REQUEST_DENIED;
    public static final int REQUEST_EXCEPTION = Code.REQUEST_EXCEPTION;

    /**
     * Scan Response code
     */
    public static final int SEARCH_START = 1;
    public static final int SEARCH_STOP = 2;
    public static final int SEARCH_CANCEL = 3;
    public static final int DEVICE_FOUND = 4;

    public static final int STATUS_CONNECTED = 0x10;
    public static final int STATUS_DISCONNECTED = 0x20;

    public static final String ACTION_CONNECT_STATUS_CHANGED = "action.connect_status_changed";
    public static final String ACTION_CHARACTER_CHANGED = "action.character_changed";

    public static final int SEARCH_TYPE_CLASSIC = 1;
    public static final int SEARCH_TYPE_BLE = 2;

    public static final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public static final int CODE_CONNECT = 1;
    public static final int CODE_DISCONNECT = 2;
    public static final int CODE_READ = 3;
    public static final int CODE_WRITE = 4;
    public static final int CODE_WRITE_NORSP = 5;
    public static final int CODE_NOTIFY = 6;
    public static final int CODE_UNNOTIFY = 7;
    public static final int CODE_READ_RSSI = 8;
    public static final int CODE_INDICATE = 10;
    public static final int CODE_SEARCH = 11;
    public static final int CODE_STOP_SESARCH = 12;
    public static final int CODE_READ_DESCRIPTOR = 13;
    public static final int CODE_WRITE_DESCRIPTOR = 14;
    public static final int CODE_CLEAR_REQUEST = 20;
    public static final int CODE_REFRESH_CACHE = 21;
    public static final int CODE_REQUEST_MTU = 22;

    public static final int STATUS_UNKNOWN = -1;
    public static final int STATUS_DEVICE_CONNECTED = BluetoothProfile.STATE_CONNECTED;
    public static final int STATUS_DEVICE_CONNECTING = BluetoothProfile.STATE_CONNECTING;
    public static final int STATUS_DEVICE_DISCONNECTING = BluetoothProfile.STATE_DISCONNECTING;
    public static final int STATUS_DEVICE_DISCONNECTED = BluetoothProfile.STATE_DISCONNECTED;
    public static final int STATUS_DEVICE_SERVICE_READY = 0x13;

    public static final int STATE_OFF = BluetoothAdapter.STATE_OFF;
    public static final int STATE_TURNING_OFF = BluetoothAdapter.STATE_TURNING_OFF;
    public static final int STATE_ON = BluetoothAdapter.STATE_ON;
    public static final int STATE_TURNING_ON = BluetoothAdapter.STATE_TURNING_ON;

    public static String getStatusText(int status) {
        switch (status) {
            case Constants.STATUS_DEVICE_CONNECTED: return "Connected";
            case Constants.STATUS_DEVICE_CONNECTING: return "Connecting";
            case Constants.STATUS_DEVICE_DISCONNECTING: return "Disconnecting";
            case Constants.STATUS_DEVICE_DISCONNECTED: return "Disconnected";
            case Constants.STATUS_DEVICE_SERVICE_READY: return "Service Ready";
            default: return String.format("Unknown %d", status);
        }
    }

    public static final int REQUEST_READ = 0x1;
    public static final int REQUEST_WRITE = 0x2;
    public static final int REQUEST_NOTIFY = 0x4;
    public static final int REQUEST_RSSI = 0x8;

    public static final int BOND_NONE = BluetoothDevice.BOND_NONE;
    public static final int BOND_BONDING = BluetoothDevice.BOND_BONDING;
    public static final int BOND_BONDED = BluetoothDevice.BOND_BONDED;

    public static final int GATT_DEF_BLE_MTU_SIZE = 23;
    public static final int GATT_MAX_MTU_SIZE = 517;
}
