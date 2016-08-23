package com.inuker.bluetooth.library;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;

import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleResponseStub;
import com.inuker.bluetooth.library.utils.ProxyUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by dingjikerbo on 16/4/8.
 */
public class BluetoothClient {

    private Context mContext;

    private IBluetoothService mBluetoothManager;

    private static BluetoothClient sInstance;

    private BluetoothClient(Context context) {
        mContext = context.getApplicationContext();
    }

    public static BluetoothClient getInstance(Context context) {
        if (sInstance == null) {
            synchronized (BluetoothClient.class) {
                if (sInstance == null) {
                    sInstance = new BluetoothClient(context);
                }
            }
        }
        return sInstance;
    }

    private void bindService() {
        if (mBluetoothManager != null) {
            return;
        }

        Intent intent = new Intent();
        intent.setClass(mContext, BluetoothService.class);
        mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothManager = IBluetoothService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothManager = null;
            bindService();
        }
    };

    public void connect(String mac, BleResponse response) {
        Bundle args = new Bundle();
        args.putString(BluetoothConstants.EXTRA_MAC, mac);

        try {
            mBluetoothManager.callBluetoothApi(BluetoothConstants.CODE_CONNECT, args, response);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
