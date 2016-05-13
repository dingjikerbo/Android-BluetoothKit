package com.inuker.library.search;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.inuker.library.BaseManager;
import com.inuker.library.BluetoothConstants;
import com.inuker.library.utils.BluetoothLog;
import com.inuker.library.utils.BluetoothUtils;

/**
 * @author liwentian
 */
public class BluetoothSearchResponser extends BaseManager {

	private static final int MSG_SEARCH_START = 0x40;
	private static final int MSG_SEARCH_CANCEL = 0x50;
	private static final int MSG_SEARCH_STOP = 0x60;
	private static final int MSG_SEARCH_FOUND = 0x70;

	private BluetoothSearchResponser() {

	}

	public static BluetoothSearchResponser getInstance() {
		return BluetoothSearchResponserHolder.instance;
	}

	private static class BluetoothSearchResponserHolder {
		private static BluetoothSearchResponser instance = new BluetoothSearchResponser();
	}

	public void notifySearchStarted(BluetoothSearchResponse response) {
		Intent intent = new Intent(BluetoothConstants.ACTION_SEARCH_START);
		BluetoothUtils.sendBroadcast(intent);

		mResponseHandler.obtainMessage(MSG_SEARCH_START, response)
				.sendToTarget();
	}

	public void notifySearchStopped(BluetoothSearchResponse response) {
		Intent intent = new Intent(BluetoothConstants.ACTION_SEARCH_STOP);
		BluetoothUtils.sendBroadcast(intent);

		mResponseHandler.obtainMessage(MSG_SEARCH_STOP, response)
				.sendToTarget();
	}

	public void notifySearchCanceled(BluetoothSearchResponse response) {
		Intent intent = new Intent(BluetoothConstants.ACTION_SEARCH_STOP);
		BluetoothUtils.sendBroadcast(intent);

		mResponseHandler.obtainMessage(MSG_SEARCH_CANCEL, response)
				.sendToTarget();
	}

	public void notifyDeviceFounded(BluetoothSearchResult device,
			BluetoothSearchResponse response) {
		Message msg = mResponseHandler
				.obtainMessage(MSG_SEARCH_FOUND, response);
		msg.getData().putParcelable("device", device);
		msg.sendToTarget();
	}

	private final Handler mResponseHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			BluetoothSearchResponse response = (BluetoothSearchResponse) msg.obj;

			switch (msg.what) {
			case MSG_SEARCH_START:
				response.onSearchStarted();
				break;

			case MSG_SEARCH_CANCEL:
				response.onSearchCanceled();
				break;

			case MSG_SEARCH_STOP:
				response.onSearchStopped();
				break;

			case MSG_SEARCH_FOUND:
				BluetoothSearchResult device = msg.getData().getParcelable("device");
				response.onDeviceFounded(device);
				break;

			default:
				break;

			}
		}
	};

}
