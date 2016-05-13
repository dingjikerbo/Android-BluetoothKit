package com.inuker.library.connect;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.inuker.library.BluetoothConstants;
import com.inuker.library.connect.response.BleConnectResponse;
import com.inuker.library.connect.response.BleNotifyResponse;
import com.inuker.library.connect.response.BleReadResponse;
import com.inuker.library.connect.response.BleReadRssiResponse;
import com.inuker.library.connect.response.BleWriteResponse;

import java.util.UUID;

public class BleConnectMaster implements IBleRunner {

    public static final int MSG_CONNECT = 0x10;
    public static final int MSG_READ = 0x20;
    public static final int MSG_WRITE = 0x30;
    public static final int MSG_DISCONNECT = 0x40;
    public static final int MSG_NOTIFY = 0x50;
    public static final int MSG_UNNOTIFY = 0x60;
    public static final int MSG_RESPONSE = 0x70;
    public static final int MSG_CLOSE = 0x80;
    public static final int MSG_READ_RSSI = 0x90;

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
        BleResponser response = (BleResponser) msg.obj;
        Bundle data = msg.getData();

        switch (msg.what) {
            case MSG_CONNECT:
                mBleConnectDispatcher.connect(response);
                break;

            case MSG_DISCONNECT:
                mBleConnectDispatcher.disconnect();
                break;

            case MSG_READ:
                if (data != null) {
                    UUID service = (UUID) data.getSerializable(BluetoothConstants.KEY_SERVICE_UUID);
                    UUID character = (UUID) data.getSerializable(BluetoothConstants.KEY_CHARACTER_UUID);
                    mBleConnectDispatcher.read(service, character, response);
                }

                break;

            case MSG_WRITE:
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

            case MSG_NOTIFY:
                if (data != null) {
                    UUID service = (UUID) data.getSerializable(BluetoothConstants.KEY_SERVICE_UUID);
                    UUID character = (UUID) data.getSerializable(BluetoothConstants.KEY_CHARACTER_UUID);
                    mBleConnectDispatcher.notify(service, character, response);
                }
                break;

            case MSG_UNNOTIFY:
                if (data != null) {
                    UUID service = (UUID) data.getSerializable(BluetoothConstants.KEY_SERVICE_UUID);
                    UUID character = (UUID) data.getSerializable(BluetoothConstants.KEY_CHARACTER_UUID);
                    mBleConnectDispatcher.unnotify(service, character);
                }
                break;

            case MSG_READ_RSSI:
                mBleConnectDispatcher.readRemoteRssi(response);
                break;
        }
    }

    public void connect(BleConnectResponse response) {
        sendMessageToDispatcher(MSG_CONNECT, BleResponser.newInstance(response));
    }

    public void disconnect() {
        sendMessageToDispatcher(MSG_DISCONNECT);
    }

    public void read(UUID service, UUID character, BleReadResponse response) {
        Bundle data = new Bundle();
        data.putSerializable(BluetoothConstants.KEY_SERVICE_UUID, service);
        data.putSerializable(BluetoothConstants.KEY_CHARACTER_UUID, character);
        sendMessageToDispatcher(MSG_READ, BleResponser.newInstance(response), data);
    }

    public void write(UUID service, UUID character, byte[] bytes, BleWriteResponse response) {
        Bundle data = new Bundle();
        data.putByteArray(BluetoothConstants.KEY_BYTES, bytes);
        data.putSerializable(BluetoothConstants.KEY_SERVICE_UUID, service);
        data.putSerializable(BluetoothConstants.KEY_CHARACTER_UUID, character);
        sendMessageToDispatcher(MSG_WRITE, BleResponser.newInstance(response), data);
    }

    public void notify(UUID service, UUID character, BleNotifyResponse response) {
        Bundle data = new Bundle();
        data.putSerializable(BluetoothConstants.KEY_SERVICE_UUID, service);
        data.putSerializable(BluetoothConstants.KEY_CHARACTER_UUID, character);
        sendMessageToDispatcher(MSG_NOTIFY, BleResponser.newInstance(response), data);
    }

    public void unnotify(UUID service, UUID character) {
        Bundle data = new Bundle();
        data.putSerializable(BluetoothConstants.KEY_SERVICE_UUID, service);
        data.putSerializable(BluetoothConstants.KEY_CHARACTER_UUID, character);
        sendMessageToDispatcher(MSG_UNNOTIFY, null, data);
    }

    public void readRemoteRssi(BleReadRssiResponse response) {
        sendMessageToDispatcher(MSG_READ_RSSI, BleResponser.newInstance(response));
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
