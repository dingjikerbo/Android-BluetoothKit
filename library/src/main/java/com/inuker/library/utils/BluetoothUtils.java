package com.inuker.library.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.inuker.library.BaseManager;
import com.inuker.library.search.BluetoothSearchResult;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by liwentian on 2016/5/13.
 */
public class BluetoothUtils  extends BaseManager {

    private static android.bluetooth.BluetoothManager mBluetoothManager;
    private static BluetoothAdapter mBluetoothLeAdapter;
    private static BluetoothAdapter mBluetoothClassicAdapter;

    public static boolean isBleSupported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
                && getContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public static boolean isBluetoothEnabled() {
        BluetoothAdapter adapter = getBluetoothClassicAdapter();
        return adapter != null && adapter.isEnabled();
    }

    public static void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        registerGlobalReceiver(receiver, filter);
    }

    public static void unregisterReceiver(BroadcastReceiver receiver) {
        unregisterGlobalReceiver(receiver);
    }

    public static void sendBroadcast(Intent intent) {
        sendGlobalBroadcast(intent);
    }

    public static void sendBroadcast(String action) {
        sendGlobalBroadcast(new Intent(action));
    }

    private static void registerGlobalReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        getContext().registerReceiver(receiver, filter);
    }

    private static void unregisterGlobalReceiver(BroadcastReceiver receiver) {
        getContext().unregisterReceiver(receiver);
    }

    private static void sendGlobalBroadcast(Intent intent) {
        getContext().sendBroadcast(intent);
    }

    public static android.bluetooth.BluetoothManager getBluetoothManager() {
        if (isBleSupported()) {
            if (mBluetoothManager == null) {
                mBluetoothManager = (android.bluetooth.BluetoothManager) getContext()
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    mBluetoothLeAdapter = manager.getAdapter();
                }
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

    public static void closeBluetooth() {
        if (isBluetoothEnabled()) {
            BluetoothAdapter adapter = getBluetoothClassicAdapter();
            if (adapter != null) {
                adapter.disable();
            }
        }
    }

    public static BluetoothDevice getRemoteDevice(String mac) {
        if (!TextUtils.isEmpty(mac)) {
            BluetoothAdapter adapter = getBluetoothLeAdapter();
            if (adapter != null) {
                return adapter.getRemoteDevice(mac);
            }
        }
        return null;
    }

    public static List<BluetoothSearchResult> getConnectedBluetoothLeDevices() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return ListUtils.getEmptyList();
        }

        List<BluetoothSearchResult> results = new ArrayList<BluetoothSearchResult>();

        android.bluetooth.BluetoothManager manager = getBluetoothManager();

        if (manager != null) {
            List<BluetoothDevice> devices = manager.getConnectedDevices(BluetoothProfile.GATT);
            for (BluetoothDevice device : devices) {
                BluetoothSearchResult result = new BluetoothSearchResult(device);
                result.setBleDevice();
                result.setName(device.getName());
                results.add(result);
            }
        }

        return results;
    }

    public static List<BluetoothSearchResult> getBondedBluetoothClassicDevices() {
        List<BluetoothSearchResult> results = new ArrayList<BluetoothSearchResult>();

        BluetoothAdapter adapter = getBluetoothClassicAdapter();

        if (adapter != null) {
            Set<BluetoothDevice> devices = adapter.getBondedDevices();
            for (BluetoothDevice device : devices) {
                BluetoothSearchResult result = new BluetoothSearchResult(device);
                result.setClassicDevice();
                result.setName(device.getName());
                results.add(result);
            }
        }

        return results;
    }

    public static boolean refreshDeviceCache(BluetoothGatt gatt){
        BluetoothLog.w("refreshDeviceCache");

        try {
            if (gatt != null) {
                Method refresh = BluetoothGatt.class.getMethod("refresh");
                if (refresh != null) {
                    refresh.setAccessible(true);
                    return (boolean) refresh.invoke(gatt, new Object[0]);
                }
            }
        } catch (Exception e) {
            BluetoothLog.e(e);
        }
        return false;
    }

}
