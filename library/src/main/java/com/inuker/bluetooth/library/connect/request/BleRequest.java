package com.inuker.bluetooth.library.connect.request;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.inuker.bluetooth.library.Code;
import com.inuker.bluetooth.library.connect.gatt.GattResponseListener;
import com.inuker.bluetooth.library.connect.IBleRequestProcessor;
import com.inuker.bluetooth.library.connect.response.BleResponse;
import com.inuker.bluetooth.library.BluetoothConstants;

import java.util.UUID;

public class BleRequest implements IBleRequest, IBleRequestProcessor, Handler.Callback {

    private static final int MSG_REQUEST_TIMEOUT = 0x22;

    private static final int DEFAULT_RETRY_LIMIT = 0;
    private static final int DEFAULT_TIMEOUT_LIMIT = 10000;

    protected UUID mServiceUUID;
    protected UUID mCharacterUUID;

    protected byte[] mBytes;

    protected BleResponse mResponse;

    protected int mRetryLimit;
    protected int mRetryCount;

    protected int mTimeoutLimit;

    protected Bundle mExtra;

    private Handler mHandler;

    protected IBleRequestProcessor mProcessor;

    public BleRequest(BleResponse response) {
        mExtra = new Bundle();
        mResponse = response;
        mRetryLimit = getDefaultRetryLimit();
        mTimeoutLimit = DEFAULT_TIMEOUT_LIMIT;
        mHandler = new Handler(Looper.myLooper(), this);
    }

    public int getTimeoutLimit() {
        return mTimeoutLimit;
    }

    public void setResponse(BleResponse response) {
        mResponse = response;
    }

    public void onResponse(int code, Bundle data) {
        // TODO Auto-generated method stub
        if (mResponse != null) {
            try {
                mResponse.onResponse(code, data);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        return sb.toString();
    }

    public boolean canRetry() {
        return mRetryCount < mRetryLimit;
    }

    public Bundle getBundle() {
        return mExtra;
    }

    public void putExtra(Bundle bundle) {
        if (bundle != null) {
            mExtra.putAll(bundle);
        }
    }

    public void putByteArrayExtra(String key, byte[] bytes) {
        if (!TextUtils.isEmpty(key)) {
            Bundle bundle = new Bundle();
            bundle.putByteArray(key, bytes);
            putExtra(bundle);
        }
    }

    public void putIntExtra(String key, int value) {
        if (!TextUtils.isEmpty(key)) {
            Bundle bundle = new Bundle();
            bundle.putInt(key, value);
            putExtra(bundle);
        }
    }

    public int getIntExtra(String key, int defaultValue) {
        if (!TextUtils.isEmpty(key)) {
            Bundle bundle = getBundle();
            if (bundle != null) {
                return bundle.getInt(key, defaultValue);
            }
        }
        return defaultValue;
    }

    public void setRequestCode(int code) {
        putIntExtra(BluetoothConstants.EXTRA_CODE, code);
    }

    public void retry() {
        mRetryCount++;
    }

    protected int getDefaultRetryLimit() {
        return DEFAULT_RETRY_LIMIT;
    }

    public BleResponse getResponse() {
        return mResponse;
    }

    @Override
    public void process(IBleRequestProcessor processor) {
        mProcessor = processor;

        Message msg = mHandler.obtainMessage(MSG_REQUEST_TIMEOUT);
        mHandler.sendMessageDelayed(msg, getTimeoutLimit());
    }

    @Override
    public void registerGattResponseListener(int responseId, GattResponseListener listener) {
        mProcessor.registerGattResponseListener(responseId, listener);
    }

    protected void registerGattResponseListener(GattResponseListener listener) {
        mProcessor.registerGattResponseListener(getGattResponseListenerId(), listener);
    }

    @Override
    public void unregisterGattResponseListener(int responseId) {
        mProcessor.unregisterGattResponseListener(responseId);
    }

    @Override
    public int getConnectStatus() {
        return mProcessor.getConnectStatus();
    }

    @Override
    public void notifyRequestResult(int code, Bundle data) {
        unregisterGattResponseListener(getGattResponseListenerId());
        mProcessor.notifyRequestResult(code, data);
    }

    @Override
    public boolean openBluetoothGatt() {
        return mProcessor.openBluetoothGatt();
    }

    @Override
    public void closeBluetoothGatt() {
        mProcessor.closeBluetoothGatt();
    }

    @Override
    public boolean readCharacteristic(UUID service, UUID character) {
        return mProcessor.readCharacteristic(service, character);
    }

    int getGattResponseListenerId() {
        return 0;
    }

    @Override
    public boolean writeCharacteristic(UUID service, UUID character, byte[] value) {
        return mProcessor.writeCharacteristic(service, character, value);
    }

    @Override
    public boolean setCharacteristicNotification(UUID service, UUID character, boolean enable) {
        return mProcessor.setCharacteristicNotification(service, character, enable);
    }

    @Override
    public boolean readRemoteRssi() {
        return mProcessor.readRemoteRssi();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_REQUEST_TIMEOUT:
                notifyRequestResult(Code.REQUEST_TIMEDOUT, null);
                break;
        }
        return true;
    }
}

