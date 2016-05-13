package com.inuker.library.search;

public interface BluetoothSearchResponse {
    void onSearchStarted();

    void onDeviceFounded(BluetoothSearchResult device);

    void onSearchStopped();

    void onSearchCanceled();
}
