package com.inuker.bluetooth;

import android.widget.Toast;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.BluetoothContext;
import com.inuker.bluetooth.library.Code;
import com.inuker.bluetooth.library.beacon.BeaconParser;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.utils.ByteUtils;

import java.util.MissingResourceException;
import java.util.UUID;

/**
 * Created by dingjikerbo on 17/1/23.
 */

public class WaveGenerator implements IWaveGenerator {

    private String mMac;

    private BluetoothClient mClient;

    private WaveResponse mResponse;

    WaveGenerator(String mac) {
        mMac = mac;
        mClient = ClientManager.getClient();
    }

    @Override
    public void start(WaveResponse response) {
        mResponse = response;
        connect();
    }

    @Override
    public void stop() {
        mClient.disconnect(mMac);
    }

    private void connect() {
        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3)
                .setConnectTimeout(20000)
                .setServiceDiscoverRetry(3)
                .setServiceDiscoverTimeout(10000)
                .build();

        mClient.connect(mMac, options, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile data) {
                if (code == Code.REQUEST_SUCCESS) {
                    Toast.makeText(MyApplication.getInstance(), "connect success", Toast.LENGTH_SHORT).show();
                    openNotify();
                } else {
                    Toast.makeText(MyApplication.getInstance(), "connect failed", Toast.LENGTH_SHORT).show();
                    onWaveFailed();
                }
            }
        });
    }

    private void openNotify() {
        mClient.notify(mMac, SERVICE, CHARACTER, new BleNotifyResponse() {
            @Override
            public void onNotify(UUID service, UUID character, byte[] value) {
                processNotify(value);
            }

            @Override
            public void onResponse(int code) {
                if (code != Code.REQUEST_SUCCESS) {
                    onWaveFailed();
                    Toast.makeText(MyApplication.getInstance(), "open notify failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MyApplication.getInstance(), "open notify success", Toast.LENGTH_SHORT).show();
                    writeMode();
                }
            }
        });
    }

    private void writeMode() {
        mClient.write(mMac, SERVICE, CHARACTER, ByteUtils.fromInt(MODE), new BleWriteResponse() {
            @Override
            public void onResponse(int code) {
                if (code != Code.REQUEST_SUCCESS) {
                    onWaveFailed();
                    Toast.makeText(MyApplication.getInstance(), "Write mode failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MyApplication.getInstance(), "Write mode success", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void onWaveFailed() {
        if (mResponse != null) {
            mResponse.onFail();
        }
    }

    private void processNotify(byte[] value) {
        BeaconParser parser = new BeaconParser(value);
        int mode = parser.readByte();
        int len = parser.readByte();
        int x = parser.readShort();
        int y = parser.readShort();
        int z = parser.readShort();
        mResponse.onRaw(x, y, z);
    }
}
