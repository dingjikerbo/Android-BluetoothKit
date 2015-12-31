package com.dingjikerbo.bluetooth.library.connect;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.dingjikerbo.bluetooth.library.connect.request.IBleRunner;
import com.dingjikerbo.bluetooth.library.connect.response.BleConnectResponse;
import com.dingjikerbo.bluetooth.library.connect.response.BleNotifyResponse;
import com.dingjikerbo.bluetooth.library.connect.response.BleReadResponse;
import com.dingjikerbo.bluetooth.library.connect.response.BleReadRssiResponse;
import com.dingjikerbo.bluetooth.library.connect.response.BleResponse;
import com.dingjikerbo.bluetooth.library.connect.response.BleWriteResponse;
import com.dingjikerbo.bluetooth.library.utils.BluetoothConstants;

import java.util.UUID;

public class BleConnectMaster implements IBleRunner {

    private HandlerThread mThread;
    private Handler mHandler;

    private BleConnectDispatcher mBleConnectDispatcher;

    private BleConnectMaster(String mac) {
        mBleConnectDispatcher = BleConnectDispatcher.newInstance(mac, this);
    }

    public static BleConnectMaster newInstance(String mac) {
        return new BleConnectMaster(mac);
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
        BleResponse response = (BleResponse) msg.obj;
        Bundle data = msg.getData();

        switch (msg.what) {
            case BluetoothConstants.MSG_CONNECT:
                mBleConnectDispatcher.connect((BleConnectResponse) response);
                break;

            case BluetoothConstants.MSG_DISCONNECT:
                mBleConnectDispatcher.disconnect();
                break;

            case BluetoothConstants.MSG_READ:
                if (data != null) {
                    UUID service = (UUID) data.getSerializable(BluetoothConstants.KEY_SERVICE_UUID);
                    UUID character = (UUID) data.getSerializable(BluetoothConstants.KEY_CHARACTER_UUID);
                    mBleConnectDispatcher.read(service, character, (BleReadResponse) response);
                }

                break;

            case BluetoothConstants.MSG_WRITE:
                if (data != null) {
                    UUID service = (UUID) data.getSerializable(BluetoothConstants.KEY_SERVICE_UUID);
                    UUID character = (UUID) data.getSerializable(BluetoothConstants.KEY_CHARACTER_UUID);
                    byte[] bytes = data.getByteArray(BluetoothConstants.KEY_BYTES);

                    if (bytes != null) {
                        mBleConnectDispatcher.write(service, character, bytes, (BleWriteResponse) response);
                    } else {
                        throw new IllegalStateException("data null");
                    }
                }

                break;

            case BluetoothConstants.MSG_NOTIFY:
                if (data != null) {
                    UUID service = (UUID) data.getSerializable(BluetoothConstants.KEY_SERVICE_UUID);
                    UUID character = (UUID) data.getSerializable(BluetoothConstants.KEY_CHARACTER_UUID);
                    mBleConnectDispatcher.notify(service, character, (BleNotifyResponse) response);
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
                mBleConnectDispatcher.readRemoteRssi((BleReadRssiResponse) response);
                break;
        }
    }

    public void connect(BleConnectResponse response) {
        sendMessageToDispatcher(BluetoothConstants.MSG_CONNECT, response);
    }

    public void disconnect() {
        sendMessageToDispatcher(BluetoothConstants.MSG_DISCONNECT);
    }

    public void read(UUID service, UUID character, BleReadResponse response) {
        Bundle data = new Bundle();
        data.putSerializable(BluetoothConstants.KEY_SERVICE_UUID, service);
        data.putSerializable(BluetoothConstants.KEY_CHARACTER_UUID, character);
        sendMessageToDispatcher(BluetoothConstants.MSG_READ, response, data);
    }

    public void write(UUID service, UUID character, byte[] bytes, BleWriteResponse response) {
        Bundle data = new Bundle();
        data.putSerializable(BluetoothConstants.KEY_SERVICE_UUID, service);
        data.putSerializable(BluetoothConstants.KEY_CHARACTER_UUID, character);
        data.putByteArray(BluetoothConstants.KEY_BYTES, bytes);
        sendMessageToDispatcher(BluetoothConstants.MSG_WRITE, response, data);
    }

    public void notify(UUID service, UUID character, BleNotifyResponse response) {
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

    public void readRemoteRssi(BleReadRssiResponse response) {
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
