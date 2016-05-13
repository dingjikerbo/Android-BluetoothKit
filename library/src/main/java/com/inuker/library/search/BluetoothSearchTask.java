package com.inuker.library.search;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.inuker.library.BluetoothConstants;

public abstract class BluetoothSearchTask {

	private static final int MSG_SEARCH_TIMEOUT = 0x22;

	private int searchType;
	private int searchDuration;

	private BluetoothSearcher mBluetoothSearcher;

	private Handler mHandler;

	public BluetoothSearchTask(int type) {
		this(type, BluetoothConstants.DEFAULT_DURATION);
	}

	public BluetoothSearchTask(int type, int duration) {
		setSearchType(type);
		setSearchDuration(duration);
	}

	public int getSearchType() {
		return searchType;
	}

	public void setSearchType(int searchType) {
		this.searchType = searchType;
	}

	public int getSearchDuration() {
		return searchDuration;
	}

	public void setSearchDuration(int searchDuration) {
		this.searchDuration = searchDuration;
	}

	public boolean isBluetoothLeSearch() {
		return searchType == BluetoothConstants.SEARCH_TYPE_BLE;
	}

	public boolean isBluetoothClassicSearch() {
		return searchType == BluetoothConstants.SEARCH_TYPE_CLASSIC;
	}

	private BluetoothSearcher getBluetoothSearcher() {
		if (mBluetoothSearcher == null) {
			mBluetoothSearcher = BluetoothSearcher.newInstance(searchType);
		}
		return mBluetoothSearcher;
	}

	public void start(BluetoothSearchResponse response) {
		getBluetoothSearcher().startScanBluetooth(response);
		sendMessageDelayed(MSG_SEARCH_TIMEOUT, searchDuration);
	}

	private void sendMessageDelayed(int what, int delayMillis) {
		if (mHandler == null) {
			mHandler = new Handler(Looper.myLooper()) {

				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					switch (msg.what) {
						case MSG_SEARCH_TIMEOUT:
							getBluetoothSearcher().stopScanBluetooth();
							break;
					}

				}

			};
		}

		Message msg = mHandler.obtainMessage(what);
		mHandler.sendMessageDelayed(msg, delayMillis);
	}

	public void cancel() {
		mHandler.removeCallbacksAndMessages(null);
		getBluetoothSearcher().cancelScanBluetooth();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String type = "";
		if (isBluetoothLeSearch()) {
			type = "Ble";
		} else if (isBluetoothClassicSearch()) {
			type = "classic";
		} else {
			type = "unknown";
		}
		
		if (searchDuration >= 1000) {
			return String.format("%s search (%ds)", type, searchDuration / 1000);
		} else {
			return String.format("%s search (%.1fs)", type, 1.0 * searchDuration / 1000);
		}
	}

}
