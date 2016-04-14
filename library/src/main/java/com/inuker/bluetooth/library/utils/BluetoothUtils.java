package com.inuker.bluetooth.library.utils;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.inuker.bluetooth.library.BaseManager;

import java.util.ArrayList;
import java.util.List;

public class BluetoothUtils extends BaseManager {

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

    public static LocalBroadcastManager getLocalBroadcastManager() {
        return LocalBroadcastManager.getInstance(getContext());
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

    private static void registerLocalReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        getLocalBroadcastManager().registerReceiver(receiver, filter);
    }

    private static void registerGlobalReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        getContext().registerReceiver(receiver, filter);
    }

    private static void unregisterLocalReceiver(BroadcastReceiver receiver) {
        getLocalBroadcastManager().unregisterReceiver(receiver);
    }

    private static void unregisterGlobalReceiver(BroadcastReceiver receiver) {
        getContext().unregisterReceiver(receiver);
    }

    private static void sendLocalBroadcast(Intent intent) {
        getLocalBroadcastManager().sendBroadcast(intent);
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
            android.bluetooth.BluetoothManager manager = getBluetoothManager();
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

    public static BluetoothDevice getRemoteDevice(String mac) {
        if (!TextUtils.isEmpty(mac)) {
            BluetoothAdapter adapter = getBluetoothLeAdapter();
            if (adapter != null) {
                return adapter.getRemoteDevice(mac);
            }
        }
        return null;
    }

    public static List<BluetoothDevice> getConnectedBluetoothLeDevices() {
        List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();

        android.bluetooth.BluetoothManager manager = getBluetoothManager();

        if (manager != null) {
            devices.addAll(manager.getConnectedDevices(BluetoothProfile.GATT));
        }

        return devices;
    }

    public static List<BluetoothDevice> getBondedBluetoothClassicDevices() {
        List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();

        BluetoothAdapter adapter = getBluetoothClassicAdapter();

        if (adapter != null) {
            devices.addAll(adapter.getBondedDevices());
        }

        return devices;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean isDeviceConnected(String mac) {
        if (isBleSupported() && !TextUtils.isEmpty(mac)) {
            BluetoothDevice device = getBluetoothLeAdapter().getRemoteDevice(mac);
            return getBluetoothManager().getConnectionState(device, BluetoothProfile.GATT) == BluetoothProfile.STATE_CONNECTED;
        }
        return false;
    }
}
