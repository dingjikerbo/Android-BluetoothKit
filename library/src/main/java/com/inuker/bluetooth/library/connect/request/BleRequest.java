package com.inuker.bluetooth.library.connect.request;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;

import com.inuker.bluetooth.library.Code;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.RuntimeChecker;
import com.inuker.bluetooth.library.connect.IBleConnectDispatcher;
import com.inuker.bluetooth.library.connect.IBleConnectWorker;
import com.inuker.bluetooth.library.connect.listener.GattResponseListener;
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.BluetoothUtils;

import java.util.UUID;

public abstract class BleRequest implements IBleConnectWorker, IBleRequest, Handler.Callback, GattResponseListener, RuntimeChecker {

    protected static final int MSG_REQUEST_TIMEOUT = 0x20;

    protected BleGeneralResponse mResponse;

    protected Bundle mExtra;

    protected String mAddress;

    protected IBleConnectDispatcher mDispatcher;

    protected IBleConnectWorker mWorker;

    protected Handler mHandler, mResponseHandler;

    private RuntimeChecker mRuntimeChecker;

    private boolean mFinished;

    protected boolean mRequestTimeout;

    public BleRequest(BleGeneralResponse response) {
        mResponse = response;
        mExtra = new Bundle();
        mHandler = new Handler(Looper.myLooper(), this);
        mResponseHandler = new Handler(Looper.getMainLooper());
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

    public void setWorker(IBleConnectWorker worker) {
        mWorker = worker;
    }

    /**
     * 请求完成回调，要避免多次回调
     * @param code
     */
    public void onResponse(final int code) {
        // TODO Auto-generated method stub
        if (mFinished) {
            return;
        } else {
            mFinished = true;
        }

        mResponseHandler.post(new Runnable() {

            @Override
            public void run() {
                try {
                    if (mResponse != null) {
                        mResponse.onResponse(code, mExtra);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        return sb.toString();
    }

    public void putIntExtra(String key, int value) {
        mExtra.putInt(key, value);
    }

    public int getIntExtra(String key, int defaultValue) {
        return mExtra.getInt(key, defaultValue);
    }

    public void putByteArray(String key, byte[] bytes) {
        mExtra.putByteArray(key, bytes);
    }

    public void putParcelable(String key, Parcelable object) {
        mExtra.putParcelable(key, object);
    }

    public Bundle getExtra() {
        return mExtra;
    }

    protected String getStatusText() {
        return Constants.getStatusText(getCurrentStatus());
    }

    @Override
    public boolean readDescriptor(UUID service, UUID characteristic, UUID descriptor) {
        return mWorker.readDescriptor(service, characteristic, descriptor);
    }

    @Override
    public boolean writeDescriptor(UUID service, UUID characteristic, UUID descriptor, byte[] value) {
        return mWorker.writeDescriptor(service, characteristic, descriptor, value);
    }

    public abstract void processRequest();

    @Override
    public boolean openGatt() {
        return mWorker.openGatt();
    }

    @Override
    public boolean discoverService() {
        return mWorker.discoverService();
    }

    @Override
    public int getCurrentStatus() {
        return mWorker.getCurrentStatus();
    }

    @Override
    final public void process(IBleConnectDispatcher dispatcher) {
        checkRuntime();

        mDispatcher = dispatcher;

        BluetoothLog.w(String.format("Process %s, status = %s", getClass().getSimpleName(), getStatusText()));

        if (!BluetoothUtils.isBleSupported()) {
            onRequestCompleted(Code.BLE_NOT_SUPPORTED);
        } else if (!BluetoothUtils.isBluetoothEnabled()) {
            onRequestCompleted(Code.BLUETOOTH_DISABLED);
        } else {
            try {
                registerGattResponseListener(this);
                processRequest();
            } catch (Throwable e) {
                BluetoothLog.e(e);
                onRequestCompleted(Code.REQUEST_EXCEPTION);
            }
        }
    }

    protected void onRequestCompleted(int code) {
        checkRuntime();

        log(String.format("request complete: code = %d", code));

        mHandler.removeCallbacksAndMessages(null);
        clearGattResponseListener(this);

        onResponse(code);

        mDispatcher.onRequestCompleted(this);
    }

    @Override
    public void closeGatt() {
        log(String.format("close gatt"));
        mWorker.closeGatt();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_REQUEST_TIMEOUT:
                mRequestTimeout = true;
                closeGatt();
                break;
        }
        return true;
    }

    @Override
    public void registerGattResponseListener(GattResponseListener listener) {
        mWorker.registerGattResponseListener(listener);
    }

    @Override
    public void clearGattResponseListener(GattResponseListener listener) {
        mWorker.clearGattResponseListener(listener);
    }

    @Override
    public boolean refreshDeviceCache() {
        return mWorker.refreshDeviceCache();
    }

    @Override
    public boolean readCharacteristic(UUID service, UUID characteristic) {
        return mWorker.readCharacteristic(service, characteristic);
    }

    @Override
    public boolean writeCharacteristic(UUID service, UUID character, byte[] value) {
        return mWorker.writeCharacteristic(service, character, value);
    }

    @Override
    public boolean writeCharacteristicWithNoRsp(UUID service, UUID character, byte[] value) {
        return mWorker.writeCharacteristicWithNoRsp(service, character, value);
    }

    @Override
    public boolean setCharacteristicNotification(UUID service, UUID character, boolean enable) {
        return mWorker.setCharacteristicNotification(service, character, enable);
    }

    @Override
    public boolean setCharacteristicIndication(UUID service, UUID character, boolean enable) {
        return mWorker.setCharacteristicIndication(service, character, enable);
    }

    @Override
    public boolean readRemoteRssi() {
        return mWorker.readRemoteRssi();
    }

    @Override
    public boolean requestMtu(int mtu) {
        return mWorker.requestMtu(mtu);
    }

    protected void log(String msg) {
        BluetoothLog.v(String.format("%s %s >>> %s", getClass().getSimpleName(), getAddress(), msg));
    }

    public void setRuntimeChecker(RuntimeChecker checker) {
        mRuntimeChecker = checker;
    }

    @Override
    public void checkRuntime() {
        mRuntimeChecker.checkRuntime();
    }

    @Override
    public void cancel() {
        checkRuntime();

        log(String.format("request canceled"));

        mHandler.removeCallbacksAndMessages(null);
        clearGattResponseListener(this);

        onResponse(Code.REQUEST_CANCELED);
    }

    protected long getTimeoutInMillis() {
        return 30000;
    }

    @Override
    public void onConnectStatusChanged(boolean connectedOrDisconnected) {
        if (!connectedOrDisconnected) {
            onRequestCompleted(mRequestTimeout ? Code.REQUEST_TIMEDOUT : Code.REQUEST_FAILED);
        }
    }

    protected void startRequestTiming() {
        mHandler.sendEmptyMessageDelayed(MSG_REQUEST_TIMEOUT, getTimeoutInMillis());
    }

    protected void stopRequestTiming() {
        mHandler.removeMessages(MSG_REQUEST_TIMEOUT);
    }

    @Override
    public BleGattProfile getGattProfile() {
        return mWorker.getGattProfile();
    }
}

