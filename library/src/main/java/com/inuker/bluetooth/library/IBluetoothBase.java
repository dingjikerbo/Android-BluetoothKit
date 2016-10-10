package com.inuker.bluetooth.library;

import java.util.UUID;

/**
 * Created by dingjikerbo on 2016/8/27.
 */
public interface IBluetoothBase {

    String EXTRA_MAC = "extra.mac";
    String EXTRA_SERVICE_UUID = "extra.service.uuid";
    String EXTRA_CHARACTER_UUID = "extra.character.uuid";
    String EXTRA_BYTE_VALUE = "extra.byte.value";
    String EXTRA_CODE = "extra.code";
    String EXTRA_STATUS = "extra.status";
    String EXTRA_STATE = "extra.state";
    String EXTRA_RSSI = "extra.rssi";
    String EXTRA_VERSION = "extra.version";
    String EXTRA_REQUEST = "extra.request";
    String EXTRA_SEARCH_RESULT = "extra.search.result";
    String EXTRA_GATT_PROFILE = "extra.gatt.profile";
    String EXTRA_OPTIONS = "extra.options";

    /**
     * CallBluetoothApi response code
     */
    int REQUEST_SUCCESS = Code.REQUEST_SUCCESS;
    int REQUEST_FAILED = Code.REQUEST_FAILED;
    int REQUEST_CANCELED = Code.REQUEST_CANCELED;
    int ILLEGAL_ARGUMENT = Code.ILLEGAL_ARGUMENT;
    int BLE_NOT_SUPPORTED = Code.BLE_NOT_SUPPORTED;
    int BLUETOOTH_DISABLED = Code.BLUETOOTH_DISABLED;
    int SERVICE_UNREADY = Code.SERVICE_UNREADY;
    int REQUEST_TIMEDOUT = Code.REQUEST_TIMEDOUT;
    int REQUEST_OVERFLOW = Code.REQUEST_OVERFLOW;
    int REQUEST_DENIED = Code.REQUEST_DENIED;
    int REQUEST_EXCEPTION = Code.REQUEST_EXCEPTION;

    /**
     * Scan Response code
     */
    int SEARCH_START = 1;
    int SEARCH_STOP = 2;
    int SEARCH_CANCEL = 3;
    int DEVICE_FOUND = 4;

    int STATUS_CONNECTED = 0x10;
    int STATUS_DISCONNECTED = 0x20;

    String ACTION_CONNECT_STATUS_CHANGED = "action.connect_status_changed";
    String ACTION_CHARACTER_CHANGED = "action.character_changed";

    int SEARCH_TYPE_CLASSIC = 1;
    int SEARCH_TYPE_BLE = 2;

    UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
}
