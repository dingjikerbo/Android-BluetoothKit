package com.inuker.bluetooth.library.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

import com.inuker.bluetooth.library.receiver.listener.BluetoothReceiverListener;
import com.inuker.bluetooth.library.receiver.listener.BluetoothStateChangeListener;
import com.inuker.bluetooth.library.utils.BluetoothLog;

import java.util.Arrays;
import java.util.List;

/**
 * Created by dingjikerbo on 2016/11/25.
 */

public class BluetoothStateReceiver extends AbsBluetoothReceiver {

    private static final String[] ACTIONS = {
            BluetoothAdapter.ACTION_STATE_CHANGED,
    };

    protected BluetoothStateReceiver(IReceiverDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    List<String> getActions() {
        return Arrays.asList(ACTIONS);
    }

    public static BluetoothStateReceiver newInstance(IReceiverDispatcher dispatcher) {
        return new BluetoothStateReceiver(dispatcher);
    }

    @Override
    public boolean onReceive(Context context, Intent intent) {
        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
        int previousState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, 0);

        BluetoothLog.v(String.format("state changed: %s -> %s",
                getStateString(previousState), getStateString(state)));

        onBluetoothStateChanged(previousState, state);
        return true;
    }

    private void onBluetoothStateChanged(int previousState, int state) {
        List<BluetoothReceiverListener> listeners = getListeners(BluetoothStateChangeListener.class);
        for (BluetoothReceiverListener listener : listeners) {
            listener.invoke(previousState, state);
        }
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
