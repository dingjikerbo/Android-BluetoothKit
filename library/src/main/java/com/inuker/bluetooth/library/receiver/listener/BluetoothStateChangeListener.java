package com.inuker.bluetooth.library.receiver.listener;

import com.inuker.bluetooth.library.BluetoothClientImpl;
import com.inuker.bluetooth.library.Constants;

/**
 * Created by dingjikerbo on 2016/11/25.
 */

public abstract class BluetoothStateChangeListener extends BluetoothReceiverListener {

    protected abstract void onBluetoothStateChanged(int prevState, int curState);

    @Override
    public void onInvoke(Object... args) {
        int prevState = (int) args[0];
        int curState = (int) args[1];

        if (curState == Constants.STATE_OFF || curState == Constants.STATE_TURNING_OFF) {
            BluetoothClientImpl.getInstance(null).stopSearch();
        }

        onBluetoothStateChanged(prevState, curState);
    }

    @Override
    public String getName() {
        return BluetoothStateChangeListener.class.getSimpleName();
    }
}
