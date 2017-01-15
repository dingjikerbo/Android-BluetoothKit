package com.inuker.bluetooth.library.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.inuker.bluetooth.library.receiver.listener.BluetoothReceiverListener;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.inuker.bluetooth.library.utils.proxy.ProxyBulk;
import com.inuker.bluetooth.library.utils.proxy.ProxyInterceptor;
import com.inuker.bluetooth.library.utils.proxy.ProxyUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by dingjikerbo on 2016/11/25.
 */

public class BluetoothReceiver extends BroadcastReceiver implements IBluetoothReceiver, Handler.Callback {

    private static final int MSG_REGISTER = 1;

    private Map<String, List<BluetoothReceiverListener>> mListeners;

    private static IBluetoothReceiver mReceiver;

    private Handler mHandler;

    private IReceiverDispatcher mDispatcher = new IReceiverDispatcher() {
        @Override
        public List<BluetoothReceiverListener> getListeners(Class<?> clazz) {
            return mListeners.get(clazz.getSimpleName());
        }
    };

    private AbsBluetoothReceiver[] RECEIVERS = {
            BluetoothStateReceiver.newInstance(mDispatcher),
            BluetoothBondReceiver.newInstance(mDispatcher),
            BleConnectStatusChangeReceiver.newInstance(mDispatcher),
            BleCharacterChangeReceiver.newInstance(mDispatcher),
    };

    public static IBluetoothReceiver getInstance() {
        if (mReceiver == null) {
            synchronized (BluetoothReceiver.class) {
                if (mReceiver == null) {
                    mReceiver = new BluetoothReceiver();
                }
            }
        }
        return mReceiver;
    }

    private BluetoothReceiver() {
        mListeners = new HashMap<String, List<BluetoothReceiverListener>>();
        mHandler = new Handler(Looper.getMainLooper(), this);
        BluetoothUtils.registerReceiver(this, getIntentFilter());
    }

    private IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        for (AbsBluetoothReceiver receiver : RECEIVERS) {
            List<String> actions = receiver.getActions();
            for (String action : actions) {
                filter.addAction(action);
            }
        }
        return filter;
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

        BluetoothLog.v(String.format("BluetoothReceiver onReceive: %s", action));

        for (AbsBluetoothReceiver receiver : RECEIVERS) {
            if (!receiver.containsAction(action)) {
                continue;
            }

            if (receiver.onReceive(context, intent)) {
                return;
            }
        }
    }

    @Override
    public void register(BluetoothReceiverListener listener) {
        mHandler.obtainMessage(MSG_REGISTER, listener).sendToTarget();
    }

    private void registerInner(BluetoothReceiverListener listener) {
        if (listener != null) {
            List<BluetoothReceiverListener> listeners = mListeners.get(listener.getName());
            if (listeners == null) {
                listeners = new LinkedList<BluetoothReceiverListener>();
                mListeners.put(listener.getName(), listeners);
            }
            listeners.add(listener);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_REGISTER:
                registerInner((BluetoothReceiverListener) msg.obj);
                break;
        }
        return true;
    }
}
