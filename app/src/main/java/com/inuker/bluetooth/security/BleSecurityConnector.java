package com.inuker.bluetooth.security;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.inuker.bluetooth.BluetoothConstants;
import com.inuker.bluetooth.ClientManager;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BluetoothResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.xiaomi.smarthome.device.bluetooth.security.BLECipher;
import static com.inuker.bluetooth.library.Constants.*;

import java.util.UUID;

/**
 * Created by dingjikerbo on 2016/2/22.
 */
public abstract class BleSecurityConnector {

    protected BluetoothResponse mResponse;

    private ConnectReceiver mReceiver;

    protected Bundle mBundle;

    protected boolean mNeedBindToServer = true;

    protected String mMac;

    protected int mProductId;

    protected BleSecurityConnector(String mac, int productId) {
        mMac = mac;
        mProductId = productId;
        mBundle = new Bundle();
    }

    public void connect(BluetoothResponse response) {
        mResponse = response;
        ClientManager.getClient().connect(mMac, mBleConnectResponse);
    }

    private final BleConnectResponse mBleConnectResponse = new BleConnectResponse() {
        @Override
        public void onResponse(int code, BleGattProfile profile) {
            BluetoothLog.v(String.format("code onResponse: code = %d", code));

            if (code == REQUEST_SUCCESS) {
                if (profile != null) {
                    mBundle.putParcelable("profile", profile);
                }
                mNeedBindToServer = checkNeedBindToServer(profile);
                processStep1();
            } else {
                dispatchResult(REQUEST_FAILED);
            }
        }
    };

    public void disconnect() {
        ClientManager.getClient().disconnect(mMac);
    }

    /**
     * 根据设备中是否有SN和beaconKey的character来判断是否要去云端绑定
     * @return
     */
    protected boolean checkNeedBindToServer(BleGattProfile profile) {
        return true;
    }

    protected void openTokenNotify(BleNotifyResponse response) {
        ClientManager.getClient().notify(mMac, BluetoothConstants.MISERVICE, BluetoothConstants.CHARACTER_TOKEN, response);
        registerBleNotifyReceiver();
    }

    protected void closeTokenNotify() {
        ClientManager.getClient().unnotify(mMac, BluetoothConstants.MISERVICE, BluetoothConstants.CHARACTER_TOKEN, null);
        unregisterBleNotifyReceiver();
    }

    private void registerBleNotifyReceiver() {
        if (mReceiver == null) {
            mReceiver = new ConnectReceiver();
            IntentFilter filter = new IntentFilter(ACTION_CHARACTER_CHANGED);
            filter.addAction(ACTION_CONNECT_STATUS_CHANGED);
            BluetoothUtils.registerReceiver(mReceiver, filter);
        }
    }

    private void unregisterBleNotifyReceiver() {
        if (mReceiver != null) {
            BluetoothUtils.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    protected void readFirmwareVersionFromDevice(final byte[] token, final int code) {
        if (!checkFirmwareVersionAccess()) {
            dispatchResult(code);
            return;
        }

        BluetoothLog.d("readFirmwareVersionFromDevice: ");

        ClientManager.getClient().read(mMac, BluetoothConstants.MISERVICE,
                BluetoothConstants.CHARACTER_FIRMWARE_VERSION, new BleReadResponse() {
                    @Override
                    public void onResponse(int code, byte[] data) {
                        if (code == REQUEST_SUCCESS && data != null) {
                            data = BLECipher.encrypt(token, data);
                            data = ByteUtils.cutAfterBytes(data, (byte) 0);

                            String version = new String(data);
                            BluetoothLog.w("firmWare version " + version);

                            mBundle.putString(EXTRA_VERSION, version);
                        }

                        dispatchResult(code);
                    }
                });
    }

    protected void dispatchResultInMainThread(int code) {
        boolean success = (code == REQUEST_SUCCESS);

        closeTokenNotify();

        if (!success) {
            disconnect();
        }

        mHandler.removeCallbacksAndMessages(null);

        mResponse = null;
    }

    protected void dispatchResult(final int code) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                dispatchResultInMainThread(code);
            }
        });
    }

    /**
     * 看设备是否支持云端绑定，为了兼容老设备
     * @return
     */
    public boolean checkBindAbility() {
        return mNeedBindToServer;
    }

    /**
     * 老固件是不支持读固件版本的
     * @return
     */
    private boolean checkFirmwareVersionAccess() {
        return checkBindAbility();
    }

    private class ConnectReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }

            String action = intent.getAction();

//            BluetoothLog.d("onReceive: " + action);

            if (ACTION_CHARACTER_CHANGED.equalsIgnoreCase(action)) {
                String mac = intent.getStringExtra(EXTRA_MAC);

                if (mMac.equalsIgnoreCase(mac)) {
                    UUID service = (UUID) intent.getSerializableExtra(EXTRA_SERVICE_UUID);
                    UUID character = (UUID) intent.getSerializableExtra(EXTRA_CHARACTER_UUID);
                    byte[] value = intent.getByteArrayExtra(EXTRA_BYTE_VALUE);

                    if (service != null && character != null) {
                        processNotify(service, character, value);
                    }
                }
            } else if (ACTION_CONNECT_STATUS_CHANGED.equalsIgnoreCase(action)) {
                String mac = intent.getStringExtra(EXTRA_MAC);

                if (mMac.equalsIgnoreCase(mac)) {
                    int status = intent.getIntExtra(EXTRA_STATUS, 0);

                    if (status == STATUS_DISCONNECTED) {
                        dispatchResult(REQUEST_FAILED);
                    }
                }
            }
        }
    }

    protected final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            processHandlerMessage(msg);
        }
    };

    protected byte[] getGeneratedToken() {
        return null;
    }

    protected abstract void processStep1();

    protected abstract void processHandlerMessage(Message msg);

    protected abstract void processNotify(UUID service, UUID character, byte[] value);
}
