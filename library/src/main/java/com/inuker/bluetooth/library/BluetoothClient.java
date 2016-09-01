package com.inuker.bluetooth.library;

import android.content.Context;

import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleReadRssiResponse;
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResponse;
import com.inuker.bluetooth.library.utils.ProxyUtils;

import java.util.UUID;

/**
 * Created by liwentian on 2016/9/1.
 */
public class BluetoothClient implements IBluetoothClient {

    private IBluetoothClient mClient;

    public BluetoothClient(Context context) {
        mClient = BluetoothClientImpl.getInstance(context);
    }

    @Override
    public void connect(String mac, BleConnectResponse response) {
        mClient.connect(mac, ProxyUtils.getWeakProxy(response));
    }

    @Override
    public void disconnect(String mac) {
        mClient.disconnect(mac);
    }

    @Override
    public void read(String mac, UUID service, UUID character, BleReadResponse response) {
        mClient.read(mac, service, character, ProxyUtils.getWeakProxy(response));
    }

    @Override
    public void write(String mac, UUID service, UUID character, byte[] value, BleWriteResponse response) {
        mClient.write(mac, service, character, value, ProxyUtils.getWeakProxy(response));
    }

    @Override
    public void notify(String mac, UUID service, UUID character, BleNotifyResponse response) {
        mClient.notify(mac, service, character, ProxyUtils.getWeakProxy(response));
    }

    @Override
    public void unnotify(String mac, UUID service, UUID character, BleUnnotifyResponse response) {
        mClient.unnotify(mac, service, character, ProxyUtils.getWeakProxy(response));
    }

    @Override
    public void readRssi(String mac, BleReadRssiResponse response) {
        mClient.readRssi(mac, ProxyUtils.getWeakProxy(response));
    }

    @Override
    public void search(SearchRequest request, SearchResponse response) {
        mClient.search(request, ProxyUtils.getWeakProxy(response));
    }

    @Override
    public void stopSearch() {
        mClient.stopSearch();
    }
}
