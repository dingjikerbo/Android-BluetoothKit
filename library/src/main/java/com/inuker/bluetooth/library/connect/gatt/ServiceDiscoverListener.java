package com.inuker.bluetooth.library.connect.gatt;

import android.bluetooth.BluetoothGatt;

/**
 * Created by liwentian on 2016/8/25.
 */
public interface ServiceDiscoverListener extends GattResponseListener {
    void onServicesDiscovered(int status);
}
