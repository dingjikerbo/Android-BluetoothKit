package com.inuker.bluetooth.library.connect.request;

import com.inuker.bluetooth.library.connect.response.BluetoothResponse;

import java.util.UUID;

public class BleWriteNoRspRequest extends BleWriteRequest {

    public BleWriteNoRspRequest(String mac, UUID service, UUID character, byte[] bytes,
                                BluetoothResponse response) {
        super(mac, service, character, bytes, response);
    }

    @Override
    boolean write(UUID service, UUID character, byte[] value) {
        return writeCharacteristicWithNoRsp(service, character, value);
    }
}
