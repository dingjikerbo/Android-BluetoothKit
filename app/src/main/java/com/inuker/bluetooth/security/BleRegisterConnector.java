package com.inuker.bluetooth.security;

import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;

import com.inuker.bluetooth.BluetoothConstants;
import com.inuker.bluetooth.ClientManager;
import com.inuker.bluetooth.MD5;
import com.inuker.bluetooth.library.IBluetoothConstants;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.connect.response.BluetoothResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.xiaomi.smarthome.device.bluetooth.security.BLECipher;

import java.util.UUID;

/**
 * Created by dingjikerbo on 2016/2/22.
 * 一、APP和设备建立连接
 * 二、APP向设备的0010写入DE85CA90
 * 三、APP打开0001的notify
 * 四、APP生成token，用(mac, productId)生成的key加密后写到设备的0001
 * 五、等待设备的notify
 * 六、APP收到设备的notify，并解密出token，和本地对比，如果验证通过，则用token加密FA54AB92并写到0001
 */
public class BleRegisterConnector extends BleSecurityConnector {

    private static final int MSG_NOTIFY_TIMEOUT = 0x1;

    private static final int SESSION_START = 0xDE85CA90;
    private static final int SESSION_END = 0xFA54AB92;

    private byte[] mTempToken;

    public BleRegisterConnector(String mac, int productId) {
        super(mac, productId);
    }

    @Override
    protected void processStep1() {
        BluetoothLog.d("process step 1 ...");

        ClientManager.getClient().write(mMac, BluetoothConstants.MISERVICE,
                BluetoothConstants.CHARACTER_EVENT, ByteUtils.fromInt(SESSION_START), new BleWriteResponse() {

                    @Override
                    public void onResponse(int code) {
                        BluetoothLog.v(String.format("step 1 onResponse: code = %d", code));

                        if (code == REQUEST_SUCCESS) {
                            processStep2();
                        } else {
                            dispatchResult(REQUEST_FAILED);
                        }
                    }
                });


    }

    private void processStep2() {
        BluetoothLog.d("process step 2 ...");

        openTokenNotify(mBleNotifyResponse);
    }

    private final BleNotifyResponse mBleNotifyResponse = new BleNotifyResponse() {
        @Override
        public void onNotify(UUID service, UUID character, byte[] value) {
            BluetoothLog.v(String.format("onNotify service = %s, character = %s, value = %s",
                    service, character, ByteUtils.byteToString(value)));
        }

        @Override
        public void onResponse(int code) {
            BluetoothLog.d("step 2 onResponse: " + code);

            if (code == REQUEST_SUCCESS) {
                processStep3();
            } else {
                dispatchResult(REQUEST_FAILED);
            }
        }
    };

    @Override
    protected void processNotify(UUID service, UUID character, byte[] data) {
        if (service.equals(BluetoothConstants.MISERVICE) && character.equals(BluetoothConstants.CHARACTER_TOKEN)) {
            if (mHandler.hasMessages(MSG_NOTIFY_TIMEOUT)) {
                mHandler.removeMessages(MSG_NOTIFY_TIMEOUT);

                byte[] key1 = BLECipher.mixA(mMac, mProductId);
                byte[] key2 = BLECipher.mixB(mMac, mProductId);

                byte[] token = BLECipher.encrypt(key2,
                        BLECipher.encrypt(key1, data));

                if (ByteUtils.byteEquals(token, mTempToken)) {
                    processStep4();
                } else {
                    BluetoothLog.w("token not match");
                    dispatchResult(IBluetoothConstants.REQUEST_FAILED);
                }
            }
        }
    }

    private byte[] generateToken() {
        long now = System.currentTimeMillis();
        String original = String.format("token.%d.%f", now, 0.23f);
        return MD5.MD5_12(original);
    }

    private void processStep3() {
        BluetoothLog.d("process step 3");

        mTempToken = generateToken();

        BluetoothLog.w(String.format("Generated Token: %s", ByteUtils.byteToString(mTempToken)));

        byte[] key1 = BLECipher.mixA(mMac, mProductId);

        byte[] t1 = BLECipher.encrypt(key1, mTempToken);

        ClientManager.getClient().write(mMac, BluetoothConstants.MISERVICE,
                BluetoothConstants.CHARACTER_TOKEN, t1, null);

        mHandler.removeMessages(MSG_NOTIFY_TIMEOUT);
        mHandler.sendEmptyMessageDelayed(
                MSG_NOTIFY_TIMEOUT, 15000);
    }

    private void processStep4() {
        BluetoothLog.d("process step 4 ...");

        byte[] flag = ByteUtils.fromInt(SESSION_END);
        byte[] bytes = BLECipher.encrypt(mTempToken, flag);

        ClientManager.getClient().write(mMac, BluetoothConstants.MISERVICE,
                BluetoothConstants.CHARACTER_TOKEN, bytes,
                new BleWriteResponse() {

                    @Override
                    public void onResponse(int code) {
                        BluetoothLog.d("step 4 onResponse: " + code);

                        if (code == REQUEST_SUCCESS) {
                            readFirmwareVersionFromDevice(getGeneratedToken(), code);
                        } else {
                            dispatchResult(code);
                        }
                    }

                });
    }

    @Override
    protected void processHandlerMessage(Message msg) {
        switch (msg.what) {
            case MSG_NOTIFY_TIMEOUT:
                BluetoothLog.w("notify timeout");
                dispatchResult(IBluetoothConstants.REQUEST_FAILED);
                break;
        }
    }

    @Override
    protected byte[] getGeneratedToken() {
        return mTempToken;
    }
}