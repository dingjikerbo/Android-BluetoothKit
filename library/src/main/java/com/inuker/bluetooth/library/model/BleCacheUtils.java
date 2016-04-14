package com.inuker.bluetooth.library.model;

import com.inuker.bluetooth.library.utils.BluetoothConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwentian on 2016/4/5.
 */
public class BleCacheUtils {

    public static void reloadCache() {
        BleDevicePropCache.getInstance().reloadIfNeeded();
    }

    public static void setPropName(String mac, String name) {
        BleDevicePropCache.getInstance().setPropName(mac, name);
    }

    public static String getPropName(String mac) {
        return BleDevicePropCache.getInstance().getPropName(mac);
    }

    public static void setPropDid(String mac, final String did) {
        BleDevicePropCache.getInstance().setPropDid(mac, did);
    }

    public static String getPropDid(String mac) {
        return BleDevicePropCache.getInstance().getPropDid(mac);
    }

    public static void setPropDesc(String mac, String desc) {
        BleDevicePropCache.getInstance().setPropDesc(mac, desc);
    }

    public static String getPropDesc(String mac) {
        return BleDevicePropCache.getInstance().getPropDesc(mac);
    }

    public static void setPropModel(String mac, String model) {
        BleDevicePropCache.getInstance().setPropModel(mac, model);
    }

    public static String getPropModel(String mac) {
        return BleDevicePropCache.getInstance().getPropModel(mac);
    }

    public static void setPropBoundStatus(String mac, int boundStatus) {
        BleDevicePropCache.getInstance().setPropBoundStatus(mac, boundStatus);
    }

    public static int getPropBoundStatus(String mac) {
        return BleDevicePropCache.getInstance().getPropBoundStatus(mac);
    }

    public static int getPropExtra(String mac, String key, int defaultValue) {
        return BleDevicePropCache.getInstance().getPropExtra(mac, key, defaultValue);
    }

    public static void setPropExtra(String mac, String key, int value) {
        BleDevicePropCache.getInstance().setPropExtra(mac, key, value);
    }

    public static boolean getPropExtra(String mac, String key, boolean defaultValue) {
        return BleDevicePropCache.getInstance().getPropExtra(mac, key, defaultValue);
    }

    public static void setPropExtra(String mac, String key, boolean value) {
        BleDevicePropCache.getInstance().setPropExtra(mac, key, value);
    }

    public static String getPropExtra(String mac, String key) {
        return BleDevicePropCache.getInstance().getPropExtra(mac, key);
    }

    public static void setPropExtra(String mac, String key, String value) {
        BleDevicePropCache.getInstance().setPropExtra(mac, key, value);
    }

    public static void removePropExtra(String mac, String key) {
        BleDevicePropCache.getInstance().removePropExtra(mac, key);
    }

    public static List<String> getLocalBoundedDevices() {
        final List<String> macs = new ArrayList<String>();

        BleDevicePropCache.getInstance().traverse(new BleDevicePropCache.IPropTraverse() {
            @Override
            public boolean onTraverse(String mac, BleDeviceProp prop) {
                if (prop.getBoundStatus() == BoundStatus.LOCAL_BOUNDED) {
                    macs.add(mac);
                }
                return false;
            }
        });

        return macs;
    }

    public static List<String> getLocalUnboundedDevices() {
        final List<String> macs = new ArrayList<String>();

        BleDevicePropCache.getInstance().traverse(new BleDevicePropCache.IPropTraverse() {

            @Override
            public boolean onTraverse(String mac, BleDeviceProp prop) {
                if (prop.getExtra(BluetoothConstants.KEY_LOCAL_UNBOUNDED, false)) {
                    macs.add(mac);
                }
                return false;
            }
        });

        return macs;
    }

    public static List<String> getAlertDevices() {
        final List<String> macs = new ArrayList<String>();

        BleDevicePropCache.getInstance().traverse(new BleDevicePropCache.IPropTraverse() {

            @Override
            public boolean onTraverse(String mac, BleDeviceProp prop) {
                if (prop.getExtra(BluetoothConstants.KEY_DIALOG_ALERTED, false)) {
                    macs.add(mac);
                }
                return false;
            }
        });

        return macs;
    }
}
