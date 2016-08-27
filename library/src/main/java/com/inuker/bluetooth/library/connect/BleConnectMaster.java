package com.inuker.bluetooth.library.connect;

import android.os.Handler;
import android.os.Message;

import com.inuker.bluetooth.library.connect.response.BluetoothResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.ProxyUtils;
import com.inuker.bluetooth.library.utils.ProxyUtils.ProxyBulk;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by dingjikerbo on 16/8/24.
 */
public class BleConnectMaster implements IBleConnectMaster, ProxyUtils.ProxyHandler, Handler.Callback {

    private Handler mHandler;

    private String mAddress;
    private BleConnectDispatcher mBleConnectDispatcher;

    private BleConnectMaster(String mac) {
        mAddress = mac;
        mHandler = new Handler(BleConnectManager.getWorkerLooper(), this);
    }

    // Runs in worker thread
    private BleConnectDispatcher getConnectDispatcher() {
        if (mBleConnectDispatcher == null) {
            mBleConnectDispatcher = BleConnectDispatcher.newInstance(mAddress);
        }
        return mBleConnectDispatcher;
    }

    static IBleConnectMaster newInstance(String mac) {
        BleConnectMaster master = new BleConnectMaster(mac);
        return ProxyUtils.newProxyInstance(master, master);
    }

    @Override
    public void connect(BluetoothResponse response) {
        getConnectDispatcher().connect(response);
    }

    @Override
    public void disconnect() {
        getConnectDispatcher().disconnect();
    }

    @Override
    public void read(UUID service, UUID character, BluetoothResponse response) {
        getConnectDispatcher().read(service, character, response);
    }

    @Override
    public void write(UUID service, UUID character, byte[] bytes, BluetoothResponse response) {
        getConnectDispatcher().write(service, character, bytes, response);
    }

    @Override
    public void notify(UUID service, UUID character, BluetoothResponse response) {
        getConnectDispatcher().notify(service, character, response);
    }

    @Override
    public void unnotify(UUID service, UUID character, BluetoothResponse response) {
        getConnectDispatcher().unnotify(service, character, response);
    }

    @Override
    public void readRssi(BluetoothResponse response) {
        getConnectDispatcher().readRssi(response);
    }

    @Override
    public boolean onPreCalled(Object object, Method method, Object[] args) {
        mHandler.obtainMessage(0, new ProxyBulk(object, method, args)).sendToTarget();
        return false;
    }

    @Override
    public boolean handleMessage(Message msg) {
        ProxyBulk bulk = (ProxyBulk) msg.obj;
//        BluetoothLog.v(String.format("BleConnectMaster.%s thread = %s",
//                bulk.method.getName(), Thread.currentThread().getName()));

        ProxyBulk.safeInvoke(msg.obj);
        return true;
    }
}
