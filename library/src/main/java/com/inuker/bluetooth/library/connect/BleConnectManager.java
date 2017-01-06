package com.inuker.bluetooth.library.connect;

import android.os.HandlerThread;
import android.os.Looper;

import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by dingjikerbo on 2016/8/24.
 */
public class BleConnectManager {

    private static final String TAG = BleConnectManager.class.getSimpleName();

    private static HashMap<String, IBleConnectMaster> mBleConnectMasters;

    private static HandlerThread mWorkerThread;

    static {
        mBleConnectMasters = new HashMap<String, IBleConnectMaster>();
    }

    private static Looper getWorkerLooper() {
        if (mWorkerThread == null) {
            mWorkerThread = new HandlerThread(TAG);
            mWorkerThread.start();
        }
        return mWorkerThread.getLooper();
    }

    private static IBleConnectMaster getBleConnectMaster(String mac) {
        IBleConnectMaster master;

        master = mBleConnectMasters.get(mac);
        if (master == null) {
            master = BleConnectMaster.newInstance(mac, getWorkerLooper());
            mBleConnectMasters.put(mac, master);
        }

        return master;
    }

    public static void connect(String mac, BleConnectOptions options, BleGeneralResponse response) {
        getBleConnectMaster(mac).connect(options, response);
    }

    public static void disconnect(String mac) {
        getBleConnectMaster(mac).disconnect();
    }

    public static void read(String mac, UUID service, UUID character, BleGeneralResponse response) {
        getBleConnectMaster(mac).read(service, character, response);
    }

    public static void write(String mac, UUID service, UUID character, byte[] value, BleGeneralResponse response) {
        getBleConnectMaster(mac).write(service, character, value, response);
    }

    public static void writeNoRsp(String mac, UUID service, UUID character, byte[] value, BleGeneralResponse response) {
        getBleConnectMaster(mac).writeNoRsp(service, character, value, response);
    }

    public static void readDescriptor(String mac, UUID service, UUID character, UUID descriptor, BleGeneralResponse response) {
        getBleConnectMaster(mac).readDescriptor(service, character, descriptor, response);
    }

    public static void writeDescriptor(String mac, UUID service, UUID character, UUID descriptor, byte[] value, BleGeneralResponse response) {
        getBleConnectMaster(mac).writeDescriptor(service, character, descriptor, value, response);
    }

    public static void notify(String mac, UUID service, UUID character, BleGeneralResponse response) {
        getBleConnectMaster(mac).notify(service, character, response);
    }

    public static void unnotify(String mac, UUID service, UUID character, BleGeneralResponse response) {
        getBleConnectMaster(mac).unnotify(service, character, response);
    }

    public static void readRssi(String mac, BleGeneralResponse response) {
        getBleConnectMaster(mac).readRssi(response);
    }

    public static void indicate(String mac, UUID service, UUID character, BleGeneralResponse response) {
        getBleConnectMaster(mac).indicate(service, character, response);
    }

    public static void clearRequest(String mac, int type) {
        getBleConnectMaster(mac).clearRequest(type);
    }
}
