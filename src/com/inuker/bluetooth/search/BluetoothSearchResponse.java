package com.inuker.bluetooth.search;

import com.inuker.bluetooth.model.XmBluetoothDevice;

public interface BluetoothSearchResponse {
	
	public void onSearchStarted();

    public void onDeviceFounded(XmBluetoothDevice device);

    public void onSearchStopped();
    
    public void onSearchCanceled();
}
