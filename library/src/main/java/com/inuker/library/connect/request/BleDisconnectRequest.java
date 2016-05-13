package com.inuker.library.connect.request;


public class BleDisconnectRequest extends BleRequest {

	public BleDisconnectRequest() {
		super(null);
		mRequestType = REQUEST_TYPE_DISCONNECT;
	}
}
