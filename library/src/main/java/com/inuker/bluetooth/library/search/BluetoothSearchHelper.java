package com.inuker.bluetooth.library.search;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;

import com.inuker.bluetooth.library.search.response.BluetoothSearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.inuker.bluetooth.library.utils.proxy.ProxyBulk;
import com.inuker.bluetooth.library.utils.proxy.ProxyInterceptor;
import com.inuker.bluetooth.library.utils.proxy.ProxyUtils;

import java.lang.reflect.Method;

/**
 * Created by dingjikerbo on 2016/8/28.
 */
public class BluetoothSearchHelper implements IBluetoothSearchHelper, ProxyInterceptor, Callback {

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
                    sInstance = ProxyUtils.getProxy(helper, IBluetoothSearchHelper.class, helper);
                }
            }
        }
        return sInstance;
    }

    @Override
    public void startSearch(BluetoothSearchRequest request, BluetoothSearchResponse response) {
        request.setSearchResponse(new BluetoothSearchResponseImpl(response));

        if (!BluetoothUtils.isBluetoothEnabled()) {
            request.cancel();
        } else {
            stopSearch();

            if (mCurrentRequest == null) {
                mCurrentRequest = request;
                mCurrentRequest.start();
            }
        }
    }

    private class BluetoothSearchResponseImpl implements BluetoothSearchResponse {

        BluetoothSearchResponse response;

        BluetoothSearchResponseImpl(BluetoothSearchResponse response) {
            this.response = response;
        }

        @Override
        public void onSearchStarted() {
            response.onSearchStarted();
        }

        @Override
        public void onDeviceFounded(SearchResult device) {
            response.onDeviceFounded(device);
        }

        @Override
        public void onSearchStopped() {
            response.onSearchStopped();
            mCurrentRequest = null;
        }

        @Override
        public void onSearchCanceled() {
            response.onSearchCanceled();
            mCurrentRequest = null;
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
    public boolean onIntercept(Object object, Method method, Object[] args) {
        mHandler.obtainMessage(0, new ProxyBulk(object, method,args)).sendToTarget();
        return true;
    }

    @Override
    public boolean handleMessage(Message msg) {
        ProxyBulk.safeInvoke(msg.obj);
        return true;
    }
}
