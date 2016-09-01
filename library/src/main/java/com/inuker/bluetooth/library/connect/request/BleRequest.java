package com.inuker.bluetooth.library.connect.request;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.inuker.bluetooth.library.connect.BleConnectManager;
import com.inuker.bluetooth.library.connect.IBleRequestProcessor;
import com.inuker.bluetooth.library.connect.gatt.GattResponseListener;
import com.inuker.bluetooth.library.connect.response.BluetoothResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.ByteUtils;

import java.util.UUID;

public abstract class BleRequest implements IBleRequest, IBleRequestProcessor, Handler.Callback {

    private static final int MSG_REQUEST_TIMEOUT = 0x22;

    private static final int DEFAULT_RETRY_LIMIT = 0;
    private static final int DEFAULT_TIMEOUT_LIMIT = 10000;

    protected UUID mServiceUUID;
    protected UUID mCharacterUUID;

    protected byte[] mBytes;

    protected BluetoothResponse mResponse;

    protected int mRetryLimit;
    protected int mRetryCount;

    protected int mTimeoutLimit;

    protected Bundle mExtra;

    private Handler mHandler;

    protected IBleRequestProcessor mProcessor;

    public BleRequest(BluetoothResponse response) {
        mExtra = new Bundle();
        mResponse = response;
        mRetryLimit = getDefaultRetryLimit();
        mTimeoutLimit = DEFAULT_TIMEOUT_LIMIT;
        mHandler = new Handler(Looper.myLooper(), this);
    }

    int getTimeoutLimit() {
        return mTimeoutLimit;
    }

    void setResponse(BluetoothResponse response) {
        mResponse = response;
    }

    public void onResponse() {
        // TODO Auto-generated method stub
        if (mResponse != null) {
            try {
                mResponse.onResponse(getRequestCode(), mExtra);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public boolean canRetry() {
        return mRetryCount < mRetryLimit;
    }

    void putByteArrayExtra(String key, byte[] bytes) {
        if (!TextUtils.isEmpty(key)) {
            mExtra.putByteArray(key, bytes);
        }
    }

    void putIntExtra(String key, int value) {
        if (!TextUtils.isEmpty(key)) {
            mExtra.putInt(key, value);
        }
    }

    int getIntExtra(String key, int defaultValue) {
        if (!TextUtils.isEmpty(key)) {
            return mExtra.getInt(key, defaultValue);
        }
        return defaultValue;
    }

    public void setRequestCode(int code) {
        putIntExtra(EXTRA_CODE, code);
    }

    int getRequestCode() {
        return getIntExtra(EXTRA_CODE, REQUEST_FAILED);
    }

    public void retry() {
        mRetryCount++;
    }

    int getDefaultRetryLimit() {
        return DEFAULT_RETRY_LIMIT;
    }

    BluetoothResponse getResponse() {
        return mResponse;
    }

    @Override
    final public void process(IBleRequestProcessor processor) {
        mProcessor = processor;

        BluetoothLog.v(String.format("%s.process, connectStatus = %s",
                getClass().getSimpleName(), getConnectStatusText(getConnectStatus())));

        Message msg = mHandler.obtainMessage(MSG_REQUEST_TIMEOUT);
        mHandler.sendMessageDelayed(msg, getTimeoutLimit());

        try {
            processRequest();
        } catch (Throwable e) {
            BluetoothLog.e(e);
            onRequestFinished(REQUEST_EXCEPTION);
        }
    }

    abstract void processRequest();

    private String getConnectStatusText(int status) {
        switch (status) {
            case STATUS_DEVICE_CONNECTED: return "connected";
            case STATUS_DEVICE_DISCONNECTED: return "disconnected";
            case STATUS_DEVICE_SERVICE_READY: return "service ready";
            default: return String.format("unknown %d", status);
        }
    }

    @Override
    public void registerGattResponseListener(int responseId, GattResponseListener listener) {
//        BluetoothLog.v(String.format("registerGattResponseListener responseId = %d", responseId));
        mProcessor.registerGattResponseListener(responseId, listener);
    }

    protected void registerGattResponseListener(GattResponseListener listener) {
        mProcessor.registerGattResponseListener(getGattResponseListenerId(), listener);
    }

    @Override
    public void unregisterGattResponseListener(int responseId) {
//        BluetoothLog.v(String.format("unregisterGattResponseListener %d", responseId));
        mProcessor.unregisterGattResponseListener(responseId);
    }

    @Override
    public int getConnectStatus() {
        return mProcessor.getConnectStatus();
    }

    void onRequestFinished(int code) {
        BluetoothLog.v(String.format("%s.notifyRequestResult code = %d",
                getClass().getSimpleName(), code));

        setRequestCode(code);

        mHandler.removeMessages(MSG_REQUEST_TIMEOUT);

        unregisterGattResponseListener(getGattResponseListenerId());
        mProcessor.notifyRequestResult();
    }

    @Override
    public boolean openBluetoothGatt() {
        BluetoothLog.v(String.format("openBluetoothGatt"));
        return mProcessor.openBluetoothGatt();
    }

    @Override
    public void closeBluetoothGatt() {
        BluetoothLog.v(String.format("closeBluetoothGatt"));
        mProcessor.closeBluetoothGatt();
    }

    @Override
    public boolean readCharacteristic(UUID service, UUID character) {
        BluetoothLog.v(String.format("readCharacteristic service %s character %s", service, character));
        return mProcessor.readCharacteristic(service, character);
    }

    int getGattResponseListenerId() {
        return 0;
    }

    @Override
    public boolean writeCharacteristic(UUID service, UUID character, byte[] value) {
        BluetoothLog.v(String.format("writeCharacteristic service %s character %s, value = %s",
                service, character, ByteUtils.byteToString(value)));
        return mProcessor.writeCharacteristic(service, character, value);
    }

    @Override
    public boolean setCharacteristicNotification(UUID service, UUID character, boolean enable) {
        BluetoothLog.v(String.format("setCharacteristicNotification service %s character %s %b", service, character, enable));
        return mProcessor.setCharacteristicNotification(service, character, enable);
    }

    @Override
    public boolean readRemoteRssi() {
        return mProcessor.readRemoteRssi();
    }

    @Override
    public void refreshCache() {
        mProcessor.refreshCache();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_REQUEST_TIMEOUT:
                onRequestFinished(REQUEST_TIMEDOUT);
                break;
        }
        return true;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        return sb.toString();
    }

    public boolean isSuccess() {
        return getRequestCode() == REQUEST_SUCCESS;
    }

    @Override
    final public void notifyRequestResult() {
        throw new IllegalStateException("should not call this method in request");
    }
}

