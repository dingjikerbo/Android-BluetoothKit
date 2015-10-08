package com.inuker.bluetooth;

public interface BluetoothSearchResponse {
	
	public void onSearchStarted();

    public void onDeviceFounded(XmBluetoothDevice device);

    public void onSearchStopped();
    
    public void onSearchCanceled();
}
