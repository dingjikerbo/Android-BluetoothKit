package com.inuker.bluetooth.library.connect;

import com.inuker.bluetooth.library.connect.response.BleResponse;

import java.util.UUID;

/**
 * Created by liwentian on 2016/8/24.
 */
public interface IBleConnectMaster {

    void connect(BleResponse response);

    void disconnect();

    void read(UUID service, UUID character, BleResponse response);

    void write(UUID service, UUID character, byte[] bytes, BleResponse response);

    void notify(UUID service, UUID character, BleResponse response);

    void unnotify(UUID service, UUID character);

    void readRssi(BleResponse response);
}
