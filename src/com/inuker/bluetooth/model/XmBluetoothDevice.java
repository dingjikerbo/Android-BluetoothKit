package com.inuker.bluetooth.model;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

public class XmBluetoothDevice implements Parcelable {

	public static final int DEVICE_TYPE_CLASSIC = 1;

	public static final int DEVICE_TYPE_BLE = 2;

	public BluetoothDevice device;

	public String name;
	
	public String mac;

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
		name = in.readString();
		mac = in.readString();
		rssi = in.readInt();
		connected = (in.readByte() != 0);

		int length = in.readInt();
		if (length > 0) {
			scanRecord = new byte[length];
			in.readByteArray(scanRecord);
		} else {
			scanRecord = null;
		}
		
		deviceType = in.readInt();
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
		dest.writeString(name);
		dest.writeString(mac);
		dest.writeInt(rssi);
		dest.writeByte((byte) (connected ? 1 : 0));

		dest.writeInt(scanRecord != null ? scanRecord.length : 0);
		dest.writeByteArray(scanRecord);
		
		dest.writeInt(deviceType);
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

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if (o == null) {
        	return false;
        }
		
		if (o == this) {
            return true;
        } 

        XmBluetoothDevice dev = (XmBluetoothDevice) o;
        if (dev.device == device) {
        	return true;
        } 
        
        if (dev.device.getAddress().equalsIgnoreCase(device.getAddress())) {
        	return true;
        }

        return super.equals(o);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		if (device != null) {
			return device.getAddress().hashCode();
		} else {
			return super.hashCode();
		}
	}

}
