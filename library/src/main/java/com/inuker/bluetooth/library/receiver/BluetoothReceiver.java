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
 * Created by liwentian on 2016/11/25.
 */

public class BluetoothReceiver extends BroadcastReceiver implements IBluetoothReceiver, Handler.Callback, ProxyInterceptor {

    private static final int MSG_INVOKE_PROXY = 1;

    private IReceiverDispatcher mDispatcher = new IReceiverDispatcher() {
        @Override
        public List<BluetoothReceiverListener> getListeners(Class<?> clazz) {
            return mListeners.get(clazz);
        }
    };

    private AbsBluetoothReceiver[] RECEIVERS = {
            BluetoothStateReceiver.newInstance(mDispatcher),
    };

    private Map<Class<?>, List<BluetoothReceiverListener>> mListeners;

    private static IBluetoothReceiver mReceiver;

    private Handler mHandler;

    public static IBluetoothReceiver getInstance() {
        if (mReceiver == null) {
            synchronized (BluetoothReceiver.class) {
                if (mReceiver == null) {
                    BluetoothReceiver receiver = new BluetoothReceiver();
                    mReceiver = ProxyUtils.getUIProxy(receiver, IBluetoothReceiver.class, receiver);
                }
            }
        }
        return mReceiver;
    }

    private BluetoothReceiver() {
        mListeners = new HashMap<Class<?>, List<BluetoothReceiverListener>>();
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
        if (listener != null) {
            List<BluetoothReceiverListener> listeners = mListeners.get(listener.getClass());
            if (listeners == null) {
                listeners = new LinkedList<BluetoothReceiverListener>();
                mListeners.put(listener.getClass(), listeners);
            }
            // You can register repetitive
            listeners.add(listener);
        }
    }

    @Override
    public void unregister(BluetoothReceiverListener listener) {
        if (listener != null) {
            List<BluetoothReceiverListener> listeners = mListeners.get(listener.getClass());
            if (listeners != null) {
                listeners.remove(listener);
            }
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_INVOKE_PROXY:
                ProxyBulk.safeInvoke(msg.obj);
                break;
        }
        return true;
    }

    @Override
    public boolean onIntercept(Object object, Method method, Object[] args) {
        mHandler.obtainMessage(MSG_INVOKE_PROXY, new ProxyBulk(object, method, args))
                .sendToTarget();
        return true;
    }
}
