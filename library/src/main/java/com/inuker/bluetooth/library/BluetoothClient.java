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

import java.util.UUID;

/**
 * Created by dingjikerbo on 16/4/8.
 */
public class BluetoothClient implements IBluetoothApi {

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

    @Override
    public void connect(String mac, BleResponse response) {
        Bundle args = new Bundle();
        args.putString(BluetoothConstants.EXTRA_MAC, mac);
        safeCallBluetoothApi(BluetoothConstants.CODE_CONNECT, args, response);
    }

    @Override
    public void disconnect(String mac) {
        Bundle args = new Bundle();
        args.putString(BluetoothConstants.EXTRA_MAC, mac);
        safeCallBluetoothApi(BluetoothConstants.CODE_DISCONNECT, args, null);
    }

    @Override
    public void read(String mac, UUID service, UUID character, BleResponse response) {
        Bundle args = new Bundle();
        args.putString(BluetoothConstants.EXTRA_MAC, mac);
        args.putSerializable(BluetoothConstants.EXTRA_SERVICE_UUID, service);
        args.putSerializable(BluetoothConstants.EXTRA_CHARACTER_UUID, character);
        safeCallBluetoothApi(BluetoothConstants.CODE_READ, args, response);
    }

    @Override
    public void write(String mac, UUID service, UUID character, byte[] value, BleResponse response) {
        Bundle args = new Bundle();
        args.putString(BluetoothConstants.EXTRA_MAC, mac);
        args.putSerializable(BluetoothConstants.EXTRA_SERVICE_UUID, service);
        args.putSerializable(BluetoothConstants.EXTRA_CHARACTER_UUID, character);
        args.putByteArray(BluetoothConstants.EXTRA_BYTE_VALUE, value);
        safeCallBluetoothApi(BluetoothConstants.CODE_WRITE, args, response);
    }

    @Override
    public void notify(String mac, UUID service, UUID character, BleResponse response) {
        Bundle args = new Bundle();
        args.putString(BluetoothConstants.EXTRA_MAC, mac);
        args.putSerializable(BluetoothConstants.EXTRA_SERVICE_UUID, service);
        args.putSerializable(BluetoothConstants.EXTRA_CHARACTER_UUID, character);
        safeCallBluetoothApi(BluetoothConstants.CODE_NOTIFY, args, response);
    }

    @Override
    public void unnotify(String mac, UUID service, UUID character, BleResponse response) {
        Bundle args = new Bundle();
        args.putString(BluetoothConstants.EXTRA_MAC, mac);
        args.putSerializable(BluetoothConstants.EXTRA_SERVICE_UUID, service);
        args.putSerializable(BluetoothConstants.EXTRA_CHARACTER_UUID, character);
        safeCallBluetoothApi(BluetoothConstants.CODE_UNNOTIFY, args, response);
    }

    @Override
    public void readRssi(String mac, BleResponse response) {
        Bundle args = new Bundle();
        args.putString(BluetoothConstants.EXTRA_MAC, mac);
        safeCallBluetoothApi(BluetoothConstants.CODE_READ_RSSI, args, response);
    }

    private void safeCallBluetoothApi(int code, Bundle args, BleResponse response) {
        try {
            mBluetoothManager.callBluetoothApi(BluetoothConstants.CODE_WRITE, args, response);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
