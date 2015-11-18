package com.inuker.bluetooth.connect.request;

import java.util.UUID;

import android.os.Bundle;

import com.inuker.bluetooth.connect.response.BleResponse;

@SuppressWarnings("rawtypes")
public abstract class BleRequest<T> implements BleResponse<T> {

	public static final int REQUEST_TYPE_CONNECT = 0x1;
	public static final int REQUEST_TYPE_READ = 0x2;
	public static final int REQUEST_TYPE_WRITE = 0x4;
	public static final int REQUEST_TYPE_DISCONNECT = 0x8;
	public static final int REQUEST_TYPE_NOTIFY = 0x16;
	public static final int REQUEST_TYPE_UNNOTIFY = 0x32;

	private static final int DEFAULT_TIMEOUT_LIMIT = 10000;

	/**
	 * 默认是不能重试的，除了连接任务
	 */
	private static final int DEFAULT_RETRY_LIMIT = 0;

	protected int mRequestType;

	protected UUID mServiceUUID;
	protected UUID mCharacterUUID;

	protected int mValue;
	protected byte[] mBytes;

	protected BleResponse mResponse;

	protected int mRetryLimit;
	protected int mRetryCount;

	protected int mTimeoutLimit;

	protected Bundle mExtra;

	public BleRequest(BleResponse response) {
		mResponse = response;
		mRetryLimit = getDefaultRetryLimit();
		mTimeoutLimit = DEFAULT_TIMEOUT_LIMIT;
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

	public int getRetryCount() {
		return mRetryCount;
	}

	public void setmRetryCount(int mRetryCount) {
		this.mRetryCount = mRetryCount;
	}

	public void setRetryLimit(int retryLimit) {
		this.mRetryLimit = retryLimit;
	}

	public boolean isConnectRequest() {
		return mRequestType == REQUEST_TYPE_CONNECT;
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
	
	public boolean needConnectionReady() {
		return isReadRequest() || isWriteRequest() || isNotifyRequest() || isUnnotifyRequest();
	}

	public void setResponse(BleResponse response) {
		mResponse = response;
	}

	public UUID getServiceUUID() {
		return mServiceUUID;
	}

	public UUID getCharacterUUID() {
		return mCharacterUUID;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onResponse(int code, T data) {
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

	public void putExtra(Bundle bundle) {
		if (mExtra == null) {
			mExtra = new Bundle();
		}
		if (bundle != null) {
			mExtra.putAll(bundle);
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

	public BleResponse getResponse() {
		return mResponse;
	}
}
