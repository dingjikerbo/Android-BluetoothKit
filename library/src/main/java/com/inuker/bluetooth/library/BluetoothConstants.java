package com.inuker.bluetooth.library;

/**
 * Created by dingjikerbo on 16/4/8.
 */
public class BluetoothConstants {

    public static final int CODE_CONNECT = 1;
    public static final int CODE_DISCONNECT = 2;
    public static final int CODE_READ = 3;
    public static final int CODE_WRITE = 4;
    public static final int CODE_NOTIFY = 5;
    public static final int CODE_UNNOTIFY = 6;
    public static final int CODE_READ_RSSI = 7;

    public static final String EXTRA_MAC = "extra.mac";
    public static final String EXTRA_SERVICE_UUID = "extra.service.uuid";
    public static final String EXTRA_CHARACTER_UUID = "extra.character.uuid";
    public static final String EXTRA_BYTE_VALUE = "extra.byte.value";
    public static final String EXTRA_CODE = "extra.code";
    public static final String EXTRA_STATUS = "extra.status";
    public static final String EXTRA_STATE = "extra.state";
    public static final String EXTRA_RSSI = "extra.rssi";

    public static final int STATUS_CONNECTED = 0x10;
    public static final int STATUS_DISCONNECTED = 0x20;

    public static final String ACTION_CONNECT_STATUS_CHANGED = "action.connect_status_changed";
    public static final String ACTION_CHARACTER_CHANGED = "action.character_changed";

    public static final int GATT_ERROR = 133;
}
