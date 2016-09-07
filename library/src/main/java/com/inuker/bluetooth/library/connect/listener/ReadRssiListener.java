package com.inuker.bluetooth.library.connect.listener;

/**
 * Created by dingjikerbo on 2016/8/25.
 */
public interface ReadRssiListener extends GattResponseListener {
    void onReadRemoteRssi(int rssi, int status);
}
