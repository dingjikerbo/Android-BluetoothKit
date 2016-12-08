package com.inuker.bluetooth.library.receiver;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.inuker.bluetooth.library.BluetoothContext;
import com.inuker.bluetooth.library.receiver.listener.BluetoothReceiverListener;
import com.inuker.bluetooth.library.utils.ListUtils;

import java.util.List;

/**
 * Created by dingjikerbo on 2016/11/25.
 */

public abstract class AbsBluetoothReceiver {

    protected Context mContext;

    protected Handler mHandler;

    protected IReceiverDispatcher mDispatcher;

    protected AbsBluetoothReceiver(IReceiverDispatcher dispatcher) {
        mDispatcher = dispatcher;
        mContext = BluetoothContext.get();
        mHandler = new Handler(Looper.getMainLooper());
    }

    boolean containsAction(String action) {
        List<String> actions = getActions();
        if (!ListUtils.isEmpty(actions) && !TextUtils.isEmpty(action)) {
            return actions.contains(action);
        }
        return false;
    }

    protected List<BluetoothReceiverListener> getListeners(Class<?> clazz) {
        return mDispatcher.getListeners(clazz);
    }

    abstract List<String> getActions();

    abstract boolean onReceive(Context context, Intent intent);
}
