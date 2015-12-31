package com.dingjikerbo.bluetooth.library.connect.request;

import com.dingjikerbo.bluetooth.library.connect.response.BleReadResponse;

import java.util.UUID;


public class BleReadRequest extends BleRequest<byte[]> {

    public BleReadRequest(UUID service, UUID character, BleReadResponse response) {
        super(response);
        mRequestType = REQUEST_TYPE_READ;
        mServiceUUID = service;
        mCharacterUUID = character;
    }
}
