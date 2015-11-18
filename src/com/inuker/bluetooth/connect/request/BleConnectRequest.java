package com.inuker.bluetooth.connect.request;

import com.inuker.bluetooth.connect.response.BleResponse;

public class BleConnectRequest extends BleRequest<Void> {

	public BleConnectRequest() {
		this(null);
	}

	@SuppressWarnings("rawtypes")
	public BleConnectRequest(BleResponse response) {
		super(response);
		mRequestType = REQUEST_TYPE_CONNECT;
	}

	@Override
	protected int getDefaultRetryLimit() {
		// TODO Auto-generated method stub
		return 3;
	}
}
