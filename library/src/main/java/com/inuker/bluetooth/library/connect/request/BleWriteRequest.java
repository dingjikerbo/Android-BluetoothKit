package com.inuker.bluetooth.library.connect.request;

import com.inuker.bluetooth.library.connect.response.BleResponse;

import java.util.UUID;

public class BleWriteRequest extends BleRequest {

    public BleWriteRequest(UUID service, UUID character, byte[] bytes,
                           BleResponse response) {
        super(response);
        mRequestType = REQUEST_TYPE_WRITE;
        mServiceUUID = service;
        mCharacterUUID = character;
        mBytes = bytes;
    }

    public byte[] getBytes() {
        return mBytes;
    }

}
