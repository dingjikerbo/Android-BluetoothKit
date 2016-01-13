package com.dingjikerbo.bluetooth.library.security;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.dingjikerbo.bluetooth.library.BaseManager;
import com.dingjikerbo.bluetooth.library.connect.BLEConnectManager;
import com.dingjikerbo.bluetooth.library.connect.request.Code;
import com.dingjikerbo.bluetooth.library.connect.response.BleConnectResponse;
import com.dingjikerbo.bluetooth.library.connect.response.BleNotifyResponse;
import com.dingjikerbo.bluetooth.library.connect.response.BleResponse;
import com.dingjikerbo.bluetooth.library.connect.response.BleWriteResponse;
import com.dingjikerbo.bluetooth.library.utils.BluetoothConstants;
import com.dingjikerbo.bluetooth.library.utils.BluetoothLog;
import com.dingjikerbo.bluetooth.library.utils.BluetoothUtils;
import com.dingjikerbo.bluetooth.library.utils.ByteUtils;

import java.util.Arrays;
import java.util.UUID;

/**
 * Created by liwentian on 2016/1/8.
 */
public class BleSecurityLogin extends BaseManager {

    private String mDeviceMac;
    private byte[] mToken;

    private BleLoginResponse mLoginResponse;

    private boolean mCanceled;

    private static final int MSG_NOTIFY_TICK_TIMEOUT = 0x1;
    private static final int MSG_NOTIFY_CONFIRM_TIMEOUT = 0x2;

    private static final int SESSION_START = 0xCD43BC00;
    private static final int SESSION_END = 0x93BFAC09;
    private static final int SESSION_CONFIRM = 0x369A58C9;

    /**
     * token^tick形成的加密key
     */
    private byte[] mEncryptKey;

    public BleSecurityLogin(String mac, byte[] token) {
        mDeviceMac = mac;
        mToken = token;
    }

    public void cancel() {
        BluetoothLog.d("BleSecurityLogin cancel");
        mCanceled = true;
        unregisterBleNotifyReceiver();
    }

    public void login(BleLoginResponse response) {
        mLoginResponse = response;

        BluetoothLog.w("security login start");
        BluetoothLog.d("connect to " + mDeviceMac);
        BLEConnectManager.connect(mDeviceMac, mBleConnectResponse);
    }

    private final BleConnectResponse mBleConnectResponse = new BleConnectResponse() {

        @Override
        public void onResponse(int code, Bundle data) {
            BluetoothLog.d("connect onResponse: " + Code.toString(code));

            if (code == Code.REQUEST_SUCCESS) {
                processStep1();
            } else {
                dispatchLoginResult(Code.REQUEST_FAILED);
            }
        }
    };

    private void processStep1() {
        if (mCanceled) {
            dispatchLoginResult(Code.REQUEST_CANCELED);
            return;
        }

        BluetoothLog.d("process step 1 ...");

        BLEConnectManager.notify(mDeviceMac, BluetoothConstants.MISERVICE,
                BluetoothConstants.CHARACTER_TOKEN, mBleNotifyResponse);

        registerBleNotifyReceiver();
    }

    private final BleNotifyResponse mBleNotifyResponse = new BleNotifyResponse() {
        @Override
        public void onResponse(int code, Void data) {
            // TODO Auto-generated method stub
            BluetoothLog.d("step 1 onResponse: " + Code.toString(code));

            if (code == Code.REQUEST_SUCCESS) {
                processStep2();
            } else {
                dispatchLoginResult(Code.REQUEST_FAILED);
            }
        }
    };

    private void processNotify(UUID service, UUID character, byte[] data) {
        if (service.equals(BluetoothConstants.MISERVICE) && character.equals(BluetoothConstants.CHARACTER_TOKEN)) {
            if (mHandler.hasMessages(MSG_NOTIFY_TICK_TIMEOUT)) {
                BluetoothLog.d("login onNotify tick");
                mHandler.removeMessages(MSG_NOTIFY_TICK_TIMEOUT);
                processTickNotify(data);
            } else if (mHandler
                    .hasMessages(MSG_NOTIFY_CONFIRM_TIMEOUT)) {
                BluetoothLog.d("login onNotify confirm");
                mHandler.removeMessages(MSG_NOTIFY_CONFIRM_TIMEOUT);
                processConfirmNotify(data);
            }
        }
    }

