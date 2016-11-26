package com.inuker.bluetooth.library.receiver.listener;

import java.util.UUID;

/**
 * Created by dingjikerbo on 16/11/26.
 */

public abstract class BleCharacterChangeListener implements BluetoothReceiverListener {

    public String getName() {
        return BleCharacterChangeListener.class.getSimpleName();
    }

    public abstract void onCharacterChanged(String mac, UUID service, UUID character, byte[] value);
}
