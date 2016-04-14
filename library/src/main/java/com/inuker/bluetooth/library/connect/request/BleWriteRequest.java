package com.inuker.bluetooth.library.connect.request;

import com.inuker.bluetooth.library.XmBleResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse2;

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
        return mResponse.mResponse instanceof BleWriteResponse2;
    }
}
