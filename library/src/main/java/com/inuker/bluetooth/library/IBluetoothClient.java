package com.inuker.bluetooth.library;

import com.inuker.bluetooth.library.connect.IBluetoothApi;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleReadRssiResponse;
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.connect.response.ConnectStatusListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResponse;

import java.util.UUID;

/**
 * Created by dingjikerbo on 2016/8/25.
 */
public interface IBluetoothClient extends IBluetoothApi {

    void connect(String mac, BleConnectResponse response);

    void disconnect(String mac);

    void registerConnectStatusListener(String mac, ConnectStatusListener listener);

    void unregisterConnectStatusListener(String mac, ConnectStatusListener listener);

    void read(String mac, UUID service, UUID character, BleReadResponse response);

    void write(String mac, UUID service, UUID character, byte[] value, BleWriteResponse response);

    void notify(String mac, UUID service, UUID character, BleNotifyResponse response);

    void unnotify(String mac, UUID service, UUID character, BleUnnotifyResponse response);

    void readRssi(String mac, BleReadRssiResponse response);

    void search(SearchRequest request, SearchResponse response);

    void stopSearch();
}
