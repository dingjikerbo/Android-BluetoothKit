package com.inuker.bluetooth.library.connect.gatt;

import android.bluetooth.BluetoothGattDescriptor;

/**
 * Created by liwentian on 2016/8/25.
 */
public interface WriteDescriptorListener extends GattResponseListener {

    void onDescriptorWrite(int status, BluetoothGattDescriptor descriptor);
}
