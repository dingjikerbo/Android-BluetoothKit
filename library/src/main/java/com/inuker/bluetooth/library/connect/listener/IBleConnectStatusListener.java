package com.inuker.bluetooth.library.connect.listener;

/**
 * Created by dingjikerbo on 2016/9/6.
 */
public interface IBleConnectStatusListener {
    void onConnectStatusChanged(String mac, int status);
}
