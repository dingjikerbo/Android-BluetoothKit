package com.inuker.bluetooth.library.search;

/**
 * Created by liwentian on 2016/8/28.
 */
public interface IBluetoothSearchHelper {

    void startSearch(BluetoothSearchRequest request, BluetoothSearchResponse response);

    void stopSearch();
}
