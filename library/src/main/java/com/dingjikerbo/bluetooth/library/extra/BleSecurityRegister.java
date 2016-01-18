package com.dingjikerbo.bluetooth.library.extra;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.dingjikerbo.bluetooth.library.connect.BLEConnectManager;
import com.dingjikerbo.bluetooth.library.connect.request.Code;
import com.dingjikerbo.bluetooth.library.response.BleConnectResponse;
import com.dingjikerbo.bluetooth.library.response.BleNotifyResponse;
import com.dingjikerbo.bluetooth.library.response.BleRegisterResponse;
import com.dingjikerbo.bluetooth.library.response.BleWriteResponse;
import com.dingjikerbo.bluetooth.library.utils.BluetoothConstants;
import com.dingjikerbo.bluetooth.library.utils.BluetoothLog;
import com.dingjikerbo.bluetooth.library.utils.BluetoothUtils;
import com.dingjikerbo.bluetooth.library.utils.ByteUtils;

import java.util.UUID;

/**
 * Created by liwentian on 2016/1/8.
 */
public class BleSecurityRegister {

    private static final int MSG_NOTIFY_TIMEOUT = 0x1;

    private static final int SESSION_START = 0xDE85CA90;
    private static final int SESSION_END = 0xFA54AB92;

    private String mDeviceMac;
    private int mProductId;
    private BleRegisterResponse mRegisterResponse;

    private boolean mCanceled;

    private byte[] mToken;

    public BleSecurityRegister(String mac, int pid) {
        mDeviceMac = mac;
        mProductId = pid;
    }

    public void register(final BleRegisterResponse response) {
        mRegisterResponse = response;

        BluetoothLog.w("BleSecurityRegister start");
        BluetoothLog.d("Connect to " + mDeviceMac);
        BLEConnectManager.connect(mDeviceMac, mBleConnectResponse);
    }

    private final BleConnectResponse mBleConnectResponse = new BleConnectResponse() {
        @Override
        public void onResponse(int code, Bundle data) {
            // TODO Auto-generated method stub
            BluetoothLog.d("connect onResponse: " + Code.toString(code));

            if (code == Code.REQUEST_SUCCESS) {
                processStep1();
            } else {
                dispatchRegisterResult(Code.REQUEST_FAILED);
            }
        }
    };

    public void cancel() {
        BluetoothLog.d("BleSecurityRegister cancel");
        mCanceled = true;
        unregisterBleNotifyReceiver();
    }

    private void processStep1() {
        if (mCanceled) {
            dispatchRegisterResult(Code.REQUEST_CANCELED);
            return;
        }

        BluetoothLog.d("process step 1 ...");

        BLEConnectManager.write(mDeviceMac, BluetoothConstants.MISERVICE,
                BluetoothConstants.CHARACTER_EVENT,
                ByteUtils.fromInt(SESSION_START), new BleWriteResponse() {

                    @Override
                    public void onResponse(int code, Void data) {
                        // TODO Auto-generated method stub
                        BluetoothLog.d("step 1 onResponse: " + Code.toString(code));

                        if (code == Code.REQUEST_SUCCESS) {
                            processStep2();
                        } else {
                            dispatchRegisterResult(Code.REQUEST_FAILED);
                        }
                    }

                });
    }

    private void processStep2() {
        if (mCanceled) {
            dispatchRegisterResult(Code.REQUEST_CANCELED);
            return;
        }

        BluetoothLog.d("process step 2 ...");

        BLEConnectManager.notify(mDeviceMac, BluetoothConstants.MISERVICE,
                BluetoothConstants.CHARACTER_TOKEN, mBleNotifyResponse);

        registerBleNotifyReceiver();
    }

    private final BleNotifyResponse mBleNotifyResponse = new BleNotifyResponse() {
        @Override
        public void onResponse(int code, Void data) {
            // TODO Auto-generated method stub
            BluetoothLog.d("step 2 onResponse: " + Code.toString(code));

            if (code == Code.REQUEST_SUCCESS) {
                processStep3();
            } else {
                dispatchRegisterResult(Code.REQUEST_FAILED);
            }
        }

    };

    private void processNotify(UUID service, UUID character, byte[] data) {
        if (service.equals(BluetoothConstants.MISERVICE) && character.equals(BluetoothConstants.CHARACTER_TOKEN)) {
            if (mHandler.hasMessages(MSG_NOTIFY_TIMEOUT)) {
                mHandler.removeMessages(MSG_NOTIFY_TIMEOUT);

                byte[] key1 = BLECipher.mixA(mDeviceMac, mProductId);
                byte[] key2 = BLECipher.mixB(mDeviceMac, mProductId);

                byte[] token = BLECipher.encrypt(key2,
                        BLECipher.encrypt(key1, data));

                if (ByteUtils.byteEquals(token, mToken)) {
                    processStep4();
                } else {
                    BluetoothLog.w("token not match");
                    dispatchRegisterResult(Code.REQUEST_FAILED);
                }
            }
        }
    }

