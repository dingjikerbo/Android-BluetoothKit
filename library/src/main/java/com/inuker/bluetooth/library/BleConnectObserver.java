package com.inuker.bluetooth.library;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;

import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.BluetoothUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwentian on 2016/8/24.
 */
public class BleConnectObserver implements IBleConnectObserver {

    private static final int MAX_OBSERVABLES = 3;

    private static BleConnectObserver sInstance;

    private BleConnectObserverReceiver mReceiver;

    private List<BleConnectObservable> mObservables;

    private Handler mHandler;

    private BleConnectObserver() {
        mHandler = new Handler(Looper.getMainLooper());
        mObservables = new ArrayList<BleConnectObservable>();
        registerObserverReceiver();
    }

    private void removeBleConnectObservable(String mac) {
        for (int i = 0; i < mObservables.size(); i++) {
            if (mObservables.get(i).mac.equals(mac)) {
                mObservables.remove(i);
                break;
            }
        }
    }

    private void refreshBleConnectObservable(String mac) {
        BleConnectObservable observable = null;

        for (int i = 0; i < mObservables.size(); i++) {
            if (mObservables.get(i).mac.equals(mac)) {
                observable = mObservables.remove(i);
                break;
            }
        }

        if (observable == null) {
            observable = new BleConnectObservable(mac);
        }

        mObservables.add(0, observable);

        if (mObservables.size() > MAX_OBSERVABLES) {
            BluetoothLog.w(String.format("BleConnectObserver reach limit"));
            for (BleConnectObservable observer : mObservables) {
                BluetoothLog.w(String.format(">>> mac = %s", observer.mac));
            }
        }

        while (mObservables.size() > MAX_OBSERVABLES) {
            observable = mObservables.remove(mObservables.size() - 1);
            BleConnectManager.disconnect(observable.mac);
        }
    }

    public static BleConnectObserver getInstance() {
        if (sInstance == null) {
            synchronized (BleConnectObserver.class) {
                if (sInstance == null) {
                    sInstance = new BleConnectObserver();
                }
            }
        }
        return sInstance;
    }

    private void registerObserverReceiver() {
        if (mReceiver == null) {
            mReceiver = new BleConnectObserverReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothConstants.ACTION_CONNECT_STATUS_CHANGED);
            filter.addAction(BluetoothConstants.ACTION_CHARACTER_CHANGED);
            BluetoothUtils.registerReceiver(mReceiver, filter);
        }
    }

    @Override
    public void reportAction(final String mac) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (BluetoothUtils.isDeviceConnected(mac)) {
                    refreshBleConnectObservable(mac);
                }
            }
        });
    }

    private class BleConnectObservable {

        String mac;

        BleConnectObservable(String mac) {
            this.mac = mac;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BleConnectObservable that = (BleConnectObservable) o;

            return mac.equals(that.mac);

        }

        @Override
        public int hashCode() {
            return mac.hashCode();
        }
    }

    private class BleConnectObserverReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null
                    || !intent.hasExtra(BluetoothConstants.EXTRA_MAC)) {
                return;
            }

            String action = intent.getAction();

            if (BluetoothConstants.ACTION_CONNECT_STATUS_CHANGED.equals(action)
                    && intent.hasExtra(BluetoothConstants.EXTRA_STATUS)) {
                processConnectStatusChanged(intent);
            } else if (BluetoothConstants.ACTION_CHARACTER_CHANGED.equals(action)) {
                processCharacterChanged(intent);
            } else {
                //
            }
        }
    }

    private void processConnectStatusChanged(Intent intent) {
        String mac = intent.getStringExtra(BluetoothConstants.EXTRA_MAC);
        int status = intent.getIntExtra(BluetoothConstants.EXTRA_STATUS, 0);

        if (status == BluetoothConstants.STATUS_CONNECTED) {
            refreshBleConnectObservable(mac);
        } else if (status == BluetoothConstants.STATUS_DISCONNECTED) {
            removeBleConnectObservable(mac);
        }
    }

    private void processCharacterChanged(Intent intent) {
        String mac = intent.getStringExtra(BluetoothConstants.EXTRA_MAC);
        refreshBleConnectObservable(mac);
    }
}
