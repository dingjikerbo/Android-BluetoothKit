package com.inuker.library.search.le;

import com.inuker.library.BluetoothConstants;
import com.inuker.library.search.BluetoothSearchTask;

public class BluetoothLeSearchTask extends BluetoothSearchTask {

	public BluetoothLeSearchTask() {
		super(BluetoothConstants.SEARCH_TYPE_BLE);
	}
	
	public BluetoothLeSearchTask(int duration) {
		super(BluetoothConstants.SEARCH_TYPE_BLE, duration);
	}
}
