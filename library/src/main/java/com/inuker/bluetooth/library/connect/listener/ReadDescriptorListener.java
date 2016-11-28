package com.inuker.bluetooth.library.connect.listener;

import android.bluetooth.BluetoothGattDescriptor;

/**
 * Created by dingjikerbo on 2016/8/25.
 */
public interface ReadDescriptorListener extends GattResponseListener {

    void onDescriptorRead(BluetoothGattDescriptor descriptor, int status, byte[] value);
}
