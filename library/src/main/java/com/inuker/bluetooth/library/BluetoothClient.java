package com.inuker.bluetooth.library;

import android.content.Context;

import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleReadRssiResponse;
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.inuker.bluetooth.library.utils.proxy.ProxyUtils;

import java.util.UUID;

/**
 * Created by dingjikerbo on 2016/9/1.
 */
public class BluetoothClient implements IBluetoothClient {

    private IBluetoothClient mClient;

    public BluetoothClient(Context context) {
        mClient = BluetoothClientImpl.getInstance(context);
    }

    public void connect(String mac, BleConnectResponse response) {
        connect(mac, null, response);
    }

    @Override
    public void connect(String mac, BleConnectOptions options, BleConnectResponse response) {
        BluetoothLog.v(String.format("Connect %s", mac));

        response = ProxyUtils.getUIProxy(response);
        mClient.connect(mac, options, response);
    }

    @Override
    public void disconnect(String mac) {
        BluetoothLog.v(String.format("Disconnect %s", mac));
        mClient.disconnect(mac);
    }

    @Override
    public void registerConnectStatusListener(String mac, BleConnectStatusListener listener) {
        mClient.registerConnectStatusListener(mac, listener);
    }

    @Override
    public void unregisterConnectStatusListener(String mac, BleConnectStatusListener listener) {
        mClient.unregisterConnectStatusListener(mac, listener);
    }

    @Override
    public void read(String mac, UUID service, UUID character, BleReadResponse response) {
        BluetoothLog.v(String.format("Read %s: service = %s, character = %s", mac, service, character));

        response = ProxyUtils.getUIProxy(response);
        mClient.read(mac, service, character, response);
    }

    @Override
    public void write(String mac, UUID service, UUID character, byte[] value, BleWriteResponse response) {
        BluetoothLog.v(String.format("write %s: service = %s, character = %s, value = %s", mac, service, character, ByteUtils.byteToString(value)));

        response = ProxyUtils.getUIProxy(response);
        mClient.write(mac, service, character, value, response);
    }

    @Override
    public void writeNoRsp(String mac, UUID service, UUID character, byte[] value, BleWriteResponse response) {
        BluetoothLog.v(String.format("writeNoRsp %s: service = %s, character = %s, value = %s", mac, service, character, ByteUtils.byteToString(value)));

        response = ProxyUtils.getUIProxy(response);
        mClient.writeNoRsp(mac, service, character, value, response);
    }

    @Override
    public void notify(String mac, UUID service, UUID character, BleNotifyResponse response) {
        BluetoothLog.v(String.format("notify %s: service = %s, character = %s", mac, service, character));

        response = ProxyUtils.getUIProxy(response);
        mClient.notify(mac, service, character, response);
    }

    @Override
    public void unnotify(String mac, UUID service, UUID character, BleUnnotifyResponse response) {
        BluetoothLog.v(String.format("unnotify %s: service = %s, character = %s", mac, service, character));

        response = ProxyUtils.getUIProxy(response);
        mClient.unnotify(mac, service, character, response);
    }

    @Override
    public void indicate(String mac, UUID service, UUID character, BleNotifyResponse response) {
        BluetoothLog.v(String.format("indicate %s: service = %s, character = %s", mac, service, character));

        response = ProxyUtils.getUIProxy(response);
        mClient.indicate(mac, service, character, response);
    }

    @Override
    public void unindicate(String mac, UUID service, UUID character, BleUnnotifyResponse response) {
        unindicate(mac, service, character, response);
    }

    @Override
    public void readRssi(String mac, BleReadRssiResponse response) {
        BluetoothLog.v(String.format("readRssi %s", mac));

        response = ProxyUtils.getUIProxy(response);
        mClient.readRssi(mac, response);
    }

    @Override
    public void search(SearchRequest request, SearchResponse response) {
        response = ProxyUtils.getUIProxy(response);
        mClient.search(request, response);
    }

    @Override
    public void stopSearch() {
        mClient.stopSearch();
    }
}
