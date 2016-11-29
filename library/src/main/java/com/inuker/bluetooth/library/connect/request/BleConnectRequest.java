package com.inuker.bluetooth.library.connect.request;

import android.bluetooth.BluetoothGatt;
import android.os.Message;

import com.inuker.bluetooth.library.Code;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.connect.listener.ServiceDiscoverListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.utils.BluetoothLog;

public class BleConnectRequest extends BleRequest implements ServiceDiscoverListener {

    private static final int MSG_CONNECT = 1;
    private static final int MSG_DISCOVER_SERVICE = 2;
    private static final int MSG_CONNECT_TIMEOUT = 3;
    private static final int MSG_DISCOVER_SERVICE_TIMEOUT = 4;
    private static final int MSG_RETRY_DISCOVER_SERVICE = 5;

    private BleConnectOptions mConnectOptions;

    private int mConnectCount;

    private int mServiceDiscoverCount;

    public BleConnectRequest(BleConnectOptions options, BleGeneralResponse response) {
        super(response);
        this.mConnectOptions = options != null ? options : new BleConnectOptions.Builder().build();
    }

    @Override
    public void processRequest() {
        processConnect();
    }

    private void processConnect() {
        mHandler.removeCallbacksAndMessages(null);
        mServiceDiscoverCount = 0;

        switch (getCurrentStatus()) {
            case Constants.STATUS_DEVICE_CONNECTED:
                processDiscoverService();
                break;

            case Constants.STATUS_DEVICE_DISCONNECTED:
                if (!doOpenNewGatt()) {
                    closeGatt();
                } else {
                    mHandler.sendEmptyMessageDelayed(MSG_CONNECT_TIMEOUT, mConnectOptions.getConnectTimeout());
                }
                break;

            case Constants.STATUS_DEVICE_SERVICE_READY:
                onConnectSuccess();
                break;
        }
    }

    private boolean doOpenNewGatt() {
        mConnectCount++;
        return openGatt();
    }

    private boolean doDiscoverService() {
        mServiceDiscoverCount++;
        return discoverService();
    }

    private void retryConnectIfNeeded() {
        if (mConnectCount < mConnectOptions.getConnectRetry() + 1) {
            retryConnectLater();
        } else {
            onRequestCompleted(Code.REQUEST_FAILED);
        }
    }

    private void retryDiscoverServiceIfNeeded() {
        if (mServiceDiscoverCount < mConnectOptions.getServiceDiscoverRetry() + 1) {
            retryDiscoverServiceLater();
        } else {
            closeGatt();
        }
    }

    private void onServiceDiscoverFailed() {
        BluetoothLog.v(String.format("onServiceDiscoverFailed"));
        refreshDeviceCache();
        mHandler.sendEmptyMessage(MSG_RETRY_DISCOVER_SERVICE);
    }

    private void processDiscoverService() {
        BluetoothLog.v(String.format("processDiscoverService, status = %s", getStatusText()));

        switch (getCurrentStatus()) {
            case Constants.STATUS_DEVICE_CONNECTED:
                if (!doDiscoverService()) {
                    onServiceDiscoverFailed();
                } else {
                    mHandler.sendEmptyMessageDelayed(MSG_DISCOVER_SERVICE_TIMEOUT, mConnectOptions.getServiceDiscoverTimeout());
                }
                break;

            case Constants.STATUS_DEVICE_DISCONNECTED:
                retryConnectIfNeeded();
                break;

            case Constants.STATUS_DEVICE_SERVICE_READY:
                onConnectSuccess();
                break;
        }
    }

    private void retryConnectLater() {
        log(String.format("retry connect later"));
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendEmptyMessageDelayed(MSG_CONNECT, 1000);
    }

    private void retryDiscoverServiceLater() {
        log(String.format("retry discover service later"));
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendEmptyMessageDelayed(MSG_DISCOVER_SERVICE, 1000);
    }

    private void processConnectTimeout() {
        log(String.format("connect timeout"));
        mHandler.removeCallbacksAndMessages(null);
        closeGatt();
    }

    private void processDiscoverServiceTimeout() {
        log(String.format("service discover timeout"));
        mHandler.removeCallbacksAndMessages(null);
        closeGatt();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_CONNECT:
                processConnect();
                break;

            case MSG_DISCOVER_SERVICE:
                processDiscoverService();
                break;

            case MSG_RETRY_DISCOVER_SERVICE:
                retryDiscoverServiceIfNeeded();
                break;

            case MSG_CONNECT_TIMEOUT:
                processConnectTimeout();
                break;

            case MSG_DISCOVER_SERVICE_TIMEOUT:
                processDiscoverServiceTimeout();
                break;
        }
        return super.handleMessage(msg);
    }

    @Override
    public String toString() {
        return "BleConnectRequest{" +
                "options=" + mConnectOptions +
                '}';
    }

    @Override
    public void onConnectStatusChanged(boolean connectedOrDisconnected) {
        checkRuntime();

        mHandler.removeMessages(MSG_CONNECT_TIMEOUT);

        if (connectedOrDisconnected) {
            mHandler.sendEmptyMessageDelayed(MSG_DISCOVER_SERVICE, 300);
        } else {
            mHandler.removeCallbacksAndMessages(null);
            retryConnectIfNeeded();
        }
    }

    @Override
    public void onServicesDiscovered(int status, BleGattProfile profile) {
        checkRuntime();

        mHandler.removeMessages(MSG_DISCOVER_SERVICE_TIMEOUT);

        if (status == BluetoothGatt.GATT_SUCCESS) {
            onConnectSuccess();
        } else {
            onServiceDiscoverFailed();
        }
    }

    private void onConnectSuccess() {
        BleGattProfile profile = getGattProfile();
        if (profile != null) {
            putParcelable(Constants.EXTRA_GATT_PROFILE, profile);
        }
        onRequestCompleted(Code.REQUEST_SUCCESS);
    }
}
