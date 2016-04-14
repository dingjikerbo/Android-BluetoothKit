package com.inuker.bluetooth.library.search;

public interface BluetoothSearchResponse {

    void onSearchStarted();

    void onDeviceFounded(BluetoothSearchDevice device);

    void onSearchStopped();

    void onSearchCanceled();
}
