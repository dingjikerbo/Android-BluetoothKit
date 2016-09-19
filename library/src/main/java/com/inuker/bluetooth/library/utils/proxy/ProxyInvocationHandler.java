package com.inuker.bluetooth.library.utils.proxy;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;

import com.inuker.bluetooth.library.utils.BluetoothLog;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by dingjikerbo on 2016/9/18.
 */
public class ProxyInvocationHandler implements InvocationHandler, ProxyInterceptor, Callback {

    private Object subject;

    private ProxyInterceptor interceptor;

    private boolean weakRef;

    private boolean postUI;

    private Handler handler;

    public ProxyInvocationHandler(Object subject) {
        this(subject, null);
    }

    public ProxyInvocationHandler(Object subject, ProxyInterceptor interceptor) {
        this(subject, interceptor, false);
    }

    public ProxyInvocationHandler(Object subject, ProxyInterceptor interceptor, boolean weakRef) {
        this(subject, interceptor, weakRef, false);
    }

    public ProxyInvocationHandler(Object subject, ProxyInterceptor interceptor, boolean weakRef, boolean postUI) {
        this.weakRef = weakRef;
        this.interceptor = interceptor;
        this.postUI = postUI;
        this.subject = getObject(subject);
        handler = new Handler(Looper.getMainLooper(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object subject = getObject();

        if (!onIntercept(subject, method, args)) {
            ProxyBulk bulk = new ProxyBulk(subject, method, args);
            return postUI ? postSafeInvoke(bulk) : safeInvoke(bulk);
        }

        return null;
    }

    @Override
    public boolean onIntercept(Object object, Method method, Object[] args) {
        if (interceptor != null) {
            try {
                return interceptor.onIntercept(object, method, args);
            } catch (Exception e) {
                BluetoothLog.e(e);
            }
        }
        return false;
    }

    private Object getObject(Object object) {
        return weakRef ? new WeakReference<Object>(object) : object;
    }

    @SuppressWarnings("unchecked")
    private Object getObject() {
        if (weakRef) {
            return ((WeakReference<Object>) subject).get();
        } else {
            return subject;
        }
    }

    private Object postSafeInvoke(ProxyBulk bulk) {
        handler.obtainMessage(0, bulk).sendToTarget();
        return null;
    }

    private Object safeInvoke(ProxyBulk bulk) {
        try {
            return bulk.safeInvoke();
        } catch (Throwable e) {
        }
        return null;
    }

    @Override
    public boolean handleMessage(Message msg) {
        // TODO Auto-generated method stub
        ProxyBulk.safeInvoke(msg.obj);
        return true;
    }
}
