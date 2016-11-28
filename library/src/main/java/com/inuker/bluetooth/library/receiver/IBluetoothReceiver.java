package com.inuker.bluetooth.library.receiver;

import com.inuker.bluetooth.library.receiver.listener.BluetoothReceiverListener;

/**
 * Created by dingjikerbo on 2016/11/25.
 */

public interface IBluetoothReceiver {

    void register(BluetoothReceiverListener listener);

    void unregister(BluetoothReceiverListener listener);
}
