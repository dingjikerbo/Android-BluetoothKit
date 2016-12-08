package com.inuker.bluetooth.library.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.inuker.bluetooth.library.BluetoothContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothUtils {

    private static BluetoothManager mBluetoothManager;
    private static BluetoothAdapter mBluetoothLeAdapter;
    private static BluetoothAdapter mBluetoothClassicAdapter;

    private static Handler mHandler;

    public static Context getContext() {
        return BluetoothContext.get();
    }

    private static Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    public static void post(Runnable runnable) {
        getHandler().post(runnable);
    }

    public static void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        registerGlobalReceiver(receiver, filter);
    }

    private static void registerGlobalReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        getContext().registerReceiver(receiver, filter);
    }

    public static void unregisterReceiver(BroadcastReceiver receiver) {
        unregisterGlobalReceiver(receiver);
    }

    private static void unregisterGlobalReceiver(BroadcastReceiver receiver) {
        getContext().unregisterReceiver(receiver);
    }

    public static void sendBroadcast(Intent intent) {
        sendGlobalBroadcast(intent);
    }

    public static void sendBroadcast(String action) {
        sendGlobalBroadcast(new Intent(action));
    }

    private static void sendGlobalBroadcast(Intent intent) {
        getContext().sendBroadcast(intent);
    }

    public static boolean isBleSupported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
                && getContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public static boolean isBluetoothEnabled() {
        return getBluetoothState() == BluetoothAdapter.STATE_ON;
    }

    public static int getBluetoothState() {
        BluetoothAdapter adapter = getBluetoothClassicAdapter();
        return adapter != null ? adapter.getState() : 0;
    }

    public static boolean openBluetooth() {
        BluetoothAdapter adapter = getBluetoothClassicAdapter();
        if (adapter != null) {
            return adapter.enable();
        }
        return false;
    }

    public static boolean closeBluetooth() {
        BluetoothAdapter adapter = getBluetoothClassicAdapter();
        if (adapter != null) {
            return adapter.disable();
        }
        return false;
    }

    public static BluetoothManager getBluetoothManager() {
        if (isBleSupported()) {
            if (mBluetoothManager == null) {
                mBluetoothManager = (BluetoothManager) getContext()
                        .getSystemService(Context.BLUETOOTH_SERVICE);
            }
            return mBluetoothManager;
        }
        return null;
    }

    public static BluetoothAdapter getBluetoothLeAdapter() {
        if (mBluetoothLeAdapter == null) {
            BluetoothManager manager = getBluetoothManager();
            if (manager != null) {
                mBluetoothLeAdapter = manager.getAdapter();
            }
        }
        return mBluetoothLeAdapter;
    }

    public static BluetoothAdapter getBluetoothClassicAdapter() {
        if (mBluetoothClassicAdapter == null) {
            mBluetoothClassicAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        return mBluetoothClassicAdapter;
    }

    public static android.bluetooth.BluetoothDevice getRemoteDevice(String mac) {
        if (!TextUtils.isEmpty(mac)) {
            BluetoothAdapter adapter = getBluetoothLeAdapter();
            if (adapter != null) {
                return adapter.getRemoteDevice(mac);
            }
        }
        return null;
    }

    public static List<android.bluetooth.BluetoothDevice> getConnectedBluetoothLeDevices() {
        List<android.bluetooth.BluetoothDevice> devices = new ArrayList<android.bluetooth.BluetoothDevice>();

        BluetoothManager manager = getBluetoothManager();

        if (manager != null) {
            devices.addAll(manager.getConnectedDevices(BluetoothProfile.GATT));
        }

        return devices;
    }

    public static List<BluetoothDevice> getBondedBluetoothClassicDevices() {
        BluetoothAdapter adapter = getBluetoothClassicAdapter();
        List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
        if (adapter != null) {
            Set<BluetoothDevice> sets = adapter.getBondedDevices();
            if (sets != null) {
                devices.addAll(sets);
            }
        }
        return devices;
    }

    public static boolean isDeviceConnected(String mac) {
        if (!TextUtils.isEmpty(mac) && isBleSupported()) {
            android.bluetooth.BluetoothDevice device = getBluetoothLeAdapter().getRemoteDevice(mac);
            return getBluetoothManager().getConnectionState(device, BluetoothProfile.GATT) == BluetoothProfile.STATE_CONNECTED;
        }
        return false;
    }

    public static boolean checkMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
