package com.inuker.bluetooth.library.utils.hook;

import android.os.IBinder;

import com.inuker.bluetooth.library.utils.hook.compat.ServiceManagerCompat;
import com.inuker.bluetooth.library.utils.hook.utils.HookUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

/**
 * Created by dingjikerbo on 2016/8/27.
 */
public class BluetoothHooker {

    private static final String BLUETOOTH_MANAGER = "bluetooth_manager";

    public static void hook() {
        Method getService = ServiceManagerCompat.getService();
        IBinder iBinder = HookUtils.invoke(getService, null, BLUETOOTH_MANAGER);

        IBinder proxy = (IBinder) Proxy.newProxyInstance(iBinder.getClass().getClassLoader(),
                new Class<?>[]{IBinder.class},
                new BluetoothManagerBinderProxyHandler(iBinder));

        HashMap<String, IBinder> cache = ServiceManagerCompat.getCacheValue();
        cache.put(BLUETOOTH_MANAGER, proxy);
    }
}
