package com.inuker.bluetooth.library;

/**
 * Created by dingjikerbo on 2016/10/10.
 */
public class Code {

    public static final int REQUEST_SUCCESS = 0;
    public static final int REQUEST_FAILED = -1;
    public static final int REQUEST_CANCELED = -2;
    public static final int ILLEGAL_ARGUMENT = -3;
    public static final int BLE_NOT_SUPPORTED = -4;
    public static final int BLUETOOTH_DISABLED = -5;
    public static final int SERVICE_UNREADY = -6;
    public static final int REQUEST_TIMEDOUT = -7;
    public static final int REQUEST_OVERFLOW = -8;
    public static final int REQUEST_DENIED = -9;
    public static final int REQUEST_EXCEPTION = -10;
    public static final int REQUEST_UNKNOWN = -11;

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
            case SERVICE_UNREADY:
                return "SERVICE_UNREADY";
            case REQUEST_TIMEDOUT:
                return "REQUEST_TIMEDOUT";
            case REQUEST_DENIED:
                return "REQUEST_DENIED";
            default:
                return "unknown code: " + code;
        }
    }
}
