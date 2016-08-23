package com.inuker.bluetooth.library;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

import java.util.UUID;

/**
 * Created by liwentian on 2015/10/29.
 */
public class BluetoothServiceImpl extends IBluetoothService.Stub implements Handler.Callback, IBluetoothApi {

    private static BluetoothServiceImpl sInstance;

    private Handler mHandler;

    private BluetoothServiceImpl() {
        mHandler = new Handler(Looper.getMainLooper(), this);
    }

    public static BluetoothServiceImpl getInstance() {
        if (sInstance == null) {
            synchronized (BluetoothServiceImpl.class) {
                if (sInstance == null) {
                    sInstance = new BluetoothServiceImpl();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void callBluetoothApi(int code, Bundle args, IResponse response) throws RemoteException {
        Message msg = mHandler.obtainMessage(code, response);
        msg.setData(args);
        msg.sendToTarget();
    }

    @Override
    public boolean handleMessage(Message msg) {
        Bundle args = msg.getData();
        String mac = args.getString(BluetoothConstants.EXTRA_MAC);
        UUID service = (UUID) args.getSerializable(BluetoothConstants.EXTRA_SERVICE_UUID);
        UUID character = (UUID) args.getSerializable(BluetoothConstants.EXTRA_CHARACTER_UUID);
        byte[] value = args.getByteArray(BluetoothConstants.EXTRA_BYTE_VALUE);
        BleResponse response = (BleResponse) msg.obj;

        switch (msg.what) {
            case BluetoothConstants.CODE_CONNECT:
                connect(mac, response);
                break;

            case BluetoothConstants.CODE_DISCONNECT:
                disconnect(mac);
                break;

            case BluetoothConstants.CODE_READ:
                read(mac, service, character, response);
                break;

            case BluetoothConstants.CODE_WRITE:
                write(mac, service, character, value, response);
                break;

            case BluetoothConstants.CODE_NOTIFY:
                notify(mac, service, character, response);
                break;

            case BluetoothConstants.CODE_UNNOTIFY:
                unnotify(mac, service, character, response);
                break;

            case BluetoothConstants.CODE_READ_RSSI:
                readRssi(mac, response);
                break;
        }
        return true;
    }

    @Override
    public void connect(String mac, BleResponse response) {

    }

    @Override
    public void disconnect(String mac) {

    }

    @Override
    public void read(String mac, UUID service, UUID character, BleResponse response) {

    }

    @Override
    public void write(String mac, UUID service, UUID character, byte[] value, BleResponse response) {

    }

    @Override
    public void notify(String mac, UUID service, UUID character, BleResponse response) {

    }

    @Override
    public void unnotify(String mac, UUID service, UUID character, BleResponse response) {

    }

    @Override
    public void readRssi(String mac, BleResponse response) {

    }
}
