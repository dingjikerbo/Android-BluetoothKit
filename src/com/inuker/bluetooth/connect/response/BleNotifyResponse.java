package com.inuker.bluetooth.connect.response;

import java.util.UUID;

public interface BleNotifyResponse extends BleResponse<Void> {
	public void onNotify(UUID service, UUID character, byte[] data);
}
