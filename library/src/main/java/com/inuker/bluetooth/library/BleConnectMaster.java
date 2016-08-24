package com.inuker.bluetooth.library;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.inuker.bluetooth.library.utils.ProxyUtils;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by liwentian on 2016/8/24.
 */
public class BleConnectMaster implements IBleConnectMaster {

    private static final int CHECK_ALIVE_LIMIT = 60000;
    private static final int CHECK_ALIVE_CYCLE = 15000;

    private static final int MSG_CHECK_ALIVE = 0x100;

    private HandlerThread mThread;
    private Handler mMainHandler;
    private Handler mHandler;

    private String mAddress;
    private BleConnectDispatcher mBleConnectDispatcher;

    private long mTimeStamp;

    private BleConnectMaster(String mac) {
        mAddress = mac;
        mMainHandler = new Handler(Looper.getMainLooper());
    }

    static IBleConnectMaster newInstance(String mac) {
        assertCalledInMainThread();
        return ProxyUtils.newProxyInstance(new BleConnectMaster(mac), new ProxyUtils.ProxyHandler() {
            @Override
            public boolean onPreCalled(final Object object, final Method method, final Object[] args) {
                assertCalledInMainThread();
                final BleConnectMaster master = (BleConnectMaster) object;
                master.updateTimeStamp(System.currentTimeMillis());
                master.reportToObserver();
                master.getHandler().post(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            master.initDispatcherIfNeeded();
                            method.invoke(object, args);
                        } catch (Exception e) {
                            BluetoothLog.w(e);
                        }
                    }
                });
                return false;
            }
        });
    }

    private static void assertCalledInMainThread() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException("");
        }
    }

    /**
     * 工作线程里初始化Dispatcher和Worker
     * 所有Dispatcher引用只会在工作线程中，故无需同步
     */
    private void initDispatcherIfNeeded() {
        if (mBleConnectDispatcher == null) {
            mBleConnectDispatcher = BleConnectDispatcher.newInstance(mAddress);
        }
    }

    /**
     * 主线程调用
     */
    private void stopMasterLooper() {
        mMainHandler.post(new Runnable() {

            @Override
            public void run() {
                assertCalledInMainThread();
                BluetoothLog.v(String.format("stopMasterLooper for %s", mAddress));

                if (mThread != null) {
                    mThread.quit();
                    mThread = null;
                    mHandler = null;
                    mBleConnectDispatcher = null;
                }
            }
        });
    }

    /**
     * 主线程调用
     */
    private void startMasterLooper() {
        assertCalledInMainThread();

        BluetoothLog.v(String.format("startMasterLooper for %s", mAddress));

        if (mThread == null) {
            mThread = new HandlerThread(String.format("BleConnectMaster(%s)", mAddress));
            mThread.start();

            mHandler = new Handler(mThread.getLooper()) {

                @Override
                public void handleMessage(Message msg) {
                    // TODO Auto-generated method stub
                    switch (msg.what) {
                        case MSG_CHECK_ALIVE:
                            checkAlive();
                            break;
                        default:
                            break;
                    }
                }
            };

            prepareCheckAlive();
        }
    }

    private void prepareCheckAlive() {
        mMainHandler.post(new Runnable() {

            @Override
            public void run() {
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(MSG_CHECK_ALIVE, CHECK_ALIVE_CYCLE);
                }
            }
        });
    }

    /**
     * 工作线程调用
     */
    private void checkAlive() {
        if (System.currentTimeMillis() - mTimeStamp > CHECK_ALIVE_LIMIT && !BluetoothUtils.isDeviceConnected(mAddress)) {
            stopMasterLooper();
        } else {
            prepareCheckAlive();
        }
    }

    @Override
    public void connect(BleResponse response) {
        mBleConnectDispatcher.connect(response);
    }

    @Override
    public void disconnect() {
        mBleConnectDispatcher.disconnect();
    }

    @Override
    public void read(UUID service, UUID character, BleResponse response) {
        mBleConnectDispatcher.read(service, character, response);
    }

    @Override
    public void write(UUID service, UUID character, byte[] bytes, BleResponse response) {
        mBleConnectDispatcher.write(service, character, bytes, response);
    }

    @Override
    public void notify(UUID service, UUID character, BleResponse response) {
        mBleConnectDispatcher.notify(service, character, response);
    }

    @Override
    public void unnotify(UUID service, UUID character) {
        mBleConnectDispatcher.unnotify(service, character);
    }

    @Override
    public void readRemoteRssi(BleResponse response) {
        mBleConnectDispatcher.readRemoteRssi(response);
    }

    private void updateTimeStamp(long timeInMillis) {
        mTimeStamp = timeInMillis;
    }

    /**
     * 主线程
     * @return
     */
    public Handler getHandler() {
        assertCalledInMainThread();

        if (mHandler == null) {
            startMasterLooper();
        }
        return mHandler;
    }

    private void reportToObserver() {
        BleConnectObserver.getInstance().reportAction(mAddress);
    }
}
