package com.inuker.bluetooth.library;

import android.content.Context;

import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleReadRssiResponse;
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.connect.listener.IBleConnectStatusListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.inuker.bluetooth.library.utils.ProxyUtils;
import com.inuker.bluetooth.library.utils.UUIDUtils;

import java.util.UUID;

/**
 * Created by dingjikerbo on 2016/9/1.
 */
public class BluetoothClient implements IBluetoothClient {

    private IBluetoothClient mClient;

    public BluetoothClient(Context context) {
        mClient = BluetoothClientImpl.getInstance(context);
    }

    @Override
    public void connect(String mac, BleConnectResponse response) {
        BluetoothLog.v(String.format("Connect %s", mac));
        mClient.connect(mac, ProxyUtils.getWeakProxy(response));
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
        BluetoothLog.v(String.format("Read %s: service = %d, character = %d", mac,
                UUIDUtils.getValue(service), UUIDUtils.getValue(character)));
        mClient.read(mac, service, character, ProxyUtils.getWeakProxy(response));
    }

    @Override
    public void write(String mac, UUID service, UUID character, byte[] value, BleWriteResponse response) {
        BluetoothLog.v(String.format("write %s: service = %d, character = %d, value = %s", mac,
                UUIDUtils.getValue(service), UUIDUtils.getValue(character), ByteUtils.byteToString(value)));
        mClient.write(mac, service, character, value, ProxyUtils.getWeakProxy(response));
    }

    @Override
    public void writeNoRsp(String mac, UUID service, UUID character, byte[] value, BleWriteResponse response) {
        BluetoothLog.v(String.format("writeNoRsp %s: service = %d, character = %d, value = %s", mac,
                UUIDUtils.getValue(service), UUIDUtils.getValue(character), ByteUtils.byteToString(value)));
        mClient.writeNoRsp(mac, service, character, value, ProxyUtils.getWeakProxy(response));
    }

    @Override
    public void notify(String mac, UUID service, UUID character, BleNotifyResponse response) {
        BluetoothLog.v(String.format("notify %s: service = %d, character = %d", mac,
                UUIDUtils.getValue(service), UUIDUtils.getValue(character)));
        mClient.notify(mac, service, character, ProxyUtils.getWeakProxy(response));
    }

    @Override
    public void unnotify(String mac, UUID service, UUID character, BleUnnotifyResponse response) {
        BluetoothLog.v(String.format("unnotify %s: service = %d, character = %d", mac,
                UUIDUtils.getValue(service), UUIDUtils.getValue(character)));
        mClient.unnotify(mac, service, character, ProxyUtils.getWeakProxy(response));
    }

    @Override
    public void readRssi(String mac, BleReadRssiResponse response) {
        BluetoothLog.v(String.format("readRssi %s", mac));
        mClient.readRssi(mac, ProxyUtils.getWeakProxy(response));
    }

    @Override
    public void search(SearchRequest request, SearchResponse response) {
        mClient.search(request, ProxyUtils.getWeakProxy(response));
    }

    @Override
    public void refreshCache(String mac) {
        BluetoothLog.v(String.format("refreshCache %s", mac));
        mClient.refreshCache(mac);
    }

    @Override
    public void stopSearch() {
        mClient.stopSearch();
    }
}
