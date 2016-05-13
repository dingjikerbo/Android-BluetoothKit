package com.inuker.library.connect.request;

import com.inuker.library.connect.BleResponser;

import java.util.UUID;

/**
 * Created by liwentian on 2015/11/6.
 */
public class BleNotifyRequest extends BleRequest {

    public BleNotifyRequest(UUID service, UUID character, BleResponser response) {
        super(response);
        mRequestType = REQUEST_TYPE_NOTIFY;
        mServiceUUID = service;
        mCharacterUUID = character;
    }
}
