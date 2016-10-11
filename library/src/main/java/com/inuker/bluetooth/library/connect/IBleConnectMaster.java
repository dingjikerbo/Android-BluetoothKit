package com.inuker.bluetooth.library.connect;

import com.inuker.bluetooth.library.IBluetoothBase;
import com.inuker.bluetooth.library.connect.options.BleConnectOption;
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse;
import com.inuker.bluetooth.library.connect.response.BluetoothResponse;

import java.util.UUID;

/**
 * Created by dingjikerbo on 2016/8/24.
 */
public interface IBleConnectMaster extends IBluetoothBase {

    void connect(BleConnectOption options, BleGeneralResponse response);

    void disconnect();

    void read(UUID service, UUID character, BleGeneralResponse response);

    void write(UUID service, UUID character, byte[] bytes, BleGeneralResponse response);

    void writeNoRsp(UUID service, UUID character, byte[] bytes, BleGeneralResponse response);

    void notify(UUID service, UUID character, BleGeneralResponse response);

    void unnotify(UUID service, UUID character, BleGeneralResponse response);

    void readRssi(BleGeneralResponse response);

    void refresh();
}
