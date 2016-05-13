package com.inuker.library.search.le;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.os.Build;

import com.inuker.library.search.BluetoothSearchResponse;
import com.inuker.library.search.BluetoothSearchResult;
import com.inuker.library.search.BluetoothSearcher;
import com.inuker.library.utils.BluetoothUtils;

/**
 * @author liwentian
 */
public class BluetoothLESearcher extends BluetoothSearcher {

	private BluetoothLESearcher() {
		mBluetoothAdapter = BluetoothUtils.getBluetoothLeAdapter();
	}

	public static BluetoothLESearcher getInstance() {
		return BluetoothLESearcherHolder.instance;
	}

	private static class BluetoothLESearcherHolder {
		private static BluetoothLESearcher instance = new BluetoothLESearcher();
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	@SuppressWarnings("deprecation")
	@Override
	public void startScanBluetooth(BluetoothSearchResponse response) {
		// TODO Auto-generated method stub
		super.startScanBluetooth(response);
		
		mBluetoothAdapter.startLeScan(mLeScanCallback);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	@SuppressWarnings("deprecation")
	@Override
	public void stopScanBluetooth() {
		// TODO Auto-generated method stub
		mBluetoothAdapter.stopLeScan(mLeScanCallback);
		super.stopScanBluetooth();
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	@SuppressWarnings("deprecation")
	@Override
	protected void cancelScanBluetooth() {
		// TODO Auto-generated method stub
		mBluetoothAdapter.stopLeScan(mLeScanCallback);
		super.cancelScanBluetooth();
	}

	private final LeScanCallback mLeScanCallback = new LeScanCallback() {

		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			// TODO Auto-generated method stub
			BluetoothSearchResult xmDevice = new BluetoothSearchResult(device, rssi, scanRecord);
			xmDevice.setBleDevice();
            notifyDeviceFounded(xmDevice);
		}
		
	};
}
