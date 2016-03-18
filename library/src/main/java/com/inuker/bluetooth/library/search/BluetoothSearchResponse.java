package com.inuker.bluetooth.library.search;

import com.inuker.bluetooth.library.connect.XmBluetoothDevice;

public interface BluetoothSearchResponse {
    public void onSearchStarted();

    public void onDeviceFounded(XmBluetoothDevice device);

    public void onSearchStopped();

    public void onSearchCanceled();
}