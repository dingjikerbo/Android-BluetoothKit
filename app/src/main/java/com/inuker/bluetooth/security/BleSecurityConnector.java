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
import com.inuker.bluetooth.library.IBluetoothConstants;
import com.inuker.bluetooth.library.connect.response.BluetoothResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.xiaomi.smarthome.device.bluetooth.security.BLECipher;

import java.util.UUID;

/**
 * Created by liwentian on 2016/2/22.
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

    private final BluetoothResponse mBleConnectResponse = new BluetoothResponse() {
        @Override
        public void onResponse(int code, Bundle data) {
            if (code == REQUEST_SUCCESS) {
                if (data != null) {
                    mBundle.putAll(data);
                }
                mNeedBindToServer = checkNeedBindToServer(data);
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
    protected boolean checkNeedBindToServer(Bundle bundle) {
        return true;
    }

    protected void openTokenNotify(BluetoothResponse response) {
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
            IntentFilter filter = new IntentFilter(IBluetoothConstants.ACTION_CHARACTER_CHANGED);
            filter.addAction(IBluetoothConstants.ACTION_CONNECT_STATUS_CHANGED);
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
                BluetoothConstants.CHARACTER_FIRMWARE_VERSION, new BluetoothResponse() {
                    @Override
                    public void onResponse(int code, Bundle bundle) {
                        if (code == REQUEST_SUCCESS && bundle != null) {
                            byte[] data = bundle.getByteArray(EXTRA_BYTE_VALUE);

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
        boolean success = (code == IBluetoothConstants.REQUEST_SUCCESS);

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

    /**
     * 老固件在token不匹配时是不会notify的，只会超时
     * @return
     */
    protected boolean willNotifyTokenNotMatch() {
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

            if (IBluetoothConstants.ACTION_CHARACTER_CHANGED.equalsIgnoreCase(action)) {
                String mac = intent.getStringExtra(IBluetoothConstants.EXTRA_MAC);

                if (mMac.equalsIgnoreCase(mac)) {
                    UUID service = (UUID) intent.getSerializableExtra(IBluetoothConstants.EXTRA_SERVICE_UUID);
                    UUID character = (UUID) intent.getSerializableExtra(IBluetoothConstants.EXTRA_CHARACTER_UUID);
                    byte[] value = intent.getByteArrayExtra(IBluetoothConstants.EXTRA_BYTE_VALUE);

                    if (service != null && character != null) {
                        processNotify(service, character, value);
                    }
                }
            } else if (IBluetoothConstants.ACTION_CONNECT_STATUS_CHANGED.equalsIgnoreCase(action)) {
                String mac = intent.getStringExtra(IBluetoothConstants.EXTRA_MAC);

                if (mMac.equalsIgnoreCase(mac)) {
                    int status = intent.getIntExtra(IBluetoothConstants.EXTRA_STATUS, 0);

                    if (status == IBluetoothConstants.STATUS_DISCONNECTED) {
                        dispatchResult(IBluetoothConstants.REQUEST_FAILED);
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
