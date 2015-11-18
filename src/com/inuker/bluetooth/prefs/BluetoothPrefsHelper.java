package com.inuker.bluetooth.prefs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liwentian on 2015/10/14.
 */
public class BluetoothPrefsHelper {

    private static HashMap<Class<?>, BluetoothPrefsManager> mBluetoothPrefsManagers;

    public static void init() {
        initBluetoothPrefsManagers();
    }

    private static void initBluetoothPrefsManagers() {
        if (mBluetoothPrefsManagers == null) {
            mBluetoothPrefsManagers = new HashMap<Class<?>, BluetoothPrefsManager>();
        } else {
            mBluetoothPrefsManagers.clear();
        }

        mBluetoothPrefsManagers.put(DeviceModelPrefsManager.class, new DeviceModelPrefsManager());

        refreshAllData();
    }

    public static DeviceModelPrefsManager getDeviceNamePrefs() {
        return (DeviceModelPrefsManager) mBluetoothPrefsManagers.get(DeviceModelPrefsManager.class);
    }

    private static List<BluetoothPrefsManager> getAllBluetoothPrefsManagers() {
        List<BluetoothPrefsManager> managers = new ArrayList<BluetoothPrefsManager>();
        managers.addAll(mBluetoothPrefsManagers.values());
        return managers;
    }

    private static void refreshAllData() {
        List<BluetoothPrefsManager> managers = getAllBluetoothPrefsManagers();

        for (BluetoothPrefsManager manager : managers) {
            manager.refreshSharePrefsAsync();
        }
    }
}
