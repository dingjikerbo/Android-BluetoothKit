package com.inuker.bluetooth.library.utils.proxy;

import java.lang.reflect.Proxy;

/**
 * Created by dingjikerbo on 2016/8/23.
 */
public class ProxyUtils {

    public static <T> T getProxy(Object object, Class<?> clazz, ProxyInterceptor handler, boolean weakRef, boolean postUI) {
        return (T) Proxy.newProxyInstance(object.getClass().getClassLoader(),
                new Class<?>[] { clazz },
                new ProxyInvocationHandler(object, handler, weakRef, postUI));
    }

    public static <T> T getProxy(Object object, Class<?>[] interfaces, ProxyInterceptor handler, boolean weakRef, boolean postUI) {
        return (T) Proxy.newProxyInstance(object.getClass().getClassLoader(),
                interfaces,
                new ProxyInvocationHandler(object, handler, weakRef, postUI));
    }

    public static <T> T getProxy(Object object, Class<?> clazz, ProxyInterceptor interceptor) {
        return (T) getProxy(object, clazz, interceptor, false, false);
    }

    public static <T> T getWeakUIProxy(Object object) {
//        return (T) getProxy(object, object.getClass().getInterfaces(), null, true, true);

        return (T) Proxy.newProxyInstance(object.getClass().getClassLoader(),
                object.getClass().getInterfaces(),
                new ProxyInvocationHandler(object, null, true, true));
    }

    public static <T> T getWeakUIProxy(Object object, Class<?> clazz) {
        return (T) getProxy(object, clazz, null, true, true);
    }
}
