package com.inuker.bluetooth.connect.request;

import java.util.UUID;

import com.inuker.bluetooth.connect.response.BleResponse;

public class BleReadRequest extends BleRequest<Void> {

	@SuppressWarnings("rawtypes")
	public BleReadRequest(UUID service, UUID character, BleResponse response) {
		super(response);
		mRequestType = REQUEST_TYPE_READ;
		mServiceUUID = service;
		mCharacterUUID = character;
	}
}
