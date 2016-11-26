package com.inuker.bluetooth.library.receiver;

import android.content.Context;
import android.content.Intent;

import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.receiver.listener.BleConnectStatusChangeListener;
import com.inuker.bluetooth.library.receiver.listener.BluetoothReceiverListener;
import com.inuker.bluetooth.library.utils.BluetoothLog;

import java.util.Arrays;
import java.util.List;

/**
 * Created by dingjikerbo on 16/11/26.
 */

public class BleConnectStatusChangeReceiver extends AbsBluetoothReceiver {

    private static final String[] ACTIONS = {
            Constants.ACTION_CONNECT_STATUS_CHANGED
    };

    protected BleConnectStatusChangeReceiver(IReceiverDispatcher dispatcher) {
        super(dispatcher);
    }

    public static BleConnectStatusChangeReceiver newInstance(IReceiverDispatcher dispatcher) {
        return new BleConnectStatusChangeReceiver(dispatcher);
    }

    @Override
    List<String> getActions() {
        return Arrays.asList(ACTIONS);
    }

    @Override
    boolean onReceive(Context context, Intent intent) {
        String mac = intent.getStringExtra(Constants.EXTRA_MAC);
        int status = intent.getIntExtra(Constants.EXTRA_STATUS, 0);

        BluetoothLog.v(String.format("onConnectStatusChanged for %s, status = %d", mac, status));
        onConnectStatusChanged(mac, status);
        return true;
    }

    private void onConnectStatusChanged(String mac, int status) {
        List<BluetoothReceiverListener> listeners = getListeners(BleConnectStatusChangeListener.class);
        for (BluetoothReceiverListener listener : listeners) {
            ((BleConnectStatusChangeListener) listener).onConnectStatusChanged(mac, status);
        }
    }
}
