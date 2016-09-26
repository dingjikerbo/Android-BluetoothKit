package com.inuker.bluetooth.library.connect;

import android.os.Handler;

import com.inuker.bluetooth.library.connect.request.BleConnectRequest;
import com.inuker.bluetooth.library.connect.request.BleDisconnectRequest;
import com.inuker.bluetooth.library.connect.request.BleNotifyRequest;
import com.inuker.bluetooth.library.connect.request.BleReadRequest;
import com.inuker.bluetooth.library.connect.request.BleReadRssiRequest;
import com.inuker.bluetooth.library.connect.request.BleRefreshCacheRequest;
import com.inuker.bluetooth.library.connect.request.BleRequest;
import com.inuker.bluetooth.library.connect.request.BleUnnotifyRequest;
import com.inuker.bluetooth.library.connect.request.BleWriteNoRspRequest;
import com.inuker.bluetooth.library.connect.request.BleWriteRequest;
import com.inuker.bluetooth.library.connect.response.BluetoothResponse;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.inuker.bluetooth.library.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BleConnectDispatcher implements IBleConnectDispatcher, IBleConnectMaster {

    private static final int MAX_REQUEST_COUNT = 100;

    private Handler mWorkerHandler;

    private List<BleRequest> mBleWorkList;
    private BleRequest mCurrentRequest;

    private String mMac;

    public static BleConnectDispatcher newInstance(String mac) {
        return new BleConnectDispatcher(mac);
    }

    private BleConnectDispatcher(String mac) {
        mMac = mac;
        BleConnectWorker.attach(mac, this);
        mBleWorkList = new ArrayList<BleRequest>();
    }

    @Override
    public void connect(BluetoothResponse response) {
        addNewRequest(new BleConnectRequest(mMac, response));
    }

    @Override
    public void disconnect() {
        addNewRequest(new BleDisconnectRequest(mMac));
    }

    @Override
    public void read(UUID service, UUID character, BluetoothResponse response) {
        addNewRequest(new BleReadRequest(mMac, service, character, response));
    }

    @Override
    public void write(UUID service, UUID character, byte[] bytes, BluetoothResponse response) {
        addNewRequest(new BleWriteRequest(mMac, service, character, bytes, response));
    }

    @Override
    public void writeNoRsp(UUID service, UUID character, byte[] bytes, BluetoothResponse response) {
        addNewRequest(new BleWriteNoRspRequest(mMac, service, character, bytes, response));
    }

    @Override
    public void notify(UUID service, UUID character, BluetoothResponse response) {
        addNewRequest(new BleNotifyRequest(mMac, service, character, response));
    }

    @Override
    public void unnotify(UUID service, UUID character, BluetoothResponse response) {
        addNewRequest(new BleUnnotifyRequest(mMac, service, character, response));
    }

    @Override
    public void readRssi(BluetoothResponse response) {
        addNewRequest(new BleReadRssiRequest(mMac, response));
    }

    @Override
    public void refresh() {
        addNewRequest(new BleRefreshCacheRequest(mMac));
    }

    private void addNewRequest(BleRequest request) {
        if (!isRequestExceedLimit()) {
            mBleWorkList.add(request);
            scheduleNextRequest();
        } else {
            notifyRequestExceedLimit(request);
        }
    }

    private boolean isRequestExceedLimit() {
        return mBleWorkList.size() >= MAX_REQUEST_COUNT;
    }

    private void addPrioRequest(BleRequest request) {
        mBleWorkList.add(0, request);
        scheduleNextRequest();
    }

    private void callWorkerForNewRequest(BleRequest request) {
        mWorkerHandler.obtainMessage(BleConnectWorker.MSG_SCHEDULE_NEXT, request).sendToTarget();
    }

    private void scheduleNextRequest() {
        if (mCurrentRequest != null) {
            return;
        }

        if (!ListUtils.isEmpty(mBleWorkList)) {
            mCurrentRequest = mBleWorkList.remove(0);

            if (!BluetoothUtils.isBleSupported()) {
                mCurrentRequest.setRequestCode(BLE_NOT_SUPPORTED);
                dispatchRequestResult();
            } else if (!BluetoothUtils.isBluetoothEnabled()) {
                mCurrentRequest.setRequestCode(BLUETOOTH_DISABLED);
                dispatchRequestResult();
            } else {
                callWorkerForNewRequest(mCurrentRequest);
            }
        }
    }

    private void retryCurrentRequest() {
        BleRequest request = mCurrentRequest;
        mCurrentRequest.retry();
        mCurrentRequest = null;
        addPrioRequest(request);
    }

    private void notifyRequestExceedLimit(BleRequest request) {
        request.setRequestCode(REQUEST_OVERFLOW);
        mWorkerHandler.obtainMessage(0, request).sendToTarget();
    }

    @Override
    public void notifyWorkerResult(BleRequest request) {
        if (request == null || request != mCurrentRequest) {
            return;
        }

        if (request.isSuccess() || !mCurrentRequest.canRetry()) {
            dispatchRequestResult();
        } else {
            retryCurrentRequest();
        }
    }

    private void dispatchRequestResult() {
        if (mCurrentRequest != null) {
            mCurrentRequest.onResponse();
        }

        mCurrentRequest = null;
        scheduleNextRequest();
    }

    @Override
    public void notifyHandlerReady(Handler handler) {
        // TODO Auto-generated method stub
        mWorkerHandler = handler;
    }
}
