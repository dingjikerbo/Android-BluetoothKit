package com.inuker.bluetooth;

import com.inuker.bluetooth.search.BluetoothSearchHelper;
import com.inuker.bluetooth.search.BluetoothSearchRequest;
import com.inuker.bluetooth.search.BluetoothSearchResponse;

public class BluetoothManager {

	private BluetoothManager() {

	}

	public static BluetoothManager getInstance() {
		return BluetoothManagerHolder.instance;
	}

	private static class BluetoothManagerHolder {
		private static BluetoothManager instance = new BluetoothManager();
	}

	public void search(BluetoothSearchRequest request,
			BluetoothSearchResponse response) {
		BluetoothSearchHelper.getInstance().startSearch(request, response);
	}
}
