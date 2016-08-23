package com.inuker.bluetooth.library.connect.request;

import com.inuker.bluetooth.library.BleResponse;

import java.util.UUID;


public class BleReadRequest extends BleRequest {

    public BleReadRequest(UUID service, UUID character, BleResponse response) {
        super(response);
        mRequestType = REQUEST_TYPE_READ;
        mServiceUUID = service;
        mCharacterUUID = character;
    }
}
