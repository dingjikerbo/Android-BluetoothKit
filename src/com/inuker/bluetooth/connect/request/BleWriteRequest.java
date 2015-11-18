package com.inuker.bluetooth.connect.request;

import java.util.UUID;

import com.inuker.bluetooth.connect.response.BleResponse;

public class BleWriteRequest extends BleRequest<Void> {

	@SuppressWarnings("rawtypes")
	public BleWriteRequest(UUID service, UUID character, int value,
			BleResponse response) {
		super(response);
		mRequestType = REQUEST_TYPE_WRITE;
		mServiceUUID = service;
		mCharacterUUID = character;
		mValue = value;
	}
	
	@SuppressWarnings("rawtypes")
	public BleWriteRequest(UUID service, UUID character, byte[] bytes,
			BleResponse response) {
		super(response);
		mRequestType = REQUEST_TYPE_WRITE;
		mServiceUUID = service;
		mCharacterUUID = character;
		mBytes = bytes;
	}

	public long getValue() {
		return mValue;
	}
	
	public byte[] getBytes() {
		return mBytes;
	}
}
