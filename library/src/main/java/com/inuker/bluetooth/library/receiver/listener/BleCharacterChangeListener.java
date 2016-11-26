package com.inuker.bluetooth.library.receiver.listener;

import java.util.UUID;

/**
 * Created by dingjikerbo on 16/11/26.
 */

public interface BleCharacterChangeListener extends BluetoothReceiverListener {
    void onCharacterChanged(String mac, UUID service, UUID character, byte[] value);
}
