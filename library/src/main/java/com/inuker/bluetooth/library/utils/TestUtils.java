package com.inuker.bluetooth.library.utils;

public class TestUtils {

    public static String getStatus(int status) {
        switch (status) {
            case BluetoothConstants.STATUS_DEVICE_DISCONNECTED:
                return "STATUS_DEVICE_DISCONNECTED";

            case BluetoothConstants.STATUS_DEVICE_CONNECTED:
                return "STATUS_DEVICE_CONNECTED";

            case BluetoothConstants.STATUS_DEVICE_CONNECTING:
                return "STATUS_DEVICE_CONNECTING";

            case BluetoothConstants.STATUS_DEVICE_DISCONNECTING:
                return "STATUS_DEVICE_DISCONNECTING";

            case BluetoothConstants.STATUS_DEVICE_SERVICE_READY:
                return "STATUS_DEVICE_SERVICE_READY";

            case BluetoothConstants.STATUS_CONNECTED:
                return "STATUS_CONNECTED";

            case BluetoothConstants.STATUS_DISCONNECTED:
                return "STATUS_DISCONNECTED";

            default:
                return "";
        }
    }
}
