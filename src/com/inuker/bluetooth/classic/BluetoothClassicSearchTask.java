package com.inuker.bluetooth.classic;

import com.inuker.bluetooth.BluetoothConstants;
import com.inuker.bluetooth.BluetoothSearchTask;

public class BluetoothClassicSearchTask extends BluetoothSearchTask {

	public BluetoothClassicSearchTask() {
		super(BluetoothConstants.SEARCH_TYPE_CLASSIC);
	}

	public BluetoothClassicSearchTask(int duration) {
		super(BluetoothConstants.SEARCH_TYPE_CLASSIC, duration);
	}
}
