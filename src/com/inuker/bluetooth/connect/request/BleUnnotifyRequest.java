package com.inuker.bluetooth.connect.request;

import java.util.UUID;

public class BleUnnotifyRequest extends BleRequest<Void> {

	public BleUnnotifyRequest(UUID service, UUID character) {
		super(null);
		mRequestType = REQUEST_TYPE_UNNOTIFY;
		mServiceUUID = service;
		mCharacterUUID = character;
	}

}
