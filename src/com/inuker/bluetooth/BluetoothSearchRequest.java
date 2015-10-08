package com.inuker.bluetooth;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.inuker.bluetooth.classic.BluetoothClassicSearchTask;
import com.inuker.bluetooth.le.BluetoothLeSearchTask;

public class BluetoothSearchRequest {

	public static final int SEARCH_TYPE_BLE = 0;
	public static final int SEARCH_TYPE_CLASSIC = 1;
	
	private static final int MSG_START_SEARCH = 0x11;

	private List<BluetoothSearchTask> mSearchTaskList;
	private BluetoothSearchResponse mOuterResponse;

	private BluetoothSearchTask mCurrentTask;

	private Handler mHandler = new Handler(Looper.myLooper()) {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MSG_START_SEARCH:
				scheduleNewSearchTask();
				break;
			}

		}

	};

	public BluetoothSearchRequest() {
		mSearchTaskList = new ArrayList<BluetoothSearchTask>();
	}

	public void start() {
		if (mOuterResponse != null) {
			mOuterResponse.onSearchStarted();
		}
		
		BluetoothUtils.log(getClass().getSimpleName() + " start");
		BluetoothUtils.log(toString());
		
		notifyConnectedBluetoothDevices();
		mHandler.obtainMessage(MSG_START_SEARCH).sendToTarget();
	}
	
	private void scheduleNewSearchTask() {
		if (mSearchTaskList.size() > 0) {
			mCurrentTask = mSearchTaskList.remove(0);
			mCurrentTask.start(mBluetoothSearchResponse);
		} else {
			BluetoothUtils.log(getClass().getSimpleName()+ " stopped");
			
			mCurrentTask = null;
			
			if (mOuterResponse != null) {
				mOuterResponse.onSearchStopped();
			}
		}
	}
	
	private final BluetoothSearchResponse mBluetoothSearchResponse = new BluetoothSearchResponse() {

		@Override
		public void onSearchStarted() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onDeviceFounded(XmBluetoothDevice device) {
			// TODO Auto-generated method stub
			notifyDeviceFounded(device);
		}

		@Override
		public void onSearchStopped() {
			// TODO Auto-generated method stub
			mHandler.sendEmptyMessageDelayed(MSG_START_SEARCH, 100);
		}

		@Override
		public void onSearchCanceled() {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	public void cancel() {
		BluetoothUtils.log(getClass().getSimpleName()+ " cancel");
		
		if (mCurrentTask != null) {
			mCurrentTask.cancel();
			mCurrentTask = null;
		}
		
		mSearchTaskList.clear();
		
		if (mOuterResponse != null) {
			mOuterResponse.onSearchCanceled();
		}
	}

	private void notifyConnectedBluetoothDevices() {
		boolean hasBleTask = false;
		boolean hasBscTask = false;

		for (BluetoothSearchTask task : mSearchTaskList) {
			if (task.isBluetoothLeSearch()) {
				hasBleTask = true;
			} else if (task.isBluetoothClassicSearch()) {
				hasBscTask = true;
			} else {
				throw new IllegalArgumentException("unknown search task type!");
			}
		}

		if (hasBleTask) {
			notifyConnectedBluetoothLeDevices();
		}

		if (hasBscTask) {
			notifyBondedBluetoothClassicDevices();
		}
	}

	private void notifyConnectedBluetoothLeDevices() {
		List<BluetoothDevice> devices = BluetoothUtils
				.getConnectedBluetoothLeDevices();

		for (BluetoothDevice device : devices) {
			XmBluetoothDevice xmDevice = new XmBluetoothDevice(device,
					XmBluetoothDevice.DEVICE_TYPE_BLE);
			xmDevice.connected = true;
			notifyDeviceFounded(xmDevice);
		}
	}

	private void notifyBondedBluetoothClassicDevices() {
		List<BluetoothDevice> devices = BluetoothUtils
				.getBondedBluetoothClassicDevices();

		for (BluetoothDevice device : devices) {
			XmBluetoothDevice xmDevice = new XmBluetoothDevice(device,
					XmBluetoothDevice.DEVICE_TYPE_CLASSIC);
			xmDevice.connected = true;
			notifyDeviceFounded(xmDevice);
		}
	}

	private void notifyDeviceFounded(XmBluetoothDevice device) {
		if (mOuterResponse != null) {
			mOuterResponse.onDeviceFounded(device);
		}
	}

	public void addSearchTasks(List<BluetoothSearchTask> tasks) {
		mSearchTaskList.clear();
		mSearchTaskList.addAll(tasks);
	}

	public void setSearchResponse(BluetoothSearchResponse response) {
		mOuterResponse = response;
	}

	public static class Builder {
		private List<BluetoothSearchTask> searchTaskList;
		private BluetoothSearchResponse searchResponse;

		public Builder() {
			searchTaskList = new ArrayList<BluetoothSearchTask>();
		}

		public Builder searchBluetoothLeDevice() {
			searchBluetoothLeDevice(BluetoothConstants.DEFAULT_DURATION);
			return this;
		}

		public Builder searchBluetoothLeDevice(int duration) {
			BluetoothSearchTask search = new BluetoothLeSearchTask();
			search.setSearchDuration(duration);
			searchTaskList.add(search);
			return this;
		}

		public Builder searchBluetoothClassicDevice() {
			searchBluetoothClassicDevice(BluetoothConstants.DEFAULT_DURATION);
			return this;
		}

		public Builder searchBluetoothClassicDevice(int duration) {
			BluetoothSearchTask search = new BluetoothClassicSearchTask();
			search.setSearchDuration(duration);
			searchTaskList.add(search);
			return this;
		}

		public Builder setSearchResponse(BluetoothSearchResponse response) {
			this.searchResponse = response;
			return this;
		}

		public BluetoothSearchRequest build() {
			BluetoothSearchRequest group = new BluetoothSearchRequest();
			group.addSearchTasks(searchTaskList);
			group.setSearchResponse(searchResponse);
			return group;
		}

	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		
		for (BluetoothSearchTask task : mSearchTaskList) {
			sb.append(task.toString() + ", ");
		}
		
		return sb.toString();
	}
}
