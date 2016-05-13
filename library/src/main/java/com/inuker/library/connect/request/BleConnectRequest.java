package com.inuker.library.connect.request;

import com.inuker.library.connect.BleResponser;

public class BleConnectRequest extends BleRequest {

	public BleConnectRequest(BleResponser response) {
		super(response);
		mRequestType = REQUEST_TYPE_CONNECT;
	}

	@Override
	protected int getDefaultRetryLimit() {
		// TODO Auto-generated method stub
		return 3;
	}

	/**
	 * 红米note 2上发现service特别慢，这里给超时延长点
	 * @return
	 */
	@Override
	public int getTimeoutLimit() {
		return 30000;
	}
}
