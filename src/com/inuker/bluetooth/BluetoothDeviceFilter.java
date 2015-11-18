
package com.inuker.bluetooth;

import java.util.ArrayList;
import java.util.List;

import com.inuker.bluetooth.model.XmBluetoothDevice;
import com.inuker.bluetooth.utils.ListUtils;
import com.inuker.bluetooth.utils.ReflectUtils;

/**
 * Created by frank on 6/22/15.
 */
public class BluetoothDeviceFilter {

    private static List<DeviceFilter> mBleDeviceFilters;
    private static List<DeviceFilter> mBscDeviceFilters;
    
    private static boolean isFilterInitiated() {
        return !ListUtils.isEmpty(mBleDeviceFilters) || !ListUtils.isEmpty(mBscDeviceFilters);
    }

    private static void initDeviceFiltersIfNeeded() {
        if (isFilterInitiated()) {
            return;
        }

        mBleDeviceFilters = new ArrayList<DeviceFilter>();
        mBscDeviceFilters = new ArrayList<DeviceFilter>();

        List<DeviceFilter> filters = ReflectUtils.getInterfaceImplClasses(
        		BluetoothDeviceFilter.class, DeviceFilter.class.getSimpleName());

        for (DeviceFilter filter : filters) {
            switch (filter.getDeviceType()) {
                case XmBluetoothDevice.DEVICE_TYPE_BLE:
                    mBleDeviceFilters.add(filter);
                    break;

                case XmBluetoothDevice.DEVICE_TYPE_CLASSIC:
                    mBscDeviceFilters.add(filter);
                    break;

                default:
                    throw new IllegalStateException("initDeviceFilters: unknown device filter type!");

            }
        }
    }

    public static List<DeviceFilter> getFilters(int filterType) {
        initDeviceFiltersIfNeeded();

        switch (filterType) {
            case XmBluetoothDevice.DEVICE_TYPE_BLE:
                return mBleDeviceFilters;

            case XmBluetoothDevice.DEVICE_TYPE_CLASSIC:
                return mBscDeviceFilters;

            default:
                return null;
        }
    }
    
    public interface DeviceFilter {
        int getDeviceType();

        boolean isDeviceFound(XmBluetoothDevice device);
    }

    public static class MyDeviceFilter implements DeviceFilter {

        @Override
        public boolean isDeviceFound(XmBluetoothDevice device) {
            // TODO Auto-generated method stub
            return device.name.contains("ABCDE");
        }

        @Override
        public int getDeviceType() {
            // TODO Auto-generated method stub
            return XmBluetoothDevice.DEVICE_TYPE_BLE;
        }
    }
}