    private void processTickNotify(byte[] data) {
        BluetoothLog.d("processTickNotify " + ByteUtils.byteToString(data));

        if (data != null && data.length == 4) {
            byte[] tick = BLECipher.encrypt(mToken, data);

            BluetoothLog.v("processTickNotify tick = "
                    + ByteUtils.byteToString(tick));

            mEncryptKey = Arrays.copyOfRange(mToken, 0, mToken.length);

            for (int i = 0; i < tick.length; i++) {
                mEncryptKey[i] ^= tick[i];
            }

            processStep3(BLECipher.encrypt(mEncryptKey,
                    ByteUtils.fromInt(SESSION_END)));

        } else {
            dispatchLoginResult(Code.REQUEST_FAILED);
        }
    }

    private void processConfirmNotify(byte[] data) {
        BluetoothLog.d("processConfirmNotify "
                + ByteUtils.byteToString(data));

        byte[] bytes = BLECipher.encrypt(mEncryptKey, data);

        if (ByteUtils.byteEquals(bytes, ByteUtils.fromInt(SESSION_CONFIRM))) {
            dispatchLoginResult(Code.REQUEST_SUCCESS);
        } else {
            dispatchLoginResult(Code.TOKEN_NOT_MATCHED);
        }
    }

    private void processStep2() {
        if (mCanceled) {
            dispatchLoginResult(Code.REQUEST_CANCELED);
            return;
        }

        BluetoothLog.d("process step 2");

        BLEConnectManager.write(mDeviceMac, BluetoothConstants.MISERVICE,
                BluetoothConstants.CHARACTER_EVENT,
                ByteUtils.fromInt(SESSION_START), new BleWriteResponse() {

                    @Override
                    public void onResponse(int code, Void data) {
                        // TODO Auto-generated method stub
                    }

                });

        mHandler.removeMessages(MSG_NOTIFY_TICK_TIMEOUT);
        mHandler.sendEmptyMessageDelayed(
                MSG_NOTIFY_TICK_TIMEOUT, BluetoothConstants.NOTIFY_TIMEOUT);
    }

    private void processStep3(byte[] bytes) {
        if (mCanceled) {
            dispatchLoginResult(Code.REQUEST_CANCELED);
            return;
        }

        BluetoothLog.d("process step 3");

        BLEConnectManager.write(mDeviceMac, BluetoothConstants.MISERVICE,
                BluetoothConstants.CHARACTER_TOKEN, bytes,
                new BleWriteResponse() {

                    @Override
                    public void onResponse(int code, Void data) {
                        // TODO Auto-generated method stub
                    }

                });

        mHandler.removeMessages(MSG_NOTIFY_CONFIRM_TIMEOUT);
        mHandler.sendEmptyMessageDelayed(
                MSG_NOTIFY_CONFIRM_TIMEOUT, BluetoothConstants.NOTIFY_TIMEOUT);
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case MSG_NOTIFY_TICK_TIMEOUT:
                    BluetoothLog.w("tick notify timeout");
                    mHandler.removeMessages(MSG_NOTIFY_TICK_TIMEOUT);
                    dispatchLoginResult(Code.REQUEST_FAILED);
                    break;

                case MSG_NOTIFY_CONFIRM_TIMEOUT:
                    BluetoothLog.w("confirm notify timeout");
                    mHandler.removeMessages(MSG_NOTIFY_CONFIRM_TIMEOUT);
                    dispatchLoginResult(Code.TOKEN_NOT_MATCHED);
                    break;
            }
        }

    };

    public interface BleLoginResponse extends BleResponse<Void> {

    }

    private void dispatchLoginResult(int code) {
        BluetoothLog.d("dispatchLoginResult " + Code.toString(code));

        if (code != Code.REQUEST_SUCCESS) {
            BLEConnectManager.disconnect(mDeviceMac);
        }

        BLEConnectManager.unnotify(mDeviceMac,
                BluetoothConstants.MISERVICE,
                BluetoothConstants.CHARACTER_TOKEN);

        if (mLoginResponse != null) {
            mLoginResponse.onResponse(code, null);
        }

        mLoginResponse = null;

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
