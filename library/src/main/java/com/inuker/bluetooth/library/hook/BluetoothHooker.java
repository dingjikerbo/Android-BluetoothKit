package com.inuker.bluetooth.library.hook;

import android.app.Service;
import android.os.IBinder;
import android.os.IInterface;

import com.inuker.bluetooth.library.hook.compat.ServiceManagerCompat;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.HookUtils;
import com.inuker.bluetooth.library.utils.ProxyUtils;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by liwentian on 2016/8/27.
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

    private static class BluetoothGattProxyHandler implements InvocationHandler {

        private Object bluetoothGatt;

        BluetoothGattProxyHandler(Object bluetoothGatt) {
            this.bluetoothGatt = bluetoothGatt;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            BluetoothLog.v(String.format("IBluetoothGatt method: %s", method.getName()));
            return method.invoke(bluetoothGatt, args);
        }
    }

    private static class BluetoothManagerProxyHandler implements InvocationHandler {

        private Object iBluetoothManager;

        private Class<?> bluetoothGattClaz;
        private Object bluetoothGatt;

        BluetoothManagerProxyHandler(Object iBluetoothManager) {
            this.iBluetoothManager = iBluetoothManager;

            this.bluetoothGattClaz = HookUtils.getClass("android.bluetooth.IBluetoothGatt");
            Class<?> stub = HookUtils.getClass("android.bluetooth.IBluetoothManager");
            Method method = HookUtils.getMethod(stub, "getBluetoothGatt");
            this.bluetoothGatt = HookUtils.invoke(method, iBluetoothManager);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            BluetoothLog.v(String.format("IBluetoothManager method: %s", method.getName()));

            if ("getBluetoothGatt".equals(method.getName())) {
                return Proxy.newProxyInstance(proxy.getClass().getClassLoader(),
                        new Class<?>[] {IBinder.class, IInterface.class, bluetoothGattClaz},
                        new BluetoothGattProxyHandler(bluetoothGatt));
            }
            return method.invoke(iBluetoothManager, args);
        }
    }

    private static class BluetoothManagerBinderProxyHandler implements InvocationHandler {

        private  IBinder iBinder;

        private Class<?> iBluetoothManagerClaz;
        private Object iBluetoothManager;

        BluetoothManagerBinderProxyHandler(IBinder iBinder) {
            this.iBinder = iBinder;

            this.iBluetoothManagerClaz = HookUtils.getClass("android.bluetooth.IBluetoothManager");
            Class<?> stub = HookUtils.getClass("android.bluetooth.IBluetoothManager$Stub");
            Method asInterface = HookUtils.getMethod(stub, "asInterface", IBinder.class);
            this.iBluetoothManager = HookUtils.invoke(asInterface, null, iBinder);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            BluetoothLog.v(String.format("IBinder method: %s", method.getName()));

            if ("queryLocalInterface".equals(method.getName())) {
                return Proxy.newProxyInstance(proxy.getClass().getClassLoader(),
                        new Class<?>[] {IBinder.class, IInterface.class, iBluetoothManagerClaz},
                        new BluetoothManagerProxyHandler(iBluetoothManager));
            }
            return method.invoke(iBinder, args);
        }
    }
}
