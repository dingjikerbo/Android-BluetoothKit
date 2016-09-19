package com.inuker.bluetooth.library.connect.listener;

import com.inuker.bluetooth.library.utils.proxy.ProxyUtils;

import java.util.UUID;

/**
 * Created by dingjikerbo on 2016/9/9.
 */
public abstract class BleConnectStatusListener implements IBleConnectStatusListener {

    protected UUID uuid;

    protected IBleConnectStatusListener listener;

    public BleConnectStatusListener() {
        uuid = UUID.randomUUID();
    }

    BleConnectStatusListener(BleConnectStatusListener listener) {
        this.uuid = listener.uuid;
        this.listener = ProxyUtils.getWeakUIProxy(listener, IBleConnectStatusListener.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BleConnectStatusListener)) return false;

        BleConnectStatusListener that = (BleConnectStatusListener) o;

        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
