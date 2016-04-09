package com.inuker.bluetooth.library;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleResponseImpl;

/**
 * Created by dingjikerbo on 16/4/8.
 */
public class BluetoothClient {

    private Context mContext;

    private final Object sLock = new Object();

    private IBluetoothManager mBluetoothManager;

    public BluetoothClient(Context context) {
        mContext = context.getApplicationContext();
    }

    private void checkService() {
        if (mBluetoothManager != null) {
            return;
        }

        Intent intent = new Intent();
        intent.setClass(mContext, BluetoothService.class);
        mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        synchronized (sLock) {
            try {
                sLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothManager = IBluetoothManager.Stub.asInterface(service);

            try {
                service.linkToDeath(mDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            synchronized (sLock) {
                sLock.notifyAll();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothManager = null;
        }
    };

    private final IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            mBluetoothManager = null;
        }
    };

    public void connect(String mac, BleConnectResponse response) {
        checkService();

        if (mBluetoothManager != null) {
            try {
                mBluetoothManager.connect(mac, BleResponseImpl.newInstance(response));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
