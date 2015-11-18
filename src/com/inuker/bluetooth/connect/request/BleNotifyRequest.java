package com.inuker.bluetooth.connect.request;

import java.util.UUID;

import com.inuker.bluetooth.connect.response.BleResponse;

public class BleNotifyRequest extends BleRequest<Void> {

	@SuppressWarnings("rawtypes")
	public BleNotifyRequest(UUID service, UUID character, BleResponse response) {
		super(response);
		mRequestType = REQUEST_TYPE_NOTIFY;
		mServiceUUID = service;
		mCharacterUUID = character;
		mResponse = response;
	}

}
