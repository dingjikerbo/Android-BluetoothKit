package com.dingjikerbo.bluetooth.library.connect.request;

import java.util.UUID;

/**
 * Created by liwentian on 2015/11/10.
 */
public class BleUnnotifyRequest extends BleRequest<Void> {

    public BleUnnotifyRequest(UUID service, UUID character) {
        super(null);
        mRequestType = REQUEST_TYPE_UNNOTIFY;
        mServiceUUID = service;
        mCharacterUUID = character;
    }

}
