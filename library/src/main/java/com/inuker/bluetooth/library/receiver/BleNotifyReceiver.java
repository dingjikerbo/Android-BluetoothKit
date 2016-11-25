package com.inuker.bluetooth.library.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

import com.inuker.bluetooth.library.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwentian on 2016/11/25.
 */

public class BleNotifyReceiver extends AbsBluetoothReceiver {

    private static final String[] ACTIONS = {
            Constants.ACTION_CHARACTER_CHANGED
    };

    private static BleNotifyReceiver sInstance;

    private BleNotifyReceiver() {
        registerReceiverActions(ACTIONS);
    }

    // 不用考虑线程问题
    public static BleNotifyReceiver getInstance() {
        if (sInstance == null) {
            sInstance = new BleNotifyReceiver();
        }
        return sInstance;
    }

    @Override
    public boolean onReceive(Context context, Intent intent) {

        return false;
    }
}
