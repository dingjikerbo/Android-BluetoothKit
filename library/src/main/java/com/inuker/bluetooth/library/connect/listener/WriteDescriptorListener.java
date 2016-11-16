package com.inuker.bluetooth.library.connect.listener;

import android.bluetooth.BluetoothGattDescriptor;

/**
 * Created by dingjikerbo on 2016/8/25.
 */
public interface WriteDescriptorListener extends GattResponseListener {

    void onDescriptorWrite(BluetoothGattDescriptor descriptor, int status);
}
