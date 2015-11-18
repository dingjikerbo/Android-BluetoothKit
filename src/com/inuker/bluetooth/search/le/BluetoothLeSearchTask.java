package com.inuker.bluetooth.search.le;

import com.inuker.bluetooth.search.BluetoothSearchTask;
import com.inuker.bluetooth.utils.BluetoothConstants;

public class BluetoothLeSearchTask extends BluetoothSearchTask {

	public BluetoothLeSearchTask() {
		super(BluetoothConstants.SEARCH_TYPE_BLE);
	}
	
	public BluetoothLeSearchTask(int duration) {
		super(BluetoothConstants.SEARCH_TYPE_BLE, duration);
	}
}
