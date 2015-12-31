package com.dingjikerbo.bluetooth.library.connect.request;

import com.dingjikerbo.bluetooth.library.BluetoothManager;

public class Code {

    public static final int REQUEST_SUCCESS = BluetoothManager.Code.REQUEST_SUCCESS;
    public static final int REQUEST_FAILED = BluetoothManager.Code.REQUEST_FAILED;
    public static final int REQUEST_CANCELED = BluetoothManager.Code.REQUEST_CANCELED;
    public static final int ILLEGAL_ARGUMENT = BluetoothManager.Code.ILLEGAL_ARGUMENT;
    public static final int BLE_NOT_SUPPORTED = BluetoothManager.Code.BLE_NOT_SUPPORTED;
    public static final int BLUETOOTH_DISABLED = BluetoothManager.Code.BLUETOOTH_DISABLED;
    public static final int CONNECTION_NOT_READY = BluetoothManager.Code.CONNECTION_NOT_READY;
    public static final int REQUEST_TIMEDOUT = BluetoothManager.Code.REQUEST_TIMEDOUT;

    public static final int SEND_WIFI_INFO_FAILED = 0x10;
    public static final int WIFI_CONNECT_FAILED = 0x20;
    public static final int BLE_DISCONNECTED = 0x30;
    public static final int OPEN_NOTIFY_FAILED = 0x40;
    public static final int WAIT_NOTIFY_TIMEOUT = 0x50;

    /**
     * 安全登录
     */
    public static final int TOKEN_NOT_MATCHED = BluetoothManager.Code.TOKEN_NOT_MATCHED;

    public static String toString(int code) {
        switch (code) {
            case REQUEST_SUCCESS:
                return "REQUEST_SUCCESS";
            case REQUEST_FAILED:
                return "REQUEST_FAILED";
            case ILLEGAL_ARGUMENT:
                return "ILLEGAL_ARGUMENT";
            case BLE_NOT_SUPPORTED:
                return "BLE_NOT_SUPPORTED";
            case BLUETOOTH_DISABLED:
                return "BLUETOOTH_DISABLED";
            case CONNECTION_NOT_READY:
                return "CONNECTION_NOT_READY";
            case TOKEN_NOT_MATCHED:
                return "TOKEN_NOT_MATCHED";
            default:
                return "unknown code: " + code;
        }
    }
}
