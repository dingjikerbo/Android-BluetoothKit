package com.dingjikerbo.bluetooth.library.connect.request;

import com.dingjikerbo.bluetooth.library.connect.response.BleWriteResponse;

import java.util.UUID;

public class BleWriteRequest extends BleRequest<Void> {

    public BleWriteRequest(UUID service, UUID character, byte[] bytes,
                           BleWriteResponse response) {
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
