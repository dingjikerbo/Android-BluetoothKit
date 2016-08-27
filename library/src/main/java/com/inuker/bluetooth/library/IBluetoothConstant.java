package com.inuker.bluetooth.library;

/**
 * Created by liwentian on 2016/8/27.
 */
public interface IBluetoothConstant {

    String EXTRA_MAC = "extra.mac";
    String EXTRA_SERVICE_UUID = "extra.service.uuid";
    String EXTRA_CHARACTER_UUID = "extra.character.uuid";
    String EXTRA_BYTE_VALUE = "extra.byte.value";
    String EXTRA_CODE = "extra.code";
    String EXTRA_STATUS = "extra.status";
    String EXTRA_STATE = "extra.state";
    String EXTRA_RSSI = "extra.rssi";

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
}
