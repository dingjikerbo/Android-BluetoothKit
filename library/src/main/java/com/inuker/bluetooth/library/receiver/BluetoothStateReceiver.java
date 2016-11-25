package com.inuker.bluetooth.library.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

import com.inuker.bluetooth.library.utils.BluetoothLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwentian on 2016/11/25.
 */

public class BluetoothStateReceiver extends AbsBluetoothReceiver {

    private static final String[] ACTIONS = {
            BluetoothAdapter.ACTION_STATE_CHANGED,
    };

    private static BluetoothStateReceiver sInstance;

    private List<BluetoothStateChangeListener> mListeners;

    private BluetoothStateReceiver() {
        mListeners = new ArrayList<BluetoothStateChangeListener>();
        registerReceiverActions(ACTIONS);
    }

    public interface BluetoothStateChangeListener {
        void onBluetoothStateChanged(int prevState, int curState);
    }

    public void registerListener(final BluetoothStateChangeListener listener) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (listener != null && !mListeners.contains(listener)) {
                    mListeners.add(listener);
                }
            }
        });
    }

    public void unregisterListener(final BluetoothStateChangeListener listener) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (listener != null) {
                    mListeners.remove(listener);
                }
            }
        });
    }

    // 不用考虑线程问题
    public static BluetoothStateReceiver getInstance() {
        if (sInstance == null) {
            sInstance = new BluetoothStateReceiver();
        }
        return sInstance;
    }

    @Override
    public boolean onReceive(Context context, Intent intent) {
        if (!BluetoothAdapter.ACTION_STATE_CHANGED.equalsIgnoreCase(intent.getAction())) {
            return false;
        }

        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
        int previousState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, 0);

        BluetoothLog.v(String.format("state changed: %s -> %s",
                getStateString(previousState), getStateString(state)));

        onBluetoothStateChanged(previousState, state);

        if (state == BluetoothAdapter.STATE_OFF) {
            onBluetoothTurnedOff();
        } else if (state == BluetoothAdapter.STATE_ON) {
            onBluetoothTurnedOn();
        }

        return true;
    }

    private void onBluetoothStateChanged(int previousState, int state) {
        for (BluetoothStateChangeListener listener : mListeners) {
            listener.onBluetoothStateChanged(previousState, state);
        }
    }

    private void onBluetoothTurnedOff() {
        BluetoothLog.v("onBluetoothTurnedOff");
    }

    private void onBluetoothTurnedOn() {
        BluetoothLog.v("onBluetoothTurnedOn");
    }

    private String getStateString(int state) {
        switch (state) {
            case BluetoothAdapter.STATE_ON: return "state_on";
            case BluetoothAdapter.STATE_OFF: return "state_off";
            case BluetoothAdapter.STATE_TURNING_OFF: return "state_turning_off";
            case BluetoothAdapter.STATE_TURNING_ON: return "state_turning_on";
            default: return "unknown";
        }
    }
}
