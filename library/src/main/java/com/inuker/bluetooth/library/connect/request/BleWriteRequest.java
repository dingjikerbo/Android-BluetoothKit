package com.inuker.bluetooth.library.connect.request;

import com.inuker.bluetooth.library.response.BleWriteResponse;

import java.util.UUID;

public class BleWriteRequest extends BleRequest {

    public BleWriteRequest(UUID service, UUID character, byte[] bytes,
                           XmBleResponse response) {
        super(response);
        mRequestType = REQUEST_TYPE_WRITE;
        mServiceUUID = service;
        mCharacterUUID = character;
        mBytes = bytes;
    }

    public byte[] getBytes() {
        return mBytes;
    }

    public boolean withNoResponse() {
        return mResponse.mResponse instanceof Response.BleWriteResponse2;
    }
}
