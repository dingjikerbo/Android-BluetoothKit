package com.inuker.bluetooth.connect.request;

import java.util.UUID;

import android.os.Handler;

import com.inuker.bluetooth.connect.response.BleNotifyResponse;

public interface IBleDispatch {

	public void notifyRequestSuccess();

	public void notifyRequestFailed();

	public void notifyDeviceStatus(int status);

	public void notifyHandlerReady(Handler handler);

	public void notifyCharacterChanged(UUID service, UUID character, byte[] data, BleNotifyResponse response);
}
