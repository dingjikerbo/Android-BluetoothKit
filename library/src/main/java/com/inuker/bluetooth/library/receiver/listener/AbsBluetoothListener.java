package com.inuker.bluetooth.library.receiver.listener;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by dingjikerbo on 17/1/14.
 */

public abstract class AbsBluetoothListener implements Handler.Callback {

    private static final int MSG_INVOKE = 1;
    private static final int MSG_SYNC_INVOKE = 2;

    private Handler mHandler, mSyncHandler;

    public AbsBluetoothListener() {
        if (Looper.myLooper() == null) {
            throw new IllegalStateException();
        }
        mHandler = new Handler(Looper.myLooper(), this);
        mSyncHandler = new Handler(Looper.getMainLooper(), this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        Object[] args = (Object[]) msg.obj;
        switch (msg.what) {
            case MSG_INVOKE:
                onInvoke(args);
                break;
            case MSG_SYNC_INVOKE:
                onSyncInvoke(args);
                break;
        }

        return true;
    }

    final public void invoke(Object...args) {
        mHandler.obtainMessage(MSG_INVOKE, args).sendToTarget();
    }

    final public void invokeSync(Object...args) {
        mSyncHandler.obtainMessage(MSG_SYNC_INVOKE, args).sendToTarget();
    }

    abstract public void onInvoke(Object...args);

    abstract public void onSyncInvoke(Object...args);
}
