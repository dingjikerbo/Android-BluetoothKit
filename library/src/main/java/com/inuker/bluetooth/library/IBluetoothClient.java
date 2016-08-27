package com.inuker.bluetooth.library;

import com.inuker.bluetooth.library.connect.IBluetoothApi;
import com.inuker.bluetooth.library.connect.response.BluetoothResponse;

import java.util.UUID;

/**
 * Created by liwentian on 2016/8/25.
 */
public interface IBluetoothClient extends IBluetoothApi {

    void connect(String mac, BluetoothResponse response);

    void disconnect(String mac);

    void read(String mac, UUID service, UUID character, BluetoothResponse response);

    void write(String mac, UUID service, UUID character, byte[] value, BluetoothResponse response);

    void notify(String mac, UUID service, UUID character, BluetoothResponse response);

    void unnotify(String mac, UUID service, UUID character, BluetoothResponse response);

    void readRssi(String mac, BluetoothResponse response);
}
