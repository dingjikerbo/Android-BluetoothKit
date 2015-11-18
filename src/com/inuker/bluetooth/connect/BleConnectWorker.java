package com.inuker.bluetooth.connect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.inuker.bluetooth.connect.request.BleConnectRequest;
import com.inuker.bluetooth.connect.request.BleDisconnectRequest;
import com.inuker.bluetooth.connect.request.BleNotifyRequest;
import com.inuker.bluetooth.connect.request.BleReadRequest;
import com.inuker.bluetooth.connect.request.BleRequest;
import com.inuker.bluetooth.connect.request.BleUnnotifyRequest;
import com.inuker.bluetooth.connect.request.BleWriteRequest;
import com.inuker.bluetooth.connect.request.IBleDispatch;
import com.inuker.bluetooth.connect.request.IBleRunner;
import com.inuker.bluetooth.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.connect.response.BleResponse;
import com.inuker.bluetooth.utils.BluetoothUtils;
import com.inuker.bluetooth.utils.ByteUtils;
import com.inuker.bluetooth.utils.TestUtils;

/**
 * 直接面向master，所以所有操作处于单线程中，不涉及同步的问题 这是个基础层，不涉及任务出错重试等容错机制，容错可在上层做
 * 本层只做简单的任务超时机制，超时则认为任务失败
 * 
 * @author liwentian
 */
@SuppressWarnings("rawtypes")
public class BleConnectWorker {

	public static final int STATUS_DEVICE_CONNECTED = BluetoothProfile.STATE_CONNECTED;
	public static final int STATUS_DEVICE_SERVICE_READY = 0x13;
	public static final int STATUS_DEVICE_CONNECTING = BluetoothProfile.STATE_CONNECTING;
	public static final int STATUS_DEVICE_DISCONNECTING = BluetoothProfile.STATE_DISCONNECTING;
	public static final int STATUS_DEVICE_DISCONNECTED = BluetoothProfile.STATE_DISCONNECTED;

	private static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

	public static final int MSG_REQUEST_TIMEOUT = 0x9;
	public static final int MSG_GATT_FAILED = 0x10;
	public static final int MSG_CONNECTED = 0x11;

	public static final int MSG_SCHEDULE_NEXT = 0x12;

	private BluetoothGatt mBluetoothGatt;
	private BluetoothDevice mBluetoothDevice;

	private IBleDispatch mBleDispatcher;

	private BleRequest mCurrentRequest;

	private Handler mWorkerHandler;

	private int mConnectStatus;

	private Map<UUID, Map<UUID, BleCharacterWrapper>> mDeviceProfile;

	public static BleConnectWorker attched(String mac, IBleRunner runner,
			IBleDispatch dispatcher) {
		return new BleConnectWorker(mac, runner, dispatcher);
	}

	private BleConnectWorker(String mac, IBleRunner runner,
			IBleDispatch dispatcher) {
		if (TextUtils.isEmpty(mac)) {
			throw new IllegalArgumentException(
					"BleConnectWorker init: empty mac");
		}

		mBleDispatcher = dispatcher;

		BluetoothAdapter adapter = BluetoothUtils.getBluetoothLeAdapter();
		if (adapter != null) {
			mBluetoothDevice = adapter.getRemoteDevice(mac);
		} else {
			throw new IllegalStateException(
					"BleConnectWorker init: adapter null");
		}

		mDeviceProfile = new HashMap<UUID, Map<UUID, BleCharacterWrapper>>();

		mWorkerHandler = new Handler(runner.getLooper()) {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				processWorkerMessage(msg);
			}

		};

