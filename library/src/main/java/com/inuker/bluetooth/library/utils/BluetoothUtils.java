package com.inuker.bluetooth.library.utils;

import android.app.Activity;
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
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.inuker.bluetooth.library.connect.XmBluetoothDevice;

import java.util.ArrayList;
import java.util.List;

public class BluetoothUtils extends BaseManager {

    private static BluetoothManager mBluetoothManager;
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
        getLocalBroadcastManager().registerReceiver(receiver, filter);
    }

    public static void unregisterReceiver(BroadcastReceiver receiver) {
        getLocalBroadcastManager().unregisterReceiver(receiver);
    }

    public static void sendBroadcast(Intent intent) {
        getLocalBroadcastManager().sendBroadcast(intent);
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

    public static void openBluetooth(Context context) {
        if (context != null && context instanceof Activity) {
            if (!isBluetoothEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                ((Activity) context).startActivityForResult(intent, BluetoothConstants.REQUEST_CODE_OPEN_BLUETOOTH);
            } else {
                BluetoothLog.w("bluetooth already opened");
            }
        } else {
            BluetoothLog.w("context not activity");
        }
    }

    public static void openBluetoothSilently() {
        if (!isBluetoothEnabled()) {
            BluetoothAdapter adapter = getBluetoothClassicAdapter();
            if (adapter != null) {
                adapter.enable();
            } else {
                BluetoothLog.w("openBluetoothSilently: adapter null");
            }
        } else {
            BluetoothLog.w("bluetooth already opened");
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

    public static XmBluetoothDevice getRemoteXmDevice(String mac) {
        XmBluetoothDevice xmDevice = new XmBluetoothDevice();
        xmDevice.device = getRemoteDevice(mac);
        return xmDevice;
    }

    public static List<BluetoothDevice> getConnectedBluetoothLeDevices() {
        List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();

        BluetoothManager manager = getBluetoothManager();

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

    public static String getType(int type) {
        switch (type) {
            case XmBluetoothDevice.DEVICE_TYPE_BLE:
                return "ble";
            case XmBluetoothDevice.DEVICE_TYPE_CLASSIC:
                return "classic";
        }
        return "unknown";
    }

    public static byte[] generateToken() {
        long now = System.currentTimeMillis();
        double rand = RandomUtils.randFloat();
        String original = String.format("token.%d.%f", now, rand);
        return MD5Utils.MD5_12(original);
    }
}
