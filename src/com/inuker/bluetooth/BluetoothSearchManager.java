package com.inuker.bluetooth;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

public class BluetoothSearchManager {

	private static final int MSG_START_SEARCH = 0x10;
	private static final int MSG_CANCEL_SEARCH = 0x20;

	private static final int MSG_SEARCH_START = 0x40;
	private static final int MSG_SEARCH_CANCEL = 0x50;
	private static final int MSG_SEARCH_STOP = 0x60;
	private static final int MSG_SEARCH_FOUND = 0x70;

	private HandlerThread mWorkerThread;
	private Handler mBluetoothSearchHandler;
	private Handler mResponseHandler;

	private BluetoothSearchRequest mCurrentRequest;

	private BluetoothSearchManager() {
		initialHandlerThread();
	}

	public static BluetoothSearchManager getInstance() {
		return BluetoothSearchManagerHolder.instance;
	}

	private static class BluetoothSearchManagerHolder {
		private static BluetoothSearchManager instance = new BluetoothSearchManager();
	}

	public void startSearch(BluetoothSearchRequest request,
			BluetoothSearchResponse response) {
		BluetoothSearchResponser responser = new BluetoothSearchResponser(response);
		request.setSearchResponse(responser);
		
		if (BluetoothUtils.isBluetoothEnabled()) {
			mBluetoothSearchHandler.obtainMessage(MSG_START_SEARCH, request)
					.sendToTarget();
		} else {
			cancelSearch(request);
		}
	}

	public void cancelSearch(BluetoothSearchRequest request) {
		mBluetoothSearchHandler.obtainMessage(MSG_CANCEL_SEARCH, request)
				.sendToTarget();
	}

	private void processStartSearch(BluetoothSearchRequest request) {
		if (mCurrentRequest != null) {
			mCurrentRequest.cancel();
			mCurrentRequest = request;
			mCurrentRequest.start();
		} else {
			mCurrentRequest = request;
			mCurrentRequest.start();
		}
	}

	private void processCancelSearch(BluetoothSearchRequest request) {
		if (mCurrentRequest != null) {
			if (mCurrentRequest == request) {
				mCurrentRequest.cancel();
				mCurrentRequest = null;
			}
		} else if (request != null) {
			request.cancel();
		}
	}

	private class BluetoothSearchResponser implements
			BluetoothSearchResponse {

		private BluetoothSearchResponse mResponse;

		private BluetoothSearchResponser(BluetoothSearchResponse response) {
			mResponse = response;
		}

		@Override
		public void onSearchStarted() {
			// TODO Auto-generated method stub
			mResponseHandler.obtainMessage(MSG_SEARCH_START, mResponse).sendToTarget();
		}

		@Override
		public void onDeviceFounded(XmBluetoothDevice device) {
			// TODO Auto-generated method stub
			Message msg = mResponseHandler.obtainMessage(MSG_SEARCH_FOUND, mResponse);
			Bundle bundle = new Bundle();
			bundle.putParcelable("device", device);
			msg.setData(bundle);
			msg.sendToTarget();
		}

		@Override
		public void onSearchStopped() {
			// TODO Auto-generated method stub
			mResponseHandler.obtainMessage(MSG_SEARCH_STOP, mResponse).sendToTarget();
		}

		@Override
		public void onSearchCanceled() {
			// TODO Auto-generated method stub
			mResponseHandler.obtainMessage(MSG_SEARCH_CANCEL, mResponse).sendToTarget();
		}

	}

	private void initialHandlerThread() {
		mWorkerThread = new HandlerThread("BluetoothSearch");
		mWorkerThread.start();

		mBluetoothSearchHandler = new Handler(mWorkerThread.getLooper()) {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				processBluetoothSearchMessage(msg);
			}

		};

		mResponseHandler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				processBluetoothResponseMessage(msg);
			}
		};
	}

	private void processBluetoothSearchMessage(Message msg) {
		switch (msg.what) {
		case MSG_START_SEARCH:
			processStartSearch((BluetoothSearchRequest) msg.obj);
			break;

		case MSG_CANCEL_SEARCH:
			processCancelSearch((BluetoothSearchRequest) msg.obj);
			break;

		default:
			break;
		}
	}

	private void processBluetoothResponseMessage(Message msg) {
		BluetoothSearchResponse response = (BluetoothSearchResponse) msg.obj;

		switch (msg.what) {
		case MSG_SEARCH_START:
			response.onSearchStarted();
			break;

		case MSG_SEARCH_CANCEL:
			response.onSearchCanceled();
			break;

		case MSG_SEARCH_STOP:
			mCurrentRequest = null;
			response.onSearchStopped();
			break;

		case MSG_SEARCH_FOUND:
			XmBluetoothDevice device = msg.getData().getParcelable("device");
			response.onDeviceFounded(device);
			break;

		default:
			break;

		}
	}
}