		mBleDispatcher.notifyHandlerReady(mWorkerHandler);
	}

	private void processWorkerMessage(Message msg) {
		BluetoothUtils.log(String.format("Worker.handleMessage: %s",
				TestUtils.getMsgName(msg.what)));
		switch (msg.what) {
		case MSG_SCHEDULE_NEXT:
			if (mCurrentRequest != null) {
				throw new IllegalStateException(
						"BleConectWorker MSG_SCHEDULE_NEXT: mCurrentRequest not null");
			}

			processRequest((BleRequest) msg.obj);

			break;

		case MSG_GATT_FAILED:
		case MSG_REQUEST_TIMEOUT:
			/**
			 * 请求超时或失败则关闭当前gatt，下次操作时重开一个gatt
			 */
			if (mCurrentRequest != null) {
				closeBluetoothGatt();
				dispatchRequestFailed();
			} else {
				throw new IllegalStateException(
						"Impossible, current request null when timeout");
			}

			break;

		case MSG_CONNECTED:
			if (mCurrentRequest != null) {
				if (mCurrentRequest.isConnectRequest()) {
					dispatchRequestSuccess();
				} else {
					/**
					 * 如果当前是其它请求，而连接状态变化了，什么也不做，等着超时失败重试或回调
					 */
				}
			} else {
				/*
				 * 有可能，比如没有任何操作了，但是连接一直在，偶尔会断开，偶尔又重新连上了，什么也不做
				 */
			}

			break;
		}
	}

	private void processConnected() {
		mWorkerHandler.obtainMessage(MSG_CONNECTED).sendToTarget();
	}

	/**
	 * 重建连接时刷新service和character，以及notify response
	 */
	private void refreshDeviceProfile() {
		Map<UUID, Map<UUID, BleCharacterWrapper>> newProfiles = new HashMap<UUID, Map<UUID, BleCharacterWrapper>>();

		if (mConnectStatus == STATUS_DEVICE_SERVICE_READY) {
			List<BluetoothGattService> services = mBluetoothGatt.getServices();
			for (BluetoothGattService service : services) {

				BluetoothUtils.log("Service: " + service.getUuid() + ", "
						+ TestUtils.getUUID(service.getUuid()));

				Map<UUID, BleCharacterWrapper> map = new HashMap<UUID, BleCharacterWrapper>();
				newProfiles.put(service.getUuid(), map);

				List<BluetoothGattCharacteristic> characters = service
						.getCharacteristics();

				for (BluetoothGattCharacteristic character : characters) {
					BluetoothUtils.log("character: uuid = "
							+ character.getUuid() + ", "
							+ TestUtils.getUUID(character.getUuid())
							+ ", value = "
							+ ByteUtils.byte2String(character.getValue()));

					map.put(character.getUuid(), new BleCharacterWrapper(
							character, null));
				}
			}

			mDeviceProfile.clear();
			mDeviceProfile.putAll(newProfiles);
		}
	}

	private BleCharacterWrapper getCharacterWrapper(UUID service, UUID character) {
		Map<UUID, BleCharacterWrapper> map = mDeviceProfile.get(service);
		return map != null ? map.get(character) : null;
	}

	private BleCharacterWrapper getCharacterWrapper(
			BluetoothGattCharacteristic character) {
		if (character != null && character.getService() != null) {
			UUID service = character.getService().getUuid();
			return getCharacterWrapper(service, character.getUuid());
		}
		return null;
	}

	private BleNotifyResponse getCharacterNotifyResponse(UUID service,
			UUID character) {
		BleCharacterWrapper wrapper = getCharacterWrapper(service, character);
		return wrapper != null ? wrapper.response : null;
	}

	private BleNotifyResponse getCharacterNotifyResponse(
			BluetoothGattCharacteristic character) {
		if (character != null && character.getService() != null) {
			UUID service = character.getService().getUuid();
			return getCharacterNotifyResponse(service, character.getUuid());
		}
		return null;
	}

	private void setCharacterNotifyResponse(
			BluetoothGattCharacteristic character, BleResponse response) {
		BleCharacterWrapper wrapper = getCharacterWrapper(character);
		if (wrapper != null && response instanceof BleNotifyResponse) {
			wrapper.response = (BleNotifyResponse) response;
		}
	}

	private BluetoothGattCharacteristic getCharacter(UUID service,
			UUID character) {
		BleCharacterWrapper wrapper = getCharacterWrapper(service, character);
		return wrapper != null ? wrapper.character : null;
	}

	private BluetoothGattCharacteristic getCharacter(BleRequest request) {
		return getCharacter(request.getServiceUUID(),
				request.getCharacterUUID());
	}

	/**
	 * 处理连接请求 不管当前是否连接上，都需要重连，因为当设备断电时，我们这边是不会实时知道的
	 * 
	 * @param request
	 */
	private void processConnectRequest(BleConnectRequest request) {
		switch (mConnectStatus) {
		case STATUS_DEVICE_CONNECTED:
			throw new IllegalStateException("status impossible");

		case STATUS_DEVICE_SERVICE_READY:
			dispatchRequestSuccess();
			break;

		default:
			if (mBluetoothGatt == null) {
				mBluetoothGatt = openNewBluetoothGatt();
			} else {
				reconnectGatt();
			}
		}
	}

	/**
	 * 重新连接
	 */
	private void reconnectGatt() {
		BluetoothUtils.log("reconnectGatt");

		if (!mBluetoothGatt.connect()) {
			mBluetoothGatt = openNewBluetoothGatt();
		}
	}

	/**
	 * 处理读请求
	 * 
	 * @param request
	 */
	private void processReadRequest(BleReadRequest request) {
		if (mConnectStatus == STATUS_DEVICE_SERVICE_READY) {
			BluetoothUtils.log(String.format(
					"processReadRequest: service = %s, character = %s",
					request.getServiceUUID(), request.getCharacterUUID()));

			BluetoothGattCharacteristic character = getCharacter(request);

			if (character != null) {
				if (!mBluetoothGatt.readCharacteristic(character)) {
					BluetoothUtils.log("readCharacteristic return false");
					processGattFailed();
				}
			} else {
				BluetoothUtils.log("character not found");
				dispatchRequestFailed();
			}
		} else {
			dispatchRequestFailed();
		}
	}

	/**
	 * 处理写请求
	 * 
	 * @param request
	 */
	private void processWriteRequest(BleWriteRequest request) {
		if (mConnectStatus == STATUS_DEVICE_SERVICE_READY) {
			BluetoothGattCharacteristic character = getCharacter(request);
			if (character != null) {

				if (request.getBytes() != null) {
					character.setValue(request.getBytes());
					BluetoothUtils
							.log(String
									.format("processWriteRequest: service = %s, character = %s, value = 0x%s",
											request.getServiceUUID(), request
													.getCharacterUUID(),
											ByteUtils.byte2String(request
													.getBytes())));
				} else {
					character
							.setValue(new byte[] { (byte) request.getValue() });

					BluetoothUtils
							.log(String
									.format("processWriteRequest: service = %s, character = %s, value = 0x%d",
											request.getServiceUUID(),
											request.getCharacterUUID(),
											request.getValue()));
				}

				BluetoothUtils.log("mBluetoothGatt.writeCharacteristic called");

				if (!mBluetoothGatt.writeCharacteristic(character)) {
					BluetoothUtils.log("writeCharacteristic return false");
					processGattFailed();
				}

			} else {
				BluetoothUtils.log("character not found");
				dispatchRequestFailed();
			}
		} else {
			dispatchRequestFailed();
		}
	}

	/**
	 * 处理断开连接请求
	 * 
	 * @param request
	 */
	private void processDisconnectRequest(BleDisconnectRequest request) {
		closeBluetoothGatt();
		dispatchRequestSuccess();
	}

	private boolean setCharacteristicNotification(BluetoothGatt gatt,
			BluetoothGattCharacteristic characteristic, boolean flag) {
		boolean result = gatt.setCharacteristicNotification(characteristic,
				flag);

		if (result) {
			BluetoothGattDescriptor descriptor = characteristic
					.getDescriptor(UUID
							.fromString(CLIENT_CHARACTERISTIC_CONFIG));

			descriptor
					.setValue(flag ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
							: BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);

			result = mBluetoothGatt.writeDescriptor(descriptor);
		}

		return result;
	}

	// 处理打开通知请求
	private void processNotifyRequest(BleNotifyRequest request) {
		if (mConnectStatus == STATUS_DEVICE_SERVICE_READY) {
			BluetoothGattCharacteristic character = getCharacter(request);

			BluetoothUtils.log(String.format(
					"processNotifyRequest: service = %s, character = %s",
					request.getServiceUUID(), request.getCharacterUUID()));

			if (character != null) {
				if (!setCharacteristicNotification(mBluetoothGatt, character,
						true)) {
					BluetoothUtils
							.log("setCharacteristicNotification return false");
					processGattFailed();
				}
			} else {
				BluetoothUtils.log("character not found");
				dispatchRequestFailed();
			}
		} else {
			dispatchRequestFailed();
		}
	}

	// 处理关闭通知请求
	private void processUnnotifyRequest(BleUnnotifyRequest request) {
		if (mConnectStatus == STATUS_DEVICE_SERVICE_READY) {
			BluetoothGattCharacteristic character = getCharacter(request);

			BluetoothUtils.log(String.format(
					"processNotifyRequest: service = %s, character = %s",
					request.getServiceUUID(), request.getCharacterUUID()));

			if (character != null) {
				if (!setCharacteristicNotification(mBluetoothGatt, character,
						false)) {
					BluetoothUtils
							.log("setCharacteristicNotification return false");
					processGattFailed();
				}
			} else {
				BluetoothUtils.log("character not found");
				dispatchRequestFailed();
			}
		} else {
			dispatchRequestFailed();
		}
	}

	/**
	 * 处理请求，根据请求类型进行分发
	 * 
	 * @param request
	 */
	private void processRequest(BleRequest request) {
		BluetoothUtils.log(String.format("process %s, current status = %s",
				request.toString(), TestUtils.getStatus(mConnectStatus)));

		mCurrentRequest = request;

		startRequestTiming();

		switch (request.getRequestType()) {
		case BleRequest.REQUEST_TYPE_CONNECT:
			processConnectRequest((BleConnectRequest) request);
			break;
		case BleRequest.REQUEST_TYPE_READ:
			processReadRequest((BleReadRequest) request);
			break;
		case BleRequest.REQUEST_TYPE_WRITE:
			processWriteRequest((BleWriteRequest) request);
			break;
		case BleRequest.REQUEST_TYPE_DISCONNECT:
			processDisconnectRequest((BleDisconnectRequest) request);
			break;
		case BleRequest.REQUEST_TYPE_NOTIFY:
			processNotifyRequest((BleNotifyRequest) request);
			break;
		case BleRequest.REQUEST_TYPE_UNNOTIFY:
			processUnnotifyRequest((BleUnnotifyRequest) request);
			break;
		default:
			throw new IllegalArgumentException("unknown request type");
		}
	}

	/**
	 * 任务开始计时，每个任务有超时取消。防止有些任务失败后收不到任何回调， 导致队列后的任务全部阻塞
	 */
	private void startRequestTiming() {
		mWorkerHandler.sendEmptyMessageDelayed(MSG_REQUEST_TIMEOUT,
				mCurrentRequest.getTimeoutLimit());
	}

	/**
	 * 当任务收到回调后，不管成功还是失败，都会停止计时
	 */
	private void stopRequestTiming() {
		mWorkerHandler.removeMessages(MSG_REQUEST_TIMEOUT);
	}

	/**
	 * 任务成功，停止计时，调用回调，并启动下一个任务 不论请求是否有回调，请求结束时都需要调用本函数
	 */
	private void dispatchRequestSuccess() {
		stopRequestTiming();

		mCurrentRequest = null;

		mBleDispatcher.notifyRequestSuccess();
	}

	/**
	 * 任务失败，停止计时，调用回调，并启动下一个任务 不论请求是否有回调，请求结束时都需要调用本函数
	 */
	private void dispatchRequestFailed() {
		stopRequestTiming();

		mCurrentRequest = null;

		mBleDispatcher.notifyRequestFailed();
	}

	private void closeBluetoothGatt() {
		if (mBluetoothGatt != null) {
			BluetoothUtils.log("closeBluetoothGatt");
			mBluetoothGatt.close();
			mBluetoothGatt = null;

			/**
			 * 假如建立连接后discoverService等待回调时超时了，则状态是connected，
			 * 但是会closeGatt，所以这里应该重置状态
			 */
			setConnectStatus(STATUS_DEVICE_DISCONNECTED);
		}
	}

	private BluetoothGatt openNewBluetoothGatt() {
		BluetoothUtils.log("openNewBluetoothGatt");

		closeBluetoothGatt();

		if (mBluetoothGatt == null) {
			mBluetoothGatt = mBluetoothDevice.connectGatt(
					BluetoothUtils.getContext(), false, mConnectCallback);
		}

		return mBluetoothGatt;
	}

	private void processGattFailed() {
		BluetoothUtils.log("processGattFailed: " + mCurrentRequest);
		mWorkerHandler.obtainMessage(MSG_GATT_FAILED, null).sendToTarget();
	}

	private void setConnectStatus(int status) {
		mConnectStatus = status;
		mBleDispatcher.notifyDeviceStatus(mConnectStatus);
	}

	private final BluetoothGattCallback mConnectCallback = new BluetoothGattCallback() {

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			// TODO Auto-generated method stub
			if (status == BluetoothGatt.GATT_FAILURE) {
				BluetoothUtils.log("onConnectionStateChange: GATT_FAILURE");
				setConnectStatus(STATUS_DEVICE_DISCONNECTED);
				processGattFailed();
			} else if (status == BluetoothGatt.GATT_SUCCESS) {
				BluetoothUtils.log("onConnectionStateChange: GATT_SUCCESS");

				if (newState == BluetoothProfile.STATE_CONNECTED) {
					BluetoothUtils
							.log("onConnectionStateChange: STATE_CONNECTED");
					setConnectStatus(STATUS_DEVICE_CONNECTED);
					gatt.discoverServices();
				} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
					BluetoothUtils
							.log("onConnectionStateChange: STATE_DISCONNECTED");
					setConnectStatus(STATUS_DEVICE_DISCONNECTED);
				} else {
					if (newState == BluetoothProfile.STATE_CONNECTING) {
						setConnectStatus(STATUS_DEVICE_CONNECTING);
						BluetoothUtils
								.log("onConnectionStateChange: STATE_CONNECTING");
					} else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
						setConnectStatus(STATUS_DEVICE_DISCONNECTING);
						BluetoothUtils
								.log("onConnectionStateChange: STATE_DISCONNECTING");
					} else {
						BluetoothUtils.logW("onConnectionStateChange: "
								+ newState);
						setConnectStatus(newState);
					}
				}
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			// TODO Auto-generated method stub
			if (status == BluetoothGatt.GATT_SUCCESS) {
				BluetoothUtils.log("onServicesDiscovered GATT_SUCCESS");
				setConnectStatus(STATUS_DEVICE_SERVICE_READY);
				refreshDeviceProfile();

				/**
				 * service准备好了才算连接建立完成，之后的读写才算可以开始了，否则连接超时或失败
				 */
				processConnected();

			} else {
				BluetoothUtils.log("onServicesDiscovered " + status);
				setConnectStatus(STATUS_DEVICE_DISCONNECTED);
				processGattFailed();
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			// TODO Auto-generated method stub
			if (status == BluetoothGatt.GATT_SUCCESS) {
				BluetoothUtils.log("onCharacteristicRead GATT_SUCCESS");

				if (mCurrentRequest != null) {
					Bundle data = new Bundle();
					data.putByteArray("value", characteristic.getValue());
					mCurrentRequest.putExtra(data);
				} else {
					throw new IllegalStateException(
							"BleConnectWorker.onCharacteristicRead: mCurrentRequest null");
				}

				dispatchRequestSuccess();
			} else if (status == BluetoothGatt.GATT_FAILURE) {
				BluetoothUtils.log("onCharacteristicRead GATT_FAILURE");
				processGattFailed();
			} else {
				BluetoothUtils.log("onCharacteristicRead, status = " + status);

				// 如果是权限等原因导致的读失败则不用重开gatt
				dispatchRequestFailed();
			}
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			// TODO Auto-generated method stub
			if (status == BluetoothGatt.GATT_SUCCESS) {
				BluetoothUtils.log("onCharacteristicWrite GATT_SUCCESS");

				if (mCurrentRequest != null) {
					Bundle data = new Bundle();
					data.putByteArray("value", characteristic.getValue());
					mCurrentRequest.putExtra(data);
				} else {
					throw new IllegalStateException(
							"BleConnectWorker.onCharacteristicWrite: mCurrentRequest null");
				}

				dispatchRequestSuccess();
			} else if (status == BluetoothGatt.GATT_FAILURE) {
				BluetoothUtils.log("onCharacteristicWrite GATT_FAILURE");
				processGattFailed();
			} else {
				BluetoothUtils.log("onCharacteristicWrite, status = " + status);
				dispatchRequestFailed();
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			// TODO Auto-generated method stub
			BluetoothUtils.log(String.format(
					"onCharacteristicChanged, uuid = %s, value = %s",
					characteristic.getUuid(),
					ByteUtils.byte2String(characteristic.getValue())));

			BleNotifyResponse response = getCharacterNotifyResponse(characteristic);

			if (response != null && characteristic.getService() != null) {
				BluetoothGattService service = characteristic.getService();
				mBleDispatcher.notifyCharacterChanged(service.getUuid(),
						characteristic.getUuid(), characteristic.getValue(),
						response);
			}
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			// TODO Auto-generated method stub
			BluetoothUtils.log(String.format("onDescriptorWrite, status = %d",
					status));

			if (status == BluetoothGatt.GATT_SUCCESS) {
				if (mCurrentRequest != null) {
					BluetoothGattCharacteristic character = getCharacter(mCurrentRequest);
					if (character != null
							&& character == descriptor.getCharacteristic()) {
						if (mCurrentRequest instanceof BleNotifyRequest) {
							setCharacterNotifyResponse(character,
									mCurrentRequest.getResponse());
							dispatchRequestSuccess();
						} else if (mCurrentRequest instanceof BleUnnotifyRequest) {
							setCharacterNotifyResponse(character, null);
							dispatchRequestSuccess();
						} else {
							BluetoothUtils.logW("onDescriptorWrite "
									+ mCurrentRequest);
						}
					}
				}
			} else if (status == BluetoothGatt.GATT_FAILURE) {
				BluetoothUtils.log("onCharacteristicWrite GATT_FAILURE");
				processGattFailed();
			} else {
				BluetoothUtils.log("onCharacteristicWrite, status = " + status);
				dispatchRequestFailed();
			}
		}
	};

	private void postDelayed(Runnable runnable, long delayMillis) {
		mWorkerHandler.postDelayed(runnable, delayMillis);
	}

	@SuppressWarnings("unused")
	private void post(Runnable runnable) {
		postDelayed(runnable, 0);
	}
}
