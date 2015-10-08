package com.inuker.bluetooth;

import android.bluetooth.BluetoothAdapter;

import com.inuker.bluetooth.classic.BluetoothClassicSearcher;
import com.inuker.bluetooth.le.BluetoothLESearcher;

public class BluetoothSearcher {

	protected BluetoothAdapter mBluetoothAdapter;
	protected BluetoothSearchResponse mSearchResponse;

	public static BluetoothSearcher newInstance(int type) {
		switch (type) {
		case BluetoothConstants.SEARCH_TYPE_CLASSIC:
			return BluetoothClassicSearcher.getInstance();
		case BluetoothConstants.SEARCH_TYPE_BLE:
			return BluetoothLESearcher.getInstance();
		default:
			throw new IllegalStateException(String.format(
					"unknown search type %d", type));
		}
	}

	protected void startScanBluetooth(BluetoothSearchResponse callback) {
		BluetoothUtils.log(getClass().getSimpleName() + " startScanBluetooth");
		mSearchResponse = callback;
		notifySearchStarted();
	}

	protected void stopScanBluetooth() {
		BluetoothUtils.log(getClass().getSimpleName() + " stopScanBluetooth");
		notifySearchStopped();
	}

	protected void cancelScanBluetooth() {
		BluetoothUtils.log(getClass().getSimpleName() + " cancelScanBluetooth");
		notifySearchCanceled();
	}

	private void notifySearchStarted() {
		if (mSearchResponse != null) {
			mSearchResponse.onSearchStarted();
		}
	}

	protected void notifyDeviceFounded(XmBluetoothDevice device) {
		if (mSearchResponse != null) {
			mSearchResponse.onDeviceFounded(device);
		}
	}

	private void notifySearchStopped() {
		if (mSearchResponse != null) {
			mSearchResponse.onSearchStopped();
		}
	}

	private void notifySearchCanceled() {
		if (mSearchResponse != null) {
			mSearchResponse.onSearchCanceled();
		}
	}
}
