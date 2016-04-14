package com.inuker.bluetooth.library.utils;

import java.util.UUID;

/**
 * Created by liwentian on 2015/11/4.
 */
public class UUIDUtils {
    public static final String UUID_FORMAT = "0000%04x-0000-1000-8000-00805f9b34fb";

    private static final String UUID_BASE = "0000%4s-0000-1000-8000-00805f9b34fb";

    public static UUID makeUUID(int value) {
        return UUID.fromString(String.format(UUID_FORMAT, value));
    }

    public static UUID makeUUID(final String uuid) {
        return UUID.fromString(String.format(UUID_BASE, uuid));
    }
}
