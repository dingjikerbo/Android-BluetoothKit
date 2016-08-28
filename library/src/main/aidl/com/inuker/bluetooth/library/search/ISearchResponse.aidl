// IBluetoothApi.aidl
package com.inuker.bluetooth.library.search;

// Declare any non-default types here with import statements

import com.inuker.bluetooth.library.search.SearchResult;

interface ISearchResponse {
    void onSearchStarted();

    void onDeviceFounded(in SearchResult device);

    void onSearchStopped();

    void onSearchCanceled();
}
