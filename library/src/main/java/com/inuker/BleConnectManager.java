package com.inuker;

import android.support.v4.util.ArrayMap;

import com.inuker.bluetooth.library.BleConnectMaster;
import com.inuker.bluetooth.library.BleResponse;
import com.inuker.bluetooth.library.IBleConnectMaster;

import java.util.UUID;

/**
 * Created by liwentian on 2016/8/24.
 */
public class BleConnectManager {

    private static ArrayMap<String, IBleConnectMaster> mBleConnectMasters;

    static {
        mBleConnectMasters = new ArrayMap<String, IBleConnectMaster>();
    }

    private static IBleConnectMaster getBleConnectMaster(String mac) {
        IBleConnectMaster master = null;

        master = mBleConnectMasters.get(mac);
        if (master == null) {
            master = BleConnectMaster.newInstance(mac);
            mBleConnectMasters.put(mac, master);
        }

        return master;
    }

    public static void connect(String mac, BleResponse response) {
        getBleConnectMaster(mac).connect(response);
    }

    public static void disconnect(String mac) {
        getBleConnectMaster(mac).disconnect();
    }

    public static void read(String mac, UUID service, UUID character, BleResponse response) {
        getBleConnectMaster(mac).read(service, character, response);
    }

    public static void write(String mac, UUID service, UUID character, byte[] value, BleResponse response) {
        getBleConnectMaster(mac).write(service, character, value, response);
    }

    public static void notify(String mac, UUID service, UUID character, BleResponse response) {
        getBleConnectMaster(mac).notify(service, character, response);
    }

    public static void unnotify(String mac, UUID service, UUID character) {
        getBleConnectMaster(mac).unnotify(service, character);
    }

    public static void readRssi(String mac, BleResponse response) {
        getBleConnectMaster(mac).readRssi(response);
    }
}
