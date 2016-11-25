package com.inuker.bluetooth.library.receiver;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.inuker.bluetooth.library.BluetoothContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by liwentian on 2016/11/25.
 */

public abstract class AbsBluetoothReceiver implements IBluetoothReceiver {

    protected List<String> mReceiverActions;

    protected Context mContext;

    protected Handler mHandler;

    protected AbsBluetoothReceiver() {
        mReceiverActions = new ArrayList<String>();
        mContext = BluetoothContext.getContext();
        mHandler = new Handler(Looper.getMainLooper());
    }

    protected void registerReceiverActions(String[] actions) {
        if (mReceiverActions == null) {
            mReceiverActions = new ArrayList<String>();
        }
        if (actions != null && actions.length > 0) {
            mReceiverActions.addAll(Arrays.asList(actions));
        }
    }

    @Override
    public List<String> getActions() {
        return mReceiverActions;
    }
}
