package com.inuker.library.connect.request;

import com.inuker.library.connect.BleResponser;
import com.inuker.library.connect.response.BleWriteResponse2;

import java.util.UUID;

public class BleWriteRequest extends BleRequest {

    public BleWriteRequest(UUID service, UUID character, byte[] bytes,
                           BleResponser response) {
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
        return mResponse.mResponse instanceof BleWriteResponse2;
    }
}
