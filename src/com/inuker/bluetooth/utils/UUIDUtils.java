package com.inuker.bluetooth.utils;

import java.util.UUID;

public class UUIDUtils {

	public static final String UUID_FORMAT = "0000%04x-0000-1000-8000-00805f9b34fb";
	
	public static UUID makeUUID(long value) {
		return UUID.fromString(String.format(UUID_FORMAT, value));
	}
}
