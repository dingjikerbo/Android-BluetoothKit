package com.inuker.bluetooth.library.connect.listener;

/**
 * Created by dingjikerbo on 16/8/25.
 */
public interface GattResponseListener {

    int GATT_RESP_SERVICE_DISCOVER = 1;

    int GATT_RESP_CHARACTER_READ = 2;

    int GATT_RESP_CHARACTER_WRITE = 3;

    int GATT_RESP_DESCRIPTOR_WRITE = 4;

    int GATT_RESP_READ_RSSI = 5;

    int GATT_RESP_DISCONNECT = 6;

    void onConnectStatusChanged(boolean connectedOrDisconnected);
}
