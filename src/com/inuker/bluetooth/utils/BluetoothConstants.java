package com.inuker.bluetooth.utils;

import java.util.UUID;

public class BluetoothConstants {

	public static final int DEFAULT_DURATION = 10000;
	
	public static final int SEARCH_TYPE_CLASSIC = 1;
	public static final int SEARCH_TYPE_BLE = 2;
	
	public static final int SUPPORTED_PROTOCOL_VERSION = 1;
	
	public static final int INUKER_UUID = 0xFE95;
	
	public static final UUID MISERVICE = UUIDUtils
			.makeUUID(BluetoothConstants.INUKER_UUID);
	
	public static final UUID CHARACTER_EVENT = UUIDUtils.makeUUID(0x10);
	public static final UUID CHARACTER_TOKEN = UUIDUtils.makeUUID(0x01);
}
