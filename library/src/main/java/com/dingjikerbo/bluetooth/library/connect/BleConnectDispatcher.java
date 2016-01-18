package com.dingjikerbo.bluetooth.library.connect;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.dingjikerbo.bluetooth.library.connect.request.BleConnectRequest;
import com.dingjikerbo.bluetooth.library.connect.request.BleDisconnectRequest;
import com.dingjikerbo.bluetooth.library.connect.request.BleNotifyRequest;
import com.dingjikerbo.bluetooth.library.connect.request.BleReadRequest;
import com.dingjikerbo.bluetooth.library.connect.request.BleReadRssiRequest;
import com.dingjikerbo.bluetooth.library.connect.request.BleRequest;
import com.dingjikerbo.bluetooth.library.connect.request.BleUnnotifyRequest;
import com.dingjikerbo.bluetooth.library.connect.request.BleWriteRequest;
import com.dingjikerbo.bluetooth.library.connect.request.Code;
import com.dingjikerbo.bluetooth.library.connect.request.IBleDispatch;
import com.dingjikerbo.bluetooth.library.connect.request.IBleRunner;
import com.dingjikerbo.bluetooth.library.response.BleConnectResponse;
import com.dingjikerbo.bluetooth.library.response.BleNotifyResponse;
import com.dingjikerbo.bluetooth.library.response.BleReadResponse;
import com.dingjikerbo.bluetooth.library.response.BleReadRssiResponse;
import com.dingjikerbo.bluetooth.library.response.BleWriteResponse;
import com.dingjikerbo.bluetooth.library.utils.BluetoothConstants;
import com.dingjikerbo.bluetooth.library.utils.BluetoothLog;
import com.dingjikerbo.bluetooth.library.utils.BluetoothUtils;
import com.dingjikerbo.bluetooth.library.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 任务调度器，任务容错，重试等调度策略
 * 告诉worker要做什么就行了，worker忠实地去完成，并通知我结果
 * 这里面所有函数运行在一个线程中，所以不用考虑多线程
 * 这里最重要的是保证任务能依次执行，不能因为某个任务异常或超时或任何其他异常而中断
 *
 * @author dingjikerbo
 */
public class BleConnectDispatcher implements IBleDispatch {

    private final Handler mResponseHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            BleRequest request = null;

            if (msg != null && msg.obj instanceof BleRequest) {
                request = (BleRequest) msg.obj;
            }

