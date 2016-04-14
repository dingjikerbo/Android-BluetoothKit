package com.inuker.bluetooth.library.utils;

import android.bluetooth.BluetoothProfile;

public class BluetoothConstants {

	public static final int DEFAULT_DURATION = 10000;
	
	public static final int SEARCH_TYPE_CLASSIC = 1;
	public static final int SEARCH_TYPE_BLE = 2;

	public static final int SUPPORTED_PROTOCOL_VERSION = 2;

	public static final String ACTION_CONNECT_STATUS_CHANGED = "com.xiaomi.smarthome.bluetooth.connect_status_changed";
	public static final String ACTION_CHARACTER_CHANGED = "com.xiaomi.smarthome.bluetooth.character_changed";
	public static final String ACTION_CHARACTER_WRITE = "com.xiaomi.smarthome.bluetooth.character_write";
	public static final String KEY_CHARACTER_WRITE_STATUS = "key_character_write_status";

	public static final String EXTRA_ACTION = "extra_action";
	public static final String EXTRA_DID = "extra_did";
	public static final String EXTRA_EVENT = "extra_event";
	public static final String EXTRA_CONDITION = "extra_condition";
	public static final String EXTRA_FROM = "extra_from";

	public static final String FROM_MATCH = "from_match";
	public static final String FROM_DEVICE_LIST = "from_device_list";
	public static final String FROM_PLUS = "from_plus";
	public static final String FROM_BIND = "from_bind";

	public static final String ACTION_SEARCH_START = "ble_search_start";
	public static final String ACTION_SEARCH_STOP = "ble_search_stop";

	public static final long DOT_CYCLE = 600L;

	public static final int MIN_SCAN_GAP = 1000;

	public static final int STATUS_CONNECTED = 0x10;
	public static final int STATUS_DISCONNECTED = 0x20;

	public static final int STATUS_DEVICE_CONNECTED = BluetoothProfile.STATE_CONNECTED;
	public static final int STATUS_DEVICE_CONNECTING = BluetoothProfile.STATE_CONNECTING;
	public static final int STATUS_DEVICE_DISCONNECTING = BluetoothProfile.STATE_DISCONNECTING;
	public static final int STATUS_DEVICE_DISCONNECTED = BluetoothProfile.STATE_DISCONNECTED;
	public static final int STATUS_DEVICE_SERVICE_READY = 0x13;

	public static final String KEY_DEVICE_ADDRESS = "key_device_address";
	public static final String KEY_CONNECT_STATUS = "key_connect_status";
	public static final String KEY_SERVICE_UUID = "key_service_uuid";
	public static final String KEY_CHARACTER_UUID = "key_character_uuid";
	public static final String KEY_CHARACTER_VALUE = "key_character_value";
	public static final String KEY_DEVICES = "devices";
	public static final String KEY_CODE = "key_code";
	public static final String KEY_BYTES = "key_bytes";
    public static final String KEY_RSSI = "key_rssi";
	public static final String KEY_TOKEN = "key_token";

	/**
	 * 固件版本
	 */
	public static final String KEY_VERSION = "key_version";

	public static final int GATT_ERROR = 133;

	public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

	public static final int MSG_CONNECT = 0x10;
	public static final int MSG_READ = 0x20;
	public static final int MSG_WRITE = 0x30;
	public static final int MSG_DISCONNECT = 0x40;
	public static final int MSG_NOTIFY = 0x50;
	public static final int MSG_UNNOTIFY = 0x60;
	public static final int MSG_RESPONSE = 0x70;
	public static final int MSG_CLOSE = 0x80;
    public static final int MSG_READ_RSSI = 0x90;

	public static final int NOTIFY_TIMEOUT = 10000;

	public static final boolean SUCCESS = true;
	public static final boolean FAILED = false;

	public static final int COMBO_PRODUCTID = 0x67;

	/**
	 * 未知绑定关系
	 */
	public static final int BIND_STYLE_UNKNOWN = 0;

	/**
	 * 强绑定关系
	 */
	public static final int BIND_STYLE_STRONG = 1;

	/**
	 * 弱绑定关系
	 */
	public static final int BIND_STYLE_WEAK = 2;


	public static final int BLE_REMOTE_UNBINDED = 0;
	public static final int BLE_REMOTE_BINDED = 1;

	public static final boolean ENABLE_BIND_TO_SERVER = true;

	/**
	 * 本地解除绑定了，只用于手环
	 */
	public static final String KEY_LOCAL_UNBOUNDED = "key.local.unbounded";

	/**
	 * 设备扫描后，弹窗提醒过
	 */
	public static final String KEY_DIALOG_ALERTED = "key.dialog.alerted";
}
