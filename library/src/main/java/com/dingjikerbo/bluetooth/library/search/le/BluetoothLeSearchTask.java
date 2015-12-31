package com.dingjikerbo.bluetooth.library.search.le;

import com.dingjikerbo.bluetooth.library.search.BluetoothSearchTask;
import com.dingjikerbo.bluetooth.library.utils.BluetoothConstants;

public class BluetoothLeSearchTask extends BluetoothSearchTask {

    public BluetoothLeSearchTask() {
        super(BluetoothConstants.SEARCH_TYPE_BLE);
    }

    public BluetoothLeSearchTask(int duration) {
        super(BluetoothConstants.SEARCH_TYPE_BLE, duration);
    }
}
