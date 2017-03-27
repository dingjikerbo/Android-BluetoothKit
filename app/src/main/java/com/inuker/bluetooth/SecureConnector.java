package com.inuker.bluetooth;


import android.bluetooth.BluetoothDevice;

import com.inuker.bluetooth.library.Code;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.inuker.bluetooth.library.utils.UUIDUtils;

import java.util.UUID;

/**
 * Created by liwentian on 2017/3/27.
 */

public class SecureConnector {

    private static BluetoothDevice mDevice;

    public static void processStep1(BluetoothDevice device) {
        mDevice = device;

        ClientManager.getClient().write(device.getAddress(), UUIDUtils.makeUUID(0xFE95), UUIDUtils.makeUUID(0x10),
                ByteUtils.fromInt(0xDE85CA90), new BleWriteResponse() {
                    @Override
                    public void onResponse(int code) {
                        if (code == Code.REQUEST_SUCCESS) {
                            processStep2();
                        }
                    }
                });
    }

    public static void processStep2() {
        ClientManager.getClient().notify(mDevice.getAddress(), UUIDUtils.makeUUID(0xFE95),
                UUIDUtils.makeUUID(0x01), new BleNotifyResponse() {
                    @Override
                    public void onNotify(UUID service, UUID character, byte[] value) {

                    }

                    @Override
                    public void onResponse(int code) {

                    }
                });
    }
}
