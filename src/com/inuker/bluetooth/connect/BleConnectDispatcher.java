package com.inuker.bluetooth.connect;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.inuker.bluetooth.connect.request.BleConnectRequest;
import com.inuker.bluetooth.connect.request.BleDisconnectRequest;
import com.inuker.bluetooth.connect.request.BleNotifyRequest;
import com.inuker.bluetooth.connect.request.BleReadRequest;
import com.inuker.bluetooth.connect.request.BleRequest;
import com.inuker.bluetooth.connect.request.BleUnnotifyRequest;
import com.inuker.bluetooth.connect.request.BleWriteRequest;
import com.inuker.bluetooth.connect.request.Code;
import com.inuker.bluetooth.connect.request.IBleDispatch;
import com.inuker.bluetooth.connect.request.IBleRunner;
import com.inuker.bluetooth.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.connect.response.BleResponse;
import com.inuker.bluetooth.utils.BluetoothUtils;
import com.inuker.bluetooth.utils.ListUtils;
import com.inuker.bluetooth.utils.TestUtils;

/**
 * 任务调度器，任务重试等调度策略 告诉worker要做什么就行了，worker忠实地去完成，并通知我结果
 * 
 * @author dingjikerbo
 */
@SuppressWarnings("rawtypes")
public class BleConnectDispatcher implements IBleDispatch {

	public static final int MSG_REQUEST_SUCCESS = 0x40;
	public static final int MSG_REQUEST_FAILED = 0x41;
	public static final int MSG_CHARACTER_CHANGED = 0X42;

	private Handler mWorkerHandler;

	private List<BleRequest> mBleWorkList;
	private BleRequest mCurrentRequest;

	private volatile int mConnectStatus;

	public static BleConnectDispatcher newInstance(String mac, IBleRunner runner) {
		return new BleConnectDispatcher(mac, runner);
	}

	private BleConnectDispatcher(String mac, IBleRunner runner) {
		mBleWorkList = new ArrayList<BleRequest>();
		BleConnectWorker.attched(mac, runner, this);
	}

	public void connect(BleResponse response) {
		addNewRequest(new BleConnectRequest(response));
	}

	public void disconnect() {
		addNewRequest(new BleDisconnectRequest());
	}

	public void read(UUID service, UUID character, BleResponse response) {
		addNewRequest(new BleReadRequest(service, character, response));
	}

	public void write(UUID service, UUID character, int value,
			BleResponse response) {
		addNewRequest(new BleWriteRequest(service, character, value, response));
	}

	public void write(UUID service, UUID character, byte[] bytes,
			BleResponse response) {
		addNewRequest(new BleWriteRequest(service, character, bytes, response));
	}

	public void notify(UUID service, UUID character, BleResponse response) {
		addNewRequest(new BleNotifyRequest(service, character, response));
	}

	public void unnotify(UUID service, UUID character) {
		addNewRequest(new BleUnnotifyRequest(service, character));
	}

	private void addNewRequest(BleRequest request) {
		if (request != null) {
			mBleWorkList.add(request);
			scheduleNextRequest();
		}
	}

	private void addPrioRequest(BleRequest request) {
		if (request != null) {
			mBleWorkList.add(0, request);
			scheduleNextRequest();
		}
	}

	/**
	 * 向worker发送一个新请求，不过得先判断连接是否ready，如果否，则得先建立连接
	 * 
	 * @param request
	 */
	private void callWorkerForNewRequest(BleRequest request) {
		if (mWorkerHandler != null) {
			mWorkerHandler.obtainMessage(BleConnectWorker.MSG_SCHEDULE_NEXT,
					request).sendToTarget();
		}
	}

	/**
	 * 准备处理下一个请求
	 */
	private void scheduleNextRequest() {
		if (mCurrentRequest != null) {
			return;
		}

		BluetoothUtils.log("Dispatcher.scheduleNextRequest ...");

		if (!ListUtils.isEmpty(mBleWorkList)) {
			mCurrentRequest = mBleWorkList.remove(0);

			if (!BluetoothUtils.isBleSupported()) {
				dispatchRequestResult(Code.BLE_NOT_SUPPORTED);
			} else if (!BluetoothUtils.isBluetoothEnabled()) {
				dispatchRequestResult(Code.BLUETOOTH_DISABLED);
			} else if (mCurrentRequest.needConnectionReady()
					&& !isConnectionReady()) {
				dispatchRequestResult(Code.CONNECTION_NOT_READY);
			} else {
				callWorkerForNewRequest(mCurrentRequest);
			}
		}
	}

