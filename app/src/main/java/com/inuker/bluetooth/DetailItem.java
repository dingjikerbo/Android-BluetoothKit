package com.inuker.bluetooth;

import java.util.UUID;

/**
 * Created by dingjikerbo on 2016/9/5.
 */
public class DetailItem {

    public static final int TYPE_SERVICE = 0;
    public static final int TYPE_CHARACTER = 1;

    public int type;

    public UUID uuid;

    public UUID service;

    public DetailItem(int type, UUID uuid, UUID service) {
        this.type = type;
        this.uuid = uuid;
        this.service = service;
    }
}
