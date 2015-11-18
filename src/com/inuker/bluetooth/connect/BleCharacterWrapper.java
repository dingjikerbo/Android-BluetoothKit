package com.inuker.bluetooth.connect;

import android.bluetooth.BluetoothGattCharacteristic;

import com.inuker.bluetooth.connect.response.BleNotifyResponse;

public class BleCharacterWrapper {

	public BluetoothGattCharacteristic character;
	public BleNotifyResponse response;
	
	public BleCharacterWrapper(BluetoothGattCharacteristic character, BleNotifyResponse response) {
		this.character = character;
		this.response = response;
	}
}
