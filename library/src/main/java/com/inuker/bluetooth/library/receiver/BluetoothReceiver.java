package com.inuker.bluetooth.library.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.inuker.bluetooth.library.utils.BluetoothUtils;

import java.util.List;

/**
 * Created by liwentian on 2016/11/25.
 */

public class BluetoothReceiver extends BroadcastReceiver {

    private static final IBluetoothReceiver[] RECEIVERS = {
            BluetoothStateReceiver.getInstance(),
    };

    private static BluetoothReceiver mReceiver;

    private static Handler mHandler = new Handler(Looper.getMainLooper());

    private static IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        for (IBluetoothReceiver receiver : RECEIVERS) {
            List<String> actions = receiver.getActions();
            for (String action : actions) {
                filter.addAction(action);
            }
        }
        return filter;
    }

    public static void registerBluetoothReceiver(long delayInMillis) {
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (mReceiver == null) {
                    mReceiver = new BluetoothReceiver();
                    BluetoothUtils.registerReceiver(mReceiver, getIntentFilter());
                }
            }
        }, delayInMillis);
    }

    public static void unregisterBluetoothReceiver() {
        mHandler.removeCallbacksAndMessages(null);

        if (mReceiver != null) {
            BluetoothUtils.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent == null) {
            return;
        }

        String action = intent.getAction();

        if (TextUtils.isEmpty(action)) {
            return;
        }

        for (IBluetoothReceiver receiver : RECEIVERS) {
            if (!receiver.getActions().contains(action)) {
                continue;
            }

            if (receiver.onReceive(context, intent)) {
                return;
            }
        }
    }
}
