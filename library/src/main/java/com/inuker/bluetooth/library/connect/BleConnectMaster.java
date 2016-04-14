package com.inuker.bluetooth.library.connect;

import java.util.UUID;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.inuker.bluetooth.library.BleResponseWrapper;
import com.inuker.bluetooth.library.utils.BluetoothConstants;

public class BleConnectMaster implements IBleRunner {

    private HandlerThread mThread;
    private Handler mHandler;

    private BleConnectDispatcher mBleConnectDispatcher;

    public static BleConnectMaster newInstance(String mac) {
        return new BleConnectMaster(mac);
    }

    private BleConnectMaster(String mac) {
        mBleConnectDispatcher = BleConnectDispatcher.newInstance(mac, this);
    }

    private void startMasterLooper() {
        if (mThread == null) {
            mThread = new HandlerThread("BleConnectMaster");
            mThread.start();

            mHandler = new Handler(mThread.getLooper()) {

                @Override
                public void handleMessage(Message msg) {
                    // TODO Auto-generated method stub
                    processDispatcherMessage(msg);
                }
            };
        }
    }

    private void processDispatcherMessage(Message msg) {
        BleResponseWrapper response = (BleResponseWrapper) msg.obj;
        Bundle data = msg.getData();

        switch (msg.what) {
            case BluetoothConstants.MSG_CONNECT:
                mBleConnectDispatcher.connect(response);
                break;

            case BluetoothConstants.MSG_DISCONNECT:
                mBleConnectDispatcher.disconnect();
                break;

            case BluetoothConstants.MSG_READ:
                if (data != null) {
                    UUID service = (UUID) data.getSerializable(BluetoothConstants.KEY_SERVICE_UUID);
                    UUID character = (UUID) data.getSerializable(BluetoothConstants.KEY_CHARACTER_UUID);
                    mBleConnectDispatcher.read(service, character, response);
                }

                break;

            case BluetoothConstants.MSG_WRITE:
                if (data != null) {
                    UUID service = (UUID) data.getSerializable(BluetoothConstants.KEY_SERVICE_UUID);
                    UUID character = (UUID) data.getSerializable(BluetoothConstants.KEY_CHARACTER_UUID);
                    byte[] bytes = data.getByteArray(BluetoothConstants.KEY_BYTES);

                    if (bytes != null) {
                        mBleConnectDispatcher.write(service, character, bytes, response);
                    } else {
                        throw new IllegalStateException("data null");
                    }
                }

                break;

            case BluetoothConstants.MSG_NOTIFY:
                if (data != null) {
                    UUID service = (UUID) data.getSerializable(BluetoothConstants.KEY_SERVICE_UUID);
                    UUID character = (UUID) data.getSerializable(BluetoothConstants.KEY_CHARACTER_UUID);
                    mBleConnectDispatcher.notify(service, character, response);
                }
                break;

            case BluetoothConstants.MSG_UNNOTIFY:
                if (data != null) {
                    UUID service = (UUID) data.getSerializable(BluetoothConstants.KEY_SERVICE_UUID);
                    UUID character = (UUID) data.getSerializable(BluetoothConstants.KEY_CHARACTER_UUID);
                    mBleConnectDispatcher.unnotify(service, character);
                }
                break;

            case BluetoothConstants.MSG_READ_RSSI:
                mBleConnectDispatcher.readRemoteRssi(response);
                break;
        }
    }

    public void connect(BleResponseWrapper response) {
        sendMessageToDispatcher(BluetoothConstants.MSG_CONNECT, response);
    }

    public void disconnect() {
        sendMessageToDispatcher(BluetoothConstants.MSG_DISCONNECT);
    }

    public void read(UUID service, UUID character, BleResponseWrapper response) {
        Bundle data = new Bundle();
        data.putSerializable(BluetoothConstants.KEY_SERVICE_UUID, service);
        data.putSerializable(BluetoothConstants.KEY_CHARACTER_UUID, character);
        sendMessageToDispatcher(BluetoothConstants.MSG_READ, response, data);
    }

    public void write(UUID service, UUID character, byte[] bytes, BleResponseWrapper response) {
        Bundle data = new Bundle();
        data.putSerializable(BluetoothConstants.KEY_SERVICE_UUID, service);
        data.putSerializable(BluetoothConstants.KEY_CHARACTER_UUID, character);
        data.putByteArray(BluetoothConstants.KEY_BYTES, bytes);
        sendMessageToDispatcher(BluetoothConstants.MSG_WRITE, response, data);
    }

    public void notify(UUID service, UUID character, BleResponseWrapper response) {
        Bundle data = new Bundle();
        data.putSerializable(BluetoothConstants.KEY_SERVICE_UUID, service);
        data.putSerializable(BluetoothConstants.KEY_CHARACTER_UUID, character);
        sendMessageToDispatcher(BluetoothConstants.MSG_NOTIFY, response, data);
    }

    public void unnotify(UUID service, UUID character) {
        Bundle data = new Bundle();
        data.putSerializable(BluetoothConstants.KEY_SERVICE_UUID, service);
        data.putSerializable(BluetoothConstants.KEY_CHARACTER_UUID, character);
        sendMessageToDispatcher(BluetoothConstants.MSG_UNNOTIFY, null, data);
    }

    public void readRemoteRssi(BleResponseWrapper response) {
        sendMessageToDispatcher(BluetoothConstants.MSG_READ_RSSI, response);
    }

    private void sendMessageToDispatcher(int what) {
        sendMessageToDispatcher(what, null);
    }

    private void sendMessageToDispatcher(int what, Object obj) {
        sendMessageToDispatcher(what, obj, null);
    }

    private void sendMessageToDispatcher(int what, Object obj, Bundle data) {
        if (mHandler == null) {
            startMasterLooper();
        }
        if (mHandler != null) {
            Message msg = mHandler.obtainMessage(what, obj);

            if (data != null) {
                msg.setData(data);
            }

            msg.sendToTarget();
        }
    }

    @Override
    public Looper getLooper() {
        if (mHandler == null) {
            startMasterLooper();
        }
        return mHandler.getLooper();
    }
}
