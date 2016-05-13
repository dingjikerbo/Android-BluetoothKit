package com.inuker.library.connect.request;

import com.inuker.library.connect.BleResponser;

import java.util.UUID;


public class BleReadRequest extends BleRequest {

	public BleReadRequest(UUID service, UUID character, BleResponser response) {
		super(response);
		mRequestType = REQUEST_TYPE_READ;
		mServiceUUID = service;
		mCharacterUUID = character;
	}
}
