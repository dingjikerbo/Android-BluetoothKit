package com.inuker.bluetooth.library.connect;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;

import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse;
import com.inuker.bluetooth.library.utils.proxy.ProxyBulk;
import com.inuker.bluetooth.library.utils.proxy.ProxyInterceptor;
import com.inuker.bluetooth.library.utils.proxy.ProxyUtils;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by dingjikerbo on 16/8/24.
 */
public class BleConnectMaster implements IBleConnectMaster, ProxyInterceptor, Callback {

    private Handler mHandler;

    private String mAddress;
    private BleConnectDispatcher mBleConnectDispatcher;

    private BleConnectMaster(String mac, Looper looper) {
        mAddress = mac;
        mHandler = new Handler(looper, this);
    }

    // Runs in worker thread
    private BleConnectDispatcher getConnectDispatcher() {
        if (mBleConnectDispatcher == null) {
            mBleConnectDispatcher = BleConnectDispatcher.newInstance(mAddress);
        }
        return mBleConnectDispatcher;
    }

    static IBleConnectMaster newInstance(String mac, Looper looper) {
        BleConnectMaster master = new BleConnectMaster(mac, looper);
        return ProxyUtils.getProxy(master, IBleConnectMaster.class, master);
    }

    @Override
    public void connect(BleConnectOptions options, BleGeneralResponse response) {
        getConnectDispatcher().connect(options, response);
    }

    @Override
    public void disconnect() {
        getConnectDispatcher().disconnect();
    }

    @Override
    public void read(UUID service, UUID character, BleGeneralResponse response) {
        getConnectDispatcher().read(service, character, response);
    }

    @Override
    public void write(UUID service, UUID character, byte[] bytes, BleGeneralResponse response) {
        getConnectDispatcher().write(service, character, bytes, response);
    }

    @Override
    public void writeNoRsp(UUID service, UUID character, byte[] bytes, BleGeneralResponse response) {
        getConnectDispatcher().writeNoRsp(service, character, bytes, response);
    }

    @Override
    public void readDescriptor(UUID service, UUID character, UUID descriptor, BleGeneralResponse response) {
        getConnectDispatcher().readDescriptor(service, character, descriptor, response);
    }

    @Override
    public void writeDescriptor(UUID service, UUID character, UUID descriptor, byte[] value, BleGeneralResponse response) {
        getConnectDispatcher().writeDescriptor(service, character, descriptor, value, response);
    }

    @Override
    public void notify(UUID service, UUID character, BleGeneralResponse response) {
        getConnectDispatcher().notify(service, character, response);
    }

    @Override
    public void unnotify(UUID service, UUID character, BleGeneralResponse response) {
        getConnectDispatcher().unnotify(service, character, response);
    }

    @Override
    public void readRssi(BleGeneralResponse response) {
        getConnectDispatcher().readRemoteRssi(response);
    }

    @Override
    public void indicate(UUID service, UUID character, BleGeneralResponse response) {
        getConnectDispatcher().indicate(service, character, response);
    }

    @Override
    public void clearRequest(int clearType) {
        getConnectDispatcher().clearRequest(clearType);
    }

    @Override
    public boolean onIntercept(Object object, Method method, Object[] args) {
        mHandler.obtainMessage(0, new ProxyBulk(object, method, args)).sendToTarget();
        return true;
    }

    @Override
    public boolean handleMessage(Message msg) {
        ProxyBulk.safeInvoke(msg.obj);
        return true;
    }
}
