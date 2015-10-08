package com.inuker.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

public class XmBluetoothDevice implements Parcelable {

	public static final int DEVICE_TYPE_CLASSIC = 1;

	public static final int DEVICE_TYPE_BLE = 2;

	public BluetoothDevice device;
	
	public String name;

	public int rssi;

	public boolean connected;

	public byte[] scanRecord;

	public int deviceType;

	public XmBluetoothDevice(BluetoothDevice device, int deviceType) {
		this.device = device;
		this.deviceType = deviceType;
	}

	/**
	 * ApiLevel:10
	 */
	public XmBluetoothDevice(BluetoothDevice device, int rssi,
			byte[] scanRecord, int deviceType) {
		this.device = device;
		this.rssi = rssi;
		this.scanRecord = scanRecord;
		this.deviceType = deviceType;
	}

	public XmBluetoothDevice(Parcel in) {
		device = in.readParcelable(BluetoothDevice.class.getClassLoader());
		rssi = in.readInt();
		connected = (in.readByte() != 0);

		int length = in.readInt();
		if (length > 0) {
			scanRecord = new byte[length];
			in.readByteArray(scanRecord);
		} else {
			scanRecord = null;
		}
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeParcelable(device, 0);
		dest.writeInt(rssi);
		dest.writeByte((byte) (connected ? 1 : 0));

		dest.writeInt(scanRecord != null ? scanRecord.length : 0);
		dest.writeByteArray(scanRecord);
	}

	public static final Parcelable.Creator<XmBluetoothDevice> CREATOR = new Parcelable.Creator<XmBluetoothDevice>() {

		@Override
		public XmBluetoothDevice createFromParcel(Parcel source) {
			return new XmBluetoothDevice(source);
		}

		@Override
		public XmBluetoothDevice[] newArray(int size) {
			return new XmBluetoothDevice[size];
		}
	};

}
