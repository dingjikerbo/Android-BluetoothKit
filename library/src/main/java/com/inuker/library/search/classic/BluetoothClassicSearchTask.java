package com.inuker.library.search.classic;

import com.inuker.library.BluetoothConstants;
import com.inuker.library.search.BluetoothSearchTask;

public class BluetoothClassicSearchTask extends BluetoothSearchTask {

	public BluetoothClassicSearchTask() {
		super(BluetoothConstants.SEARCH_TYPE_CLASSIC);
	}

	public BluetoothClassicSearchTask(int duration) {
		super(BluetoothConstants.SEARCH_TYPE_CLASSIC, duration);
	}
}
