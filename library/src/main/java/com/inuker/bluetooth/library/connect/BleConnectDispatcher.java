package com.inuker.bluetooth.library.connect;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.inuker.bluetooth.library.connect.request.BleConnectRequest;
import com.inuker.bluetooth.library.connect.request.BleDisconnectRequest;
import com.inuker.bluetooth.library.connect.request.BleNotifyRequest;
import com.inuker.bluetooth.library.connect.request.BleReadRequest;
import com.inuker.bluetooth.library.connect.request.BleReadRssiRequest;
import com.inuker.bluetooth.library.connect.request.BleRequest;
import com.inuker.bluetooth.library.connect.request.BleUnnotifyRequest;
import com.inuker.bluetooth.library.connect.request.BleWriteRequest;
import com.inuker.bluetooth.library.connect.response.BluetoothResponse;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.inuker.bluetooth.library.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BleConnectDispatcher implements IBleDispatch, IBleConnectMaster, Handler.Callback {

    public static final int MSG_REQUEST_SUCCESS = 0x100;
    public static final int MSG_REQUEST_FAILED = 0x110;

    private static final int MAX_REQUEST_COUNT = 100;

    private Handler mWorkerHandler;
    private Handler mResponseHandler;

    private List<BleRequest> mBleWorkList;
    private BleRequest mCurrentRequest;

    public static BleConnectDispatcher newInstance(String mac) {
        return new BleConnectDispatcher(mac);
    }

    private BleConnectDispatcher(String mac) {
        BleConnectWorker.attch(mac, this);
        mBleWorkList = new ArrayList<BleRequest>();
        mResponseHandler = new Handler(Looper.getMainLooper(), this);
    }

    @Override
    public void connect(BluetoothResponse response) {
        addNewRequest(new BleConnectRequest(response));
    }

    @Override
    public void disconnect() {
        addNewRequest(new BleDisconnectRequest());
    }

    @Override
    public void read(UUID service, UUID character, BluetoothResponse response) {
        addNewRequest(new BleReadRequest(service, character, response));
    }

    @Override
    public void write(UUID service, UUID character, byte[] bytes, BluetoothResponse response) {
        addNewRequest(new BleWriteRequest(service, character, bytes, response));
    }

    @Override
    public void notify(UUID service, UUID character, BluetoothResponse response) {
        addNewRequest(new BleNotifyRequest(service, character, response));
    }

    @Override
    public void unnotify(UUID service, UUID character, BluetoothResponse response) {
        addNewRequest(new BleUnnotifyRequest(service, character, response));
    }

    @Override
    public void readRssi(BluetoothResponse response) {
        addNewRequest(new BleReadRssiRequest(response));
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
        mResponseHandler.obtainMessage(0, request).sendToTarget();
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

    @Override
    public boolean handleMessage(Message msg) {
        // TODO Auto-generated method stub
        BleRequest request = null;

        if (msg != null && msg.obj instanceof BleRequest) {
            request = (BleRequest) msg.obj;
        }

        request.onResponse();

        return true;
    }

    private void dispatchRequestResult() {
        if (mCurrentRequest != null) {
            mResponseHandler.obtainMessage(0, mCurrentRequest).sendToTarget();
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
