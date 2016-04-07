package com.inuker.bluetooth.library.connect;

import android.text.TextUtils;

import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleReadRssiResponse;
import com.inuker.bluetooth.library.connect.response.BleResponseDispatcher;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;

import java.util.HashMap;
import java.util.UUID;

public class BLEConnectManager {

    private static HashMap<String, BleConnectMaster> mBleConnectWorkerMap;

    static {
        mBleConnectWorkerMap = new HashMap<String, BleConnectMaster>();
    }

    private static BleConnectMaster getBleConnectMaster(String mac) {
        BleConnectMaster master = mBleConnectWorkerMap.get(mac);
        if (master == null) {
            master = BleConnectMaster.newInstance(mac);
            mBleConnectWorkerMap.put(mac, master);
        }
        return master;
    }

    public static void connect(String mac, BleConnectResponse response) {
        if (!TextUtils.isEmpty(mac)) {
            BleConnectMaster master = getBleConnectMaster(mac);
            master.connect(BleResponseDispatcher.newInstance(response));
        }
    }

    public static void disconnect(String mac) {
        if (!TextUtils.isEmpty(mac)) {
            BleConnectMaster master = getBleConnectMaster(mac);
            master.disconnect();
        }
    }

    public static void read(String mac, UUID service, UUID character, BleReadResponse response) {
        if (!TextUtils.isEmpty(mac) && service != null && character != null) {
            BleConnectMaster master = getBleConnectMaster(mac);
            master.read(service, character, BleResponseDispatcher.newInstance(response));
        }
    }

    public static void write(String mac, UUID service, UUID character, byte[] bytes, BleWriteResponse response) {
        if (!TextUtils.isEmpty(mac) && service != null && character != null && bytes != null) {
            BleConnectMaster master = getBleConnectMaster(mac);
            master.write(service, character, bytes, BleResponseDispatcher.newInstance(response));
        }
    }

    public static void notify(String mac, UUID service, UUID character, BleNotifyResponse response) {
        if (!TextUtils.isEmpty(mac) && service != null && character != null) {
            BleConnectMaster master = getBleConnectMaster(mac);
            master.notify(service, character, BleResponseDispatcher.newInstance(response));
        }
    }

    public static void unnotify(String mac, UUID service, UUID character) {
        if (!TextUtils.isEmpty(mac) && service != null && character != null) {
            BleConnectMaster master = getBleConnectMaster(mac);
            master.unnotify(service, character);
        }
    }

    public static void readRemoteRssi(String mac, BleReadRssiResponse response) {
        if (!TextUtils.isEmpty(mac)) {
            BleConnectMaster master = getBleConnectMaster(mac);
            master.readRemoteRssi(BleResponseDispatcher.newInstance(response));
        }
    }
}

