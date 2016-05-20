package com.inuker.library.connect;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.inuker.library.BluetoothConstants;
import com.inuker.library.connect.request.BleConnectRequest;
import com.inuker.library.connect.request.BleDisconnectRequest;
import com.inuker.library.connect.request.BleNotifyRequest;
import com.inuker.library.connect.request.BleReadRequest;
import com.inuker.library.connect.request.BleReadRssiRequest;
import com.inuker.library.connect.request.BleRequest;
import com.inuker.library.connect.request.BleUnnotifyRequest;
import com.inuker.library.connect.request.BleWriteRequest;
import com.inuker.library.utils.BluetoothUtils;
import com.inuker.library.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 任务调度器，任务容错，重试等调度策略
 * 告诉worker要做什么就行了，worker忠实地去完成，并通知我结果
 * 这里面所有函数运行在一个线程中，所以不用考虑多线程
 * 这里最重要的是保证任务能依次执行，不能因为某个任务异常或超时或任何其他异常而中断
 * 任务队列要设置一个上限，避免任务太多内存溢出
 * @author dingjikerbo
 */
public class BleConnectDispatcher implements IBleDispatch {

    public static final int MSG_REQUEST_SUCCESS = 0x100;
    public static final int MSG_REQUEST_FAILED = 0x110;

    private static final int MAX_REQUEST_COUNT = 100;

    private Handler mWorkerHandler;

    private List<BleRequest> mBleWorkList;
    private BleRequest mCurrentRequest;

    public static BleConnectDispatcher newInstance(String mac, IBleRunner runner) {
        return new BleConnectDispatcher(mac, runner);
    }

    private BleConnectDispatcher(String mac, IBleRunner runner) {
        mBleWorkList = new ArrayList<BleRequest>();
        BleConnectWorker.attch(mac, runner, this);
    }

    public void connect(BleResponser response) {
        addNewRequest(new BleConnectRequest(response));
    }

    public void disconnect() {
        addNewRequest(new BleDisconnectRequest());
    }

    public void read(UUID service, UUID character, BleResponser response) {
        addNewRequest(new BleReadRequest(service, character, response));
    }

    public void write(UUID service, UUID character, byte[] bytes,
                      BleResponser response) {
        addNewRequest(new BleWriteRequest(service, character, bytes, response));
    }

    public void notify(UUID service, UUID character, BleResponser response) {
        addNewRequest(new BleNotifyRequest(service, character, response));
    }

    public void unnotify(UUID service, UUID character) {
        addNewRequest(new BleUnnotifyRequest(service, character));
    }

    public void readRemoteRssi(BleResponser response) {
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

    /**
     * 向worker发送一个新任务
     *
     * @param request
     */
    private void callWorkerForNewRequest(BleRequest request) {
        mWorkerHandler.obtainMessage(BleConnectWorker.MSG_SCHEDULE_NEXT, request).sendToTarget();
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
                dispatchRequestResult(false);
            } else if (!BluetoothUtils.isBluetoothEnabled()) {
                mCurrentRequest.setRequestCode(Code.BLUETOOTH_DISABLED);
                dispatchRequestResult(false);
            } else {
                callWorkerForNewRequest(mCurrentRequest);
            }
        }
    }

    /**
     * 重试当前任务，直接插入任务头即可
     */
    private void retryCurrentRequest() {
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

    private void notifyRequestExceedLimit(BleRequest request) {
        request.setRequestCode(Code.REQUEST_OVERFLOW);
        sendMessageToResponseHandler(MSG_REQUEST_FAILED, request);
    }

    private final Handler mResponseHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            BleRequest request = null;

            if (msg != null && msg.obj instanceof BleRequest) {
                request = (BleRequest) msg.obj;
            }

            switch (msg.what) {
                case MSG_REQUEST_SUCCESS:
                    request.onResponse(Code.REQUEST_SUCCESS, request.getBundle());

                    break;

                case MSG_REQUEST_FAILED:
                    request.onResponse(request.getIntExtra(BluetoothConstants.KEY_CODE, Code.REQUEST_FAILED), null);

                    break;
            }
        }
    };

    @Override
    public void notifyWorkerResult(BleRequest request, boolean result) {
        if (request == null || request != mCurrentRequest) {
            return;
        }

        if (result == true) {
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

    /**
     * 让当前request收场回调，然后启动下一个任务
     */
    private void dispatchRequestResult(boolean result) {
        if (mCurrentRequest != null) {
            int msg = (result ?
                    MSG_REQUEST_SUCCESS : MSG_REQUEST_FAILED);
            sendMessageToResponseHandler(msg, mCurrentRequest);
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
