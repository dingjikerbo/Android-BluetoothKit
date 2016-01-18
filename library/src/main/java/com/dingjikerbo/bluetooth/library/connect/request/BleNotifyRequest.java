package com.dingjikerbo.bluetooth.library.connect.request;

import com.dingjikerbo.bluetooth.library.response.BleNotifyResponse;

import java.util.UUID;

/**
 * Created by liwentian on 2015/11/6.
 */
public class BleNotifyRequest extends BleRequest<Void> {

    public BleNotifyRequest(UUID service, UUID character, BleNotifyResponse response) {
        super(response);
        mRequestType = REQUEST_TYPE_NOTIFY;
        mServiceUUID = service;
        mCharacterUUID = character;
    }
}
