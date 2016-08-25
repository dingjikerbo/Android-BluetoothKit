package com.inuker.bluetooth.library.connect;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.inuker.bluetooth.library.connect.response.BleResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
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
    public void connect(BleResponse response) {
        getConnectDispatcher().connect(response);
    }

    @Override
    public void disconnect() {
        getConnectDispatcher().disconnect();
    }

    @Override
    public void read(UUID service, UUID character, BleResponse response) {
        getConnectDispatcher().read(service, character, response);
    }

    @Override
    public void write(UUID service, UUID character, byte[] bytes, BleResponse response) {
        getConnectDispatcher().write(service, character, bytes, response);
    }

    @Override
    public void notify(UUID service, UUID character, BleResponse response) {
        getConnectDispatcher().notify(service, character, response);
    }

    @Override
    public void unnotify(UUID service, UUID character) {
        getConnectDispatcher().unnotify(service, character);
    }

    @Override
    public void readRssi(BleResponse response) {
        getConnectDispatcher().readRssi(response);
    }

    @Override
    public boolean onPreCalled(Object object, Method method, Object[] args) {
        mHandler.obtainMessage(0, new ProxyBulk(object, method, args)).sendToTarget();
        return false;
    }

    @Override
    public boolean handleMessage(Message msg) {
        ProxyBulk.safeInvoke(msg.obj);
        return true;
    }
}
