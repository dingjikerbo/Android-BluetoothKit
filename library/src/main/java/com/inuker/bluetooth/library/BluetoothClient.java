package com.inuker.bluetooth.library;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import com.inuker.bluetooth.library.connect.IBluetoothApi;
import com.inuker.bluetooth.library.connect.response.BleResponse;
import com.inuker.bluetooth.library.utils.ProxyUtils;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * Created by dingjikerbo on 16/4/8.
 */
public class BluetoothClient implements IBluetoothApi, ProxyUtils.ProxyHandler {

    private static final String TAG = BluetoothClient.class.getSimpleName();

    private Context mContext;

    private IBluetoothService mBluetoothService;

    private static IBluetoothApi sInstance;

    private CountDownLatch mCountDownLatch;

    private HandlerThread mWorkerThread;
    private Handler mWorkerHandler;

    private BluetoothClient(Context context) {
        mContext = context.getApplicationContext();

        mWorkerThread = new HandlerThread(TAG);
        mWorkerThread.start();
        mWorkerHandler = new Handler(mWorkerThread.getLooper());
    }

    public static IBluetoothApi getInstance(Context context) {
        if (sInstance == null) {
            synchronized (BluetoothClient.class) {
                if (sInstance == null) {
                    BluetoothClient client = new BluetoothClient(context);
                    sInstance = ProxyUtils.newProxyInstance(client, IBluetoothApi.class, client);
                }
            }
        }
        return sInstance;
    }

    private IBluetoothService getBluetoothService() {
        if (mBluetoothService == null) {
            bindServiceSync();
        }
        return mBluetoothService;
    }

    private void bindServiceSync() {
        Intent intent = new Intent();
        intent.setClass(mContext, BluetoothService.class);
        mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        waitBluetoothManagerReady();
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothService = IBluetoothService.Stub.asInterface(service);
            notifyBluetoothManagerReady();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothService = null;
        }
    };

    @Override
    public void connect(String mac, BleResponse response) {
        Bundle args = new Bundle();
        args.putString(BluetoothConstants.EXTRA_MAC, mac);
        safeCallBluetoothApi(BluetoothConstants.CODE_CONNECT, args, response);
    }

    @Override
    public void disconnect(String mac) {
        Bundle args = new Bundle();
        args.putString(BluetoothConstants.EXTRA_MAC, mac);
        safeCallBluetoothApi(BluetoothConstants.CODE_DISCONNECT, args, null);
    }

    @Override
    public void read(String mac, UUID service, UUID character, BleResponse response) {
        Bundle args = new Bundle();
        args.putString(BluetoothConstants.EXTRA_MAC, mac);
        args.putSerializable(BluetoothConstants.EXTRA_SERVICE_UUID, service);
        args.putSerializable(BluetoothConstants.EXTRA_CHARACTER_UUID, character);
        safeCallBluetoothApi(BluetoothConstants.CODE_READ, args, response);
    }

    @Override
    public void write(String mac, UUID service, UUID character, byte[] value, BleResponse response) {
        Bundle args = new Bundle();
        args.putString(BluetoothConstants.EXTRA_MAC, mac);
        args.putSerializable(BluetoothConstants.EXTRA_SERVICE_UUID, service);
        args.putSerializable(BluetoothConstants.EXTRA_CHARACTER_UUID, character);
        args.putByteArray(BluetoothConstants.EXTRA_BYTE_VALUE, value);
        safeCallBluetoothApi(BluetoothConstants.CODE_WRITE, args, response);
    }

    @Override
    public void notify(String mac, UUID service, UUID character, BleResponse response) {
        Bundle args = new Bundle();
        args.putString(BluetoothConstants.EXTRA_MAC, mac);
        args.putSerializable(BluetoothConstants.EXTRA_SERVICE_UUID, service);
        args.putSerializable(BluetoothConstants.EXTRA_CHARACTER_UUID, character);
        safeCallBluetoothApi(BluetoothConstants.CODE_NOTIFY, args, response);
    }

    @Override
    public void unnotify(String mac, UUID service, UUID character) {
        Bundle args = new Bundle();
        args.putString(BluetoothConstants.EXTRA_MAC, mac);
        args.putSerializable(BluetoothConstants.EXTRA_SERVICE_UUID, service);
        args.putSerializable(BluetoothConstants.EXTRA_CHARACTER_UUID, character);
        safeCallBluetoothApi(BluetoothConstants.CODE_UNNOTIFY, args, null);
    }

    @Override
    public void readRssi(String mac, BleResponse response) {
        Bundle args = new Bundle();
        args.putString(BluetoothConstants.EXTRA_MAC, mac);
        safeCallBluetoothApi(BluetoothConstants.CODE_READ_RSSI, args, response);
    }

    private void safeCallBluetoothApi(int code, Bundle args, BleResponse response) {
        try {
            IBluetoothService service = getBluetoothService();
            if (service != null) {
                service.callBluetoothApi(code, args, response);
            } else {
                response.onResponse(Code.SERVICE_EXCEPTION, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onPreCalled(final Object object, final Method method, final Object[] args) {
        mWorkerHandler.post(new Runnable() {

            @Override
            public void run() {
                try {
                    Log.i("bush", "onPreCalled " + method.getName());
                    method.invoke(object, args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return false;
    }

    private void notifyBluetoothManagerReady() {
        if (mCountDownLatch != null) {
            mCountDownLatch.countDown();
            mCountDownLatch = null;
        }
    }

    private void waitBluetoothManagerReady() {
        mCountDownLatch = new CountDownLatch(1);

        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
