package com.inuker.bluetooth.library;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

import com.inuker.bluetooth.library.connect.BleConnectManager;
import com.inuker.bluetooth.library.connect.options.BleConnectOption;
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse;
import com.inuker.bluetooth.library.search.BluetoothSearchManager;
import com.inuker.bluetooth.library.search.SearchRequest;
import static com.inuker.bluetooth.library.Constants.*;

import java.util.UUID;

/**
 * Created by dingjikerbo on 2015/10/29.
 */
public class BluetoothServiceImpl extends IBluetoothService.Stub implements Handler.Callback {

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
    public void callBluetoothApi(int code, Bundle args, final IResponse response) throws RemoteException {
        Message msg = mHandler.obtainMessage(code, new BleGeneralResponse() {

            @Override
            public void onResponse(int code, Bundle data) {
                if (response != null) {
                    if (data == null) {
                        data = new Bundle();
                    }
                    try {
                        response.onResponse(code, data);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        args.setClassLoader(getClass().getClassLoader());
        msg.setData(args);
        msg.sendToTarget();
    }

    @Override
    public boolean handleMessage(Message msg) {
        Bundle args = msg.getData();
        String mac = args.getString(EXTRA_MAC);
        UUID service = (UUID) args.getSerializable(EXTRA_SERVICE_UUID);
        UUID character = (UUID) args.getSerializable(EXTRA_CHARACTER_UUID);
        byte[] value = args.getByteArray(EXTRA_BYTE_VALUE);
        BleGeneralResponse response = (BleGeneralResponse) msg.obj;

        switch (msg.what) {
            case CODE_CONNECT:
                BleConnectOption options = args.getParcelable(EXTRA_OPTIONS);
                BleConnectManager.connect(mac, options, response);
                break;

            case CODE_DISCONNECT:
                BleConnectManager.disconnect(mac);
                break;

            case CODE_REFRESH:
                BleConnectManager.refresh(mac);
                break;

            case CODE_READ:
                BleConnectManager.read(mac, service, character, response);
                break;

            case CODE_WRITE:
                BleConnectManager.write(mac, service, character, value, response);
                break;

            case CODE_WRITE_NORSP:
                BleConnectManager.writeNoRsp(mac, service, character, value, response);
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

            case CODE_SEARCH:
                SearchRequest request = args.getParcelable(EXTRA_REQUEST);
                BluetoothSearchManager.search(request, response);
                break;

            case CODE_STOP_SESARCH:
                BluetoothSearchManager.stopSearch();
                break;

            case CODE_INDICATE:
                BleConnectManager.indicate(mac, service, character, response);
                break;
        }
        return true;
    }
}