	private boolean isConnectionReady() {
		return mConnectStatus == BleConnectWorker.STATUS_DEVICE_SERVICE_READY;
	}

	/**
	 * 重试当前任务，直接插入任务头即可
	 */
	private void retryCurrentRequest() {
		if (mCurrentRequest != null) {
			BluetoothUtils.log(String.format(Locale.CHINA, "%s retry (%d / %d)",
					mCurrentRequest, mCurrentRequest.getRetryCount(),
					mCurrentRequest.getRetryLimit()));

			BleRequest request = mCurrentRequest;
			mCurrentRequest.retry();
			mCurrentRequest = null;
			addPrioRequest(request);
		} else {
			throw new IllegalStateException(
					"BleConnectDispatcher.retryCurrentRequest: mCurrentRequest null");
		}
	}

	@SuppressWarnings("unused")
	private void sendMessageToResponseHandler(int what) {
		sendMessageToResponseHandler(what, null, null);
	}

	private void sendMessageToResponseHandler(int what, Object obj) {
		sendMessageToResponseHandler(what, obj, null);
	}

	private void sendMessageToResponseHandler(int what, Object obj, Bundle data) {
		Message msg = mResponseHandler.obtainMessage(what, obj);

		if (data != null) {
			msg.setData(data);
		}

		msg.sendToTarget();
	}

	private final Handler mResponseHandler = new Handler(Looper.getMainLooper()) {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			int code = 0;
			BleRequest request = null;

			if (msg != null && msg.obj instanceof BleRequest) {
				request = (BleRequest) msg.obj;
			}

			Bundle data = msg.getData();

			BluetoothUtils.log(String.format("Dispatcher.onResponse %s",
					TestUtils.getMsgName(msg.what)));

			switch (msg.what) {
			case MSG_REQUEST_SUCCESS:
				if (request != null) {
					code = Code.REQUEST_SUCCESS;

					if (request.isReadRequest()) {
						Bundle bundle = request.getExtra();
						request.onResponse(code,
								bundle != null ? bundle.getByteArray("value")
										: null);
					} else {
						request.onResponse(code, null);
					}
				}

				break;

			case MSG_REQUEST_FAILED:
				if (request != null) {
					if (data != null) {
						code = data.getInt("code", Code.REQUEST_FAILED);
					}
					request.onResponse(code, null);
				}

				break;

			case MSG_CHARACTER_CHANGED:
				BleNotifyResponse response = null;

				if (msg.obj != null && msg.obj instanceof BleNotifyResponse) {
					response = (BleNotifyResponse) msg.obj;
				}

				if (response != null && data != null) {
					UUID service = (UUID) data.getSerializable("service");
					UUID character = (UUID) data.getSerializable("character");
					byte[] value = data.getByteArray("value");
					response.onNotify(service, character, value);
				}

				break;
			}
		}

	};

	@Override
	public void notifyRequestSuccess() {
		// TODO Auto-generated method stub
		dispatchRequestResult(Code.REQUEST_SUCCESS);
	}

	@Override
	public void notifyRequestFailed() {
		// TODO Auto-generated method stub
		if (mCurrentRequest != null) {
			if (mCurrentRequest.canRetry()) {
				retryCurrentRequest();
			} else {
				dispatchRequestResult(Code.REQUEST_FAILED);
			}

		} else {
			throw new IllegalStateException(
					"BleConnectDispatcher.notifyRequestFailed: mCurrentRequest null");
		}
	}

	private void dispatchRequestResult(int code) {
		if (code == Code.REQUEST_SUCCESS) {
			sendMessageToResponseHandler(MSG_REQUEST_SUCCESS, mCurrentRequest);
		} else {
			Bundle data = new Bundle();
			data.putInt("code", code);
			sendMessageToResponseHandler(MSG_REQUEST_FAILED, mCurrentRequest,
					data);
		}

		mCurrentRequest = null;
		scheduleNextRequest();
	}

	@Override
	public void notifyCharacterChanged(UUID service, UUID character,
			byte[] data, BleNotifyResponse response) {
		// TODO Auto-generated method stub
		Bundle bundle = new Bundle();
		bundle.putSerializable("service", service);
		bundle.putSerializable("character", character);
		bundle.putByteArray("value", data);
		sendMessageToResponseHandler(MSG_CHARACTER_CHANGED, response, bundle);
	}

	@Override
	public void notifyDeviceStatus(int status) {
		// TODO Auto-generated method stub
		mConnectStatus = status;
	}
	
	@Override
	public void notifyHandlerReady(Handler handler) {
		// TODO Auto-generated method stub
		mWorkerHandler = handler;
	}

}
