package com.inuker.bluetooth.library.connect.listener;

/**
 * Created by liwentian on 2016/9/7.
 */
public interface DisconnectListener extends GattResponseListener {
    void onDisconnected();
}
