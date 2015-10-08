package com.inuker.bluetooth;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

public class BluetoothUtils {

	private static final String LOG_TAG = "miio-bluetooth";

	private static BluetoothManager mBluetoothManager;
	private static BluetoothAdapter mBluetoothLeAdapter;
	private static BluetoothAdapter mBluetoothClassicAdapter;

	private static Context mContext;
	
	public static void init(Context context) {
		mContext = context;
	}
	
	public static boolean isBleSupported() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
				&& getContext().getPackageManager().hasSystemFeature(
						PackageManager.FEATURE_BLUETOOTH_LE);
	}

	public static boolean isBluetoothEnabled() {
		BluetoothAdapter adapter = getBluetoothClassicAdapter();
		return adapter != null && adapter.isEnabled();
	}

	public static BluetoothManager getBluetoothManager() {
		if (isBleSupported()) {
			if (mBluetoothManager == null) {
				mBluetoothManager = (BluetoothManager) getContext()
						.getSystemService(Context.BLUETOOTH_SERVICE);
			}
			return mBluetoothManager;
		}
		return null;
	}

	public static BluetoothAdapter getBluetoothLeAdapter() {
		if (mBluetoothLeAdapter == null) {
			BluetoothManager manager = getBluetoothManager();
			if (manager != null) {
				mBluetoothLeAdapter = manager.getAdapter();
			}
		}
		return mBluetoothLeAdapter;
	}

	public static BluetoothAdapter getBluetoothClassicAdapter() {
		if (mBluetoothClassicAdapter == null) {
			mBluetoothClassicAdapter = BluetoothAdapter.getDefaultAdapter();
		}
		return mBluetoothClassicAdapter;
	}

	/**
	 * 返回已连接的ble设备列表
	 */
	public static List<BluetoothDevice> getConnectedBluetoothLeDevices() {
		List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();

		BluetoothManager manager = getBluetoothManager();

		if (manager != null) {
			devices.addAll(manager.getConnectedDevices(BluetoothProfile.GATT));
		}

		return devices;
	}

	/**
	 * 返回已配对的传统蓝牙设备
	 */
	public static List<BluetoothDevice> getBondedBluetoothClassicDevices() {
		List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();

		BluetoothAdapter adapter = getBluetoothClassicAdapter();

		if (adapter != null) {
			devices.addAll(adapter.getBondedDevices());
		}

		return devices;
	}

	public static void log(String msg) {
		Log.i(LOG_TAG, msg);
	}

	public static void logE(String msg) {
		Log.e(LOG_TAG, msg);
	}

	public static Context getContext() {
		return mContext;
	}
}
