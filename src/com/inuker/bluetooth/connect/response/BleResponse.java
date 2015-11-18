package com.inuker.bluetooth.connect.response;

public interface BleResponse<T> {
	public void onResponse(int code, T data);
}
