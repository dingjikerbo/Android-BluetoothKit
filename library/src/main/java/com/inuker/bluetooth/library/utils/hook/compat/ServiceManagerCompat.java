package com.inuker.bluetooth.library.utils.hook.compat;

import android.os.IBinder;

import com.inuker.bluetooth.library.utils.hook.utils.HookUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by dingjikerbo on 2016/8/27.
 */
public class ServiceManagerCompat {

    private static Class<?> serviceManager;
    private static Field sCache;
    private static Method getService;

    static {
        serviceManager = HookUtils.getClass("android.os.ServiceManager");

        sCache = HookUtils.getField(serviceManager, "sCache");
        sCache.setAccessible(true);

        getService = HookUtils.getMethod(serviceManager, "getService", String.class);
    }

    public static Class<?> getServiceManager() {
        return serviceManager;
    }

    public static Field getCacheField() {
        return sCache;
    }

    public static HashMap<String, IBinder> getCacheValue() {
        return HookUtils.getValue(sCache);
    }

    public static Method getService() {
        return getService;
    }
}
