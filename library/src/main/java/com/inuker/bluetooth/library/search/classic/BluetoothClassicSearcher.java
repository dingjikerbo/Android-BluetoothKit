package com.inuker.bluetooth.library.search.classic;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.inuker.bluetooth.library.search.BluetoothSearcher;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.BluetoothSearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothUtils;

/**
 * @author dingjikerbo
 */
public class BluetoothClassicSearcher extends BluetoothSearcher {

	private BluetoothSearchReceiver mReceiver;
	
	private BluetoothClassicSearcher() {
		mBluetoothAdapter = BluetoothUtils.getBluetoothAdapter();
	}

	public static BluetoothClassicSearcher getInstance() {
		return BluetoothClassicSearcherHolder.instance;
	}

	private static class BluetoothClassicSearcherHolder {
		private static BluetoothClassicSearcher instance = new BluetoothClassicSearcher();
	}

	@Override
	public void startScanBluetooth(BluetoothSearchResponse callback) {
		// TODO Auto-generated method stub
		super.startScanBluetooth(callback);

		registerReceiver();

		if (mBluetoothAdapter.isDiscovering()) {
			mBluetoothAdapter.cancelDiscovery();
		}

		mBluetoothAdapter.startDiscovery();
	}

	@Override
	public void stopScanBluetooth() {
		// TODO Auto-generated method stub
		unregisterReceiver();
		
		if (mBluetoothAdapter.isDiscovering()) {
			mBluetoothAdapter.cancelDiscovery();
		}

		super.stopScanBluetooth();
	}
	
	@Override
	protected void cancelScanBluetooth() {
		// TODO Auto-generated method stub
		unregisterReceiver();
		
		if (mBluetoothAdapter.isDiscovering()) {
			mBluetoothAdapter.cancelDiscovery();
		}

		super.cancelScanBluetooth();
	}
	
	private void registerReceiver() {
		if (mReceiver == null) {
			mReceiver = new BluetoothSearchReceiver();
			BluetoothUtils.registerReceiver(mReceiver,
					new IntentFilter(BluetoothDevice.ACTION_FOUND));
		}
	}
	
	private void unregisterReceiver() {
		if (mReceiver != null) {
			BluetoothUtils.unregisterReceiver(mReceiver);
			mReceiver = null;
		}
	}

	private class BluetoothSearchReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,
						Short.MIN_VALUE);

				SearchResult xmDevice = new SearchResult(device,
						rssi, null);

				notifyDeviceFounded(xmDevice);
			}
		}
	};
}
