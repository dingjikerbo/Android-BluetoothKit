package com.inuker.bluetooth.library.utils.proxy;

import com.inuker.bluetooth.library.utils.BluetoothLog;

import java.lang.reflect.Method;

/**
 * Created by dingjikerbo on 2016/9/18.
 */
public class ProxyBulk {

    public Object object;
    public Method method;
    public Object[] args;

    public ProxyBulk(Object object, Method method, Object[] args) {
        this.object = object;
        this.method = method;
        this.args = args;
    }

    public Object safeInvoke() {
        Object result = null;
        try {
            result = method.invoke(object, args);
        } catch (Throwable e) {
            BluetoothLog.e(e);
        }
        return result;
    }

    public static Object safeInvoke(Object obj) {
        return ((ProxyBulk) obj).safeInvoke();
    }
}
