package com.inuker.bluetooth.library.connect;

import com.inuker.bluetooth.library.connect.options.BleConnectOption;
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse;

import java.util.UUID;

/**
 * Created by dingjikerbo on 2016/8/24.
 */
public interface IBleConnectMaster {

    void connect(BleConnectOption options, BleGeneralResponse response);

    void disconnect();

    void read(UUID service, UUID character, BleGeneralResponse response);

    void write(UUID service, UUID character, byte[] bytes, BleGeneralResponse response);

    void writeNoRsp(UUID service, UUID character, byte[] bytes, BleGeneralResponse response);

    void notify(UUID service, UUID character, BleGeneralResponse response);

    void unnotify(UUID service, UUID character, BleGeneralResponse response);

    void readRssi(BleGeneralResponse response);

    void refresh();

    void indicate(UUID service, UUID character, BleGeneralResponse response);
}
