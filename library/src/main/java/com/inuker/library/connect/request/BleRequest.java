package com.inuker.library.connect.request;

import android.os.Bundle;
import android.text.TextUtils;

import com.inuker.library.BluetoothConstants;
import com.inuker.library.connect.BleResponser;
import com.inuker.library.utils.ByteUtils;

import java.io.Serializable;
import java.util.UUID;

@SuppressWarnings("rawtypes")
public abstract class BleRequest {

	public static final int REQUEST_TYPE_CONNECT = 0x1;
	public static final int REQUEST_TYPE_READ = 0x2;
	public static final int REQUEST_TYPE_WRITE = 0x4;
	public static final int REQUEST_TYPE_DISCONNECT = 0x8;
	public static final int REQUEST_TYPE_NOTIFY = 0x16;
	public static final int REQUEST_TYPE_UNNOTIFY = 0x32;
	public static final int REQUEST_TYPE_READ_RSSI = 0x64;

	private static final int DEFAULT_TIMEOUT_LIMIT = 10000;
	
	/**
	 * 默认是不能重试的，除了连接任务
	 */
	protected static final int DEFAULT_RETRY_LIMIT = 0;

	protected int mRequestType;

	protected UUID mServiceUUID;
	protected UUID mCharacterUUID;

	protected byte[] mBytes;

	protected BleResponser mResponse;

	protected int mRetryLimit;
	protected int mRetryCount;

	protected int mTimeoutLimit;
	
	protected Bundle mExtra;

	public BleRequest(BleResponser response) {
		mResponse = response;
		mRetryLimit = getDefaultRetryLimit();
		mTimeoutLimit = DEFAULT_TIMEOUT_LIMIT;
		mExtra = new Bundle();
	}

	public int getRequestType() {
		return mRequestType;
	}

	public int getTimeoutLimit() {
		return mTimeoutLimit;
	}

	public void setTimeoutLimit(int timeoutLimit) {
		mTimeoutLimit = timeoutLimit;
	}

	public int getRetryLimit() {
		return mRetryLimit;
	}

	public void setRetryLimit(int retryLimit) {
		this.mRetryLimit = retryLimit;
	}

	public boolean isConnectRequest() {
		return mRequestType == REQUEST_TYPE_CONNECT;
	}

	public boolean isDisconnectRequest() {
		return mRequestType == REQUEST_TYPE_DISCONNECT;
	}

	public boolean isReadRequest() {
		return mRequestType == REQUEST_TYPE_READ;
	}

	public boolean isWriteRequest() {
		return mRequestType == REQUEST_TYPE_WRITE;
	}

	public boolean isNotifyRequest() {
		return mRequestType == REQUEST_TYPE_NOTIFY;
	}

	public boolean isUnnotifyRequest() {
		return mRequestType == REQUEST_TYPE_UNNOTIFY;
	}

	public boolean isReadRssiRequest() {
		return mRequestType == REQUEST_TYPE_READ_RSSI;
	}

	public boolean needConnectionReady() {
		return isReadRequest() || isWriteRequest() || isNotifyRequest() || isUnnotifyRequest();
	}

	public void setResponse(BleResponser response) {
		mResponse = response;
	}

	public UUID getServiceUUID() {
		return mServiceUUID;
	}

	public UUID getCharacterUUID() {
		return mCharacterUUID;
	}

	public void onResponse(int code, Bundle data) {
		// TODO Auto-generated method stub
		if (mResponse != null) {
			try {
				mResponse.onResponse(code, data);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		return sb.toString();
	}

	public boolean canRetry() {
		return mRetryCount < mRetryLimit;
	}

	public Bundle getBundle() {
		return mExtra;
	}

	public void putExtra(Bundle bundle) {
		if (bundle != null) {
			mExtra.putAll(bundle);
		}
	}

	public void putStringExtra(String key, String value) {
		if (!TextUtils.isEmpty(key)) {
			Bundle bundle = new Bundle();
			bundle.putString(key, value);
			putExtra(bundle);
		}
	}

	public void putByteArrayExtra(String key, byte[] bytes) {
		if (!TextUtils.isEmpty(key)) {
			Bundle bundle = new Bundle();
			bundle.putByteArray(key, bytes);
			putExtra(bundle);
		}
	}

	public byte[] getByteArrayExtra(String key) {
		if (!TextUtils.isEmpty(key)) {
			Bundle bundle = getBundle();
			if (bundle != null) {
				return bundle.getByteArray(key);
			}
		}
		return ByteUtils.EMPTY_BYTES;
	}

	public void putIntExtra(String key, int value) {
		if (!TextUtils.isEmpty(key)) {
			Bundle bundle = new Bundle();
			bundle.putInt(key, value);
			putExtra(bundle);
		}
	}

	public int getIntExtra(String key, int defaultValue) {
		if (!TextUtils.isEmpty(key)) {
			Bundle bundle = getBundle();
			if (bundle != null) {
				return bundle.getInt(key, defaultValue);
			}
		}
		return defaultValue;
	}

	public void putLongExtra(String key, long value) {
		if (!TextUtils.isEmpty(key)) {
			Bundle bundle = new Bundle();
			bundle.putLong(key, value);
			putExtra(bundle);
		}
	}

	public long getLongExtra(String key, long defaultValue) {
		if (!TextUtils.isEmpty(key)) {
			Bundle bundle = getBundle();
			if (bundle != null) {
				return bundle.getLong(key, defaultValue);
			}
		}
		return defaultValue;
	}

	public void setRequestCode(int code) {
		putIntExtra(BluetoothConstants.KEY_CODE, code);
	}

	public void putSerializableExtra(String key, Serializable object) {
		if (!TextUtils.isEmpty(key)) {
			Bundle bundle = new Bundle();
			bundle.putSerializable(key, object);
			putExtra(bundle);
		}
	}

	public Bundle getExtra() {
		return mExtra;
	}

	public void retry() {
		mRetryCount++;
	}
	
	protected int getDefaultRetryLimit() {
		return DEFAULT_RETRY_LIMIT;
	}

	public BleResponser getResponse() {
		return mResponse;
	}
}
