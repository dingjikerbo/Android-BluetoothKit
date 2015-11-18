package com.inuker.bluetooth.search.classic;

import com.inuker.bluetooth.search.BluetoothSearchTask;
import com.inuker.bluetooth.utils.BluetoothConstants;

public class BluetoothClassicSearchTask extends BluetoothSearchTask {

	public BluetoothClassicSearchTask() {
		super(BluetoothConstants.SEARCH_TYPE_CLASSIC);
	}

	public BluetoothClassicSearchTask(int duration) {
		super(BluetoothConstants.SEARCH_TYPE_CLASSIC, duration);
	}
}