    private void processStep3() {
        if (mCanceled) {
            dispatchRegisterResult(Code.REQUEST_CANCELED);
            return;
        }

        BluetoothLog.d("process step 3");

        mToken = BluetoothUtils.generateToken();

        byte[] key1 = BLECipher.mixA(mDeviceMac, mProductId);

        byte[] t1 = BLECipher.encrypt(key1, mToken);

        BLEConnectManager.write(mDeviceMac, BluetoothConstants.MISERVICE,
                BluetoothConstants.CHARACTER_TOKEN, t1, new BleWriteResponse() {

                    @Override
                    public void onResponse(int code, Void data) {
                        // TODO Auto-generated method stub
//                        BluetoothLog.d("step 3 onResponse: " + Code.toString(code));
//
//                        if (code == Code.REQUEST_SUCCESS) {
//                            mHandler.removeMessages(MSG_NOTIFY_TIMEOUT);
//                            mHandler.sendEmptyMessageDelayed(
//                                    MSG_NOTIFY_TIMEOUT, BluetoothConstants.NOTIFY_TIMEOUT);
//                        } else {
//                            dispatchRegisterResult(Code.REQUEST_FAILED);
//                        }
                    }

                });

        mHandler.removeMessages(MSG_NOTIFY_TIMEOUT);
        mHandler.sendEmptyMessageDelayed(
                MSG_NOTIFY_TIMEOUT, BluetoothConstants.NOTIFY_TIMEOUT);
    }

    private void processStep4() {
        if (mCanceled) {
            dispatchRegisterResult(Code.REQUEST_CANCELED);
            return;
        }

        BluetoothLog.d("process step 4 ...");

        byte[] flag = ByteUtils.fromInt(SESSION_END);
        byte[] bytes = BLECipher.encrypt(mToken, flag);

        BLEConnectManager.write(mDeviceMac, BluetoothConstants.MISERVICE,
                BluetoothConstants.CHARACTER_TOKEN, bytes,
                new BleWriteResponse() {

                    @Override
                    public void onResponse(int code, Void data) {
                        // TODO Auto-generated method stub
                        BluetoothLog.d("step 4 onResponse: " + Code.toString(code));

                        if (code == Code.REQUEST_SUCCESS) {
                            dispatchRegisterResult(Code.REQUEST_SUCCESS);
                        } else {
                            dispatchRegisterResult(Code.REQUEST_FAILED);
                        }
                    }

                });
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case MSG_NOTIFY_TIMEOUT:
                    BluetoothLog.w("notify timeout");
                    dispatchRegisterResult(Code.REQUEST_FAILED);
                    break;
            }
        }

    };

    private void dispatchRegisterResult(int code) {
        BluetoothLog.d("dispatchRegisterResult " + Code.toString(code));

        if (code != Code.REQUEST_SUCCESS) {
            BLEConnectManager.disconnect(mDeviceMac);
        }

        BLEConnectManager.unnotify(mDeviceMac,
                BluetoothConstants.MISERVICE,
                BluetoothConstants.CHARACTER_TOKEN);

        if (mRegisterResponse != null) {
            mRegisterResponse.onResponse(code, mToken);
        }

        mRegisterResponse = null;

        unregisterBleNotifyReceiver();
    }

    private void registerBleNotifyReceiver() {
        BluetoothLog.d("registerBleNotifyReceiver");
        IntentFilter filter = new IntentFilter(BluetoothConstants.ACTION_CHARACTER_CHANGED);
        BluetoothUtils.registerReceiver(mReceiver, filter);
    }

    private void unregisterBleNotifyReceiver() {
        BluetoothLog.d("unregisterBleNotifyReceiver");
        BluetoothUtils.unregisterReceiver(mReceiver);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }

            String action = intent.getAction();

            if (BluetoothConstants.ACTION_CHARACTER_CHANGED.equalsIgnoreCase(action)) {
                String mac = intent.getStringExtra(BluetoothConstants.KEY_DEVICE_ADDRESS);

                if (mDeviceMac.equalsIgnoreCase(mac)) {
                    UUID service = (UUID) intent.getSerializableExtra(BluetoothConstants.KEY_SERVICE_UUID);
                    UUID character = (UUID) intent.getSerializableExtra(BluetoothConstants.KEY_CHARACTER_UUID);
                    byte[] value = intent.getByteArrayExtra(BluetoothConstants.KEY_CHARACTER_VALUE);

                    if (service != null && character != null) {
                        processNotify(service, character, value);
                    }
                }
            }
        }
    };
}
