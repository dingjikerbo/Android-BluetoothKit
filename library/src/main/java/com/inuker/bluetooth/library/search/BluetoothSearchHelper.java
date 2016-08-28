package com.inuker.bluetooth.library.search;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.inuker.bluetooth.library.utils.ProxyUtils;
import com.inuker.bluetooth.library.utils.ProxyUtils.ProxyBulk;
import com.inuker.bluetooth.library.utils.ProxyUtils.ProxyHandler;

import java.lang.reflect.Method;

/**
 * Created by dingjikerbo on 2016/8/28.
 */
public class BluetoothSearchHelper implements IBluetoothSearchHelper, ProxyHandler, Handler.Callback {

    private BluetoothSearchRequest mCurrentRequest;

    private static IBluetoothSearchHelper sInstance;

    private Handler mHandler;

    private BluetoothSearchHelper() {
        mHandler = new Handler(Looper.getMainLooper(), this);
    }

    public static IBluetoothSearchHelper getInstance() {
        if (sInstance == null) {
            synchronized (BluetoothSearchHelper.class) {
                if (sInstance == null) {
                    BluetoothSearchHelper helper = new BluetoothSearchHelper();
                    sInstance = ProxyUtils.newProxyInstance(helper, helper);
                }
            }
        }
        return sInstance;
    }

    @Override
    public void startSearch(BluetoothSearchRequest request, BluetoothSearchResponse response) {
        request.setSearchResponse(response);

        if (!BluetoothUtils.isBluetoothEnabled()) {
            request.cancel();
        } else {
            stopSearch();
            mCurrentRequest = request;
            mCurrentRequest.start();
        }
    }

    @Override
    public void stopSearch() {
        if (mCurrentRequest != null) {
            mCurrentRequest.cancel();
            mCurrentRequest = null;
        }
    }

    @Override
    public boolean onPreCalled(Object object, Method method, Object[] args) {
        mHandler.obtainMessage(0, new ProxyBulk(object, method,args)).sendToTarget();
        return false;
    }

    @Override
    public boolean handleMessage(Message msg) {
        ProxyBulk.safeInvoke(msg.obj);
        return true;
    }
}
