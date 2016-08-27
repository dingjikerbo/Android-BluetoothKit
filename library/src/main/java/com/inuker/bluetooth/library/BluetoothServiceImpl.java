package com.inuker.bluetooth.library;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

import com.inuker.bluetooth.library.connect.BleConnectManager;
import com.inuker.bluetooth.library.connect.IBluetoothApi;
import com.inuker.bluetooth.library.connect.response.BluetoothResponse;

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
        BluetoothResponse response = (BluetoothResponse) msg.obj;

        switch (msg.what) {
            case CODE_CONNECT:
                BleConnectManager.connect(mac, response);
                break;

            case CODE_DISCONNECT:
                BleConnectManager.disconnect(mac);
                break;

            case CODE_READ:
                BleConnectManager.read(mac, service, character, response);
                break;

            case CODE_WRITE:
                BleConnectManager.write(mac, service, character, value, response);
                break;

            case CODE_NOTIFY:
                BleConnectManager.notify(mac, service, character, response);
                break;

            case CODE_UNNOTIFY:
                BleConnectManager.unnotify(mac, service, character, response);
                break;

            case CODE_READ_RSSI:
                BleConnectManager.readRssi(mac, response);
                break;
        }
        return true;
    }
}
