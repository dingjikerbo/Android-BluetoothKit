package com.inuker.bluetooth.library.connect.request;

import com.inuker.bluetooth.library.connect.response.BleNoRespWriteResponse;
import com.inuker.bluetooth.library.connect.response.BleResponse;
import com.inuker.bluetooth.library.connect.response.BleResponseDispatcher;

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

    public boolean withNoResponse() {
        BleResponseDispatcher dispatcher = (BleResponseDispatcher) mResponse;
        return dispatcher.response instanceof BleNoRespWriteResponse;
    }
}
