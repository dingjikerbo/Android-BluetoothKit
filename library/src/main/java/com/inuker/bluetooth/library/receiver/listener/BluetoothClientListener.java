package com.inuker.bluetooth.library.receiver.listener;

/**
 * Created by dingjikerbo on 17/1/14.
 */

public abstract class BluetoothClientListener extends AbsBluetoothListener {

    @Override
    final public void onInvoke(Object... args) {
        throw new UnsupportedOperationException();
    }
}