            switch (msg.what) {
                case BluetoothConstants.MSG_REQUEST_SUCCESS:
                    processRequestSuccess(request);

                    break;

                case BluetoothConstants.MSG_REQUEST_FAILED:
                    processRequestFailed(request);

                    break;
            }
        }
    };
    private Handler mWorkerHandler;
    private List<BleRequest> mBleWorkList;
    private BleRequest mCurrentRequest;

    private BleConnectDispatcher(String mac, IBleRunner runner) {
        mBleWorkList = new ArrayList<BleRequest>();
        BleConnectWorker.attch(mac, runner, this);
    }

    public static BleConnectDispatcher newInstance(String mac, IBleRunner runner) {
        return new BleConnectDispatcher(mac, runner);
    }

    public void connect(BleConnectResponse response) {
        addNewRequest(new BleConnectRequest(response));
    }

    public void disconnect() {
        addNewRequest(new BleDisconnectRequest());
    }

    public void read(UUID service, UUID character, BleReadResponse response) {
        addNewRequest(new BleReadRequest(service, character, response));
    }

    public void write(UUID service, UUID character, byte[] bytes,
                      BleWriteResponse response) {
        addNewRequest(new BleWriteRequest(service, character, bytes, response));
    }

    public void notify(UUID service, UUID character, BleNotifyResponse response) {
        addNewRequest(new BleNotifyRequest(service, character, response));
    }

    public void unnotify(UUID service, UUID character) {
        addNewRequest(new BleUnnotifyRequest(service, character));
    }

    public void readRemoteRssi(BleReadRssiResponse response) {
        addNewRequest(new BleReadRssiRequest(response));
    }

    private void addNewRequest(BleRequest request) {
        mBleWorkList.add(request);
        scheduleNextRequest();
    }

    private void addPrioRequest(BleRequest request) {
        mBleWorkList.add(0, request);
        scheduleNextRequest();
    }

    /**
     * 向worker发送一个新任务
     *
     * @param request
     */
    private void callWorkerForNewRequest(BleRequest request) {
        mWorkerHandler.obtainMessage(BluetoothConstants.MSG_SCHEDULE_NEXT,
                request).sendToTarget();
    }

    /**
     * 准备处理下一个请求
     */
    private void scheduleNextRequest() {
        if (mCurrentRequest != null) {
            return;
        }

        if (!ListUtils.isEmpty(mBleWorkList)) {
            mCurrentRequest = mBleWorkList.remove(0);

            if (!BluetoothUtils.isBleSupported()) {
                mCurrentRequest.setRequestCode(Code.BLE_NOT_SUPPORTED);
                dispatchRequestResult(BluetoothConstants.FAILED);
            } else if (!BluetoothUtils.isBluetoothEnabled()) {
                mCurrentRequest.setRequestCode(Code.BLUETOOTH_DISABLED);
                dispatchRequestResult(BluetoothConstants.FAILED);
            } else {
                callWorkerForNewRequest(mCurrentRequest);
            }
        }
    }

    /**
     * 重试当前任务，直接插入任务头即可
     */
    private void retryCurrentRequest() {
        BluetoothLog.d("retryCurrentRequest " + mCurrentRequest);

        BleRequest request = mCurrentRequest;
        mCurrentRequest.retry();
        mCurrentRequest = null;
        addPrioRequest(request);
    }

    private void sendMessageToResponseHandler(int what, Object obj) {
        sendMessageToResponseHandler(what, obj, null);
    }

    private void sendMessageToResponseHandler(int what, Object obj, Bundle data) {
        Message msg = mResponseHandler.obtainMessage(what, obj);

        if (data != null) {
            msg.setData(data);
        }

        msg.sendToTarget();
    }

    private void processRequestSuccess(BleRequest request) {
        int code = Code.REQUEST_SUCCESS;

        if (request.isReadRequest()) {
            request.onResponse(code, request.getByteArray(BluetoothConstants.KEY_BYTES));
        } else if (request.isConnectRequest()) {
            request.onResponse(code, request.getExtra());
        } else if (request.isReadRssiRequest()) {
            request.onResponse(code, request.getIntExtra(BluetoothConstants.KEY_RSSI, 0));
        } else {
            request.onResponse(code, null);
        }
    }

    private void processRequestFailed(BleRequest request) {
        request.onResponse(request.getIntExtra(BluetoothConstants.KEY_CODE, Code.REQUEST_FAILED), null);
    }

    /**
     * 让当前request收场回调，然后启动下一个任务
     *
     * @param code
     */
    private void dispatchRequestResult(boolean result) {
        if (mCurrentRequest != null) {
            int msg = (result == BluetoothConstants.SUCCESS ?
                    BluetoothConstants.MSG_REQUEST_SUCCESS : BluetoothConstants.MSG_REQUEST_FAILED);
            sendMessageToResponseHandler(msg, mCurrentRequest);
        }

        mCurrentRequest = null;
        scheduleNextRequest();
    }

    @Override
    public void notifyWorkerResult(boolean result) {
        if (result == BluetoothConstants.SUCCESS) {
            dispatchRequestResult(result);
        } else {
            if (mCurrentRequest != null) {
                if (mCurrentRequest.canRetry()) {
                    retryCurrentRequest();
                } else {
                    dispatchRequestResult(result);
                }
            } else {
                /**
                 * 此处可能因为worker出现异常了，从而催促dispatcher分发下一个任务
                 */
                dispatchRequestResult(result);
            }
        }
    }

    @Override
    public void notifyHandlerReady(Handler handler) {
        // TODO Auto-generated method stub
        mWorkerHandler = handler;
    }
}
