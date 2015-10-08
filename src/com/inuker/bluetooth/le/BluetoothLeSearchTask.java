package com.inuker.bluetooth.le;

import com.inuker.bluetooth.BluetoothConstants;
import com.inuker.bluetooth.BluetoothSearchTask;

public class BluetoothLeSearchTask extends BluetoothSearchTask {

	public BluetoothLeSearchTask() {
		super(BluetoothConstants.SEARCH_TYPE_BLE);
	}
	
	public BluetoothLeSearchTask(int duration) {
		super(BluetoothConstants.SEARCH_TYPE_BLE, duration);
	}
}
