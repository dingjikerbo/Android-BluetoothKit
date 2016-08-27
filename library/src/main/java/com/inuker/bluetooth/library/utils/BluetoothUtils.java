package com.inuker.bluetooth.library.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.inuker.bluetooth.library.BluetoothConstants;
import com.inuker.bluetooth.library.BluetoothService;
import com.inuker.bluetooth.library.model.BluetoothDevice;

import java.util.ArrayList;
import java.util.List;

public class BluetoothUtils {

    private static BluetoothManager mBluetoothManager;
    private static BluetoothAdapter mBluetoothLeAdapter;
    private static BluetoothAdapter mBluetoothClassicAdapter;

    private static Context getContext() {
        return BluetoothService.getContext();
    }

    public static void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        registerGlobalReceiver(receiver, filter);
    }

    private static void registerGlobalReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        getContext().registerReceiver(receiver, filter);
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
        BluetoothAdapter adapter = getBluetoothClassicAdapter();
        return adapter != null && adapter.isEnabled();
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

    public static boolean isDeviceConnected(String mac) {
        if (!TextUtils.isEmpty(mac) && isBleSupported()) {
            android.bluetooth.BluetoothDevice device = getBluetoothLeAdapter().getRemoteDevice(mac);
            return getBluetoothManager().getConnectionState(device, BluetoothProfile.GATT) == BluetoothProfile.STATE_CONNECTED;
        }
        return false;
    }
}
