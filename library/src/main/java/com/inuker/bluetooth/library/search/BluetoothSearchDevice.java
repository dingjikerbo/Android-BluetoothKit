package com.inuker.bluetooth.library.search;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by liwentian on 2016/4/14.
 */
public class BluetoothSearchDevice implements Parcelable {

    public static final int DEVICE_TYPE_CLASSIC = 1;
    public static final int DEVICE_TYPE_BLE = 2;
    public static final Creator<BluetoothSearchDevice> CREATOR = new Creator<BluetoothSearchDevice>() {
        public BluetoothSearchDevice createFromParcel(Parcel source) {
            return new BluetoothSearchDevice(source);
        }

        public BluetoothSearchDevice[] newArray(int size) {
            return new BluetoothSearchDevice[size];
        }
    };
    public BluetoothDevice device;
    public int rssi;
    public boolean isConnected;
    public byte[] scanRecord;
    public int deviceType;
    public String name;

    public BluetoothSearchDevice() {

    }

    public BluetoothSearchDevice(BluetoothDevice device, int deviceType) {
        this.device = device;
        this.deviceType = deviceType;
    }


    public BluetoothSearchDevice(BluetoothDevice device, int rssi, byte[] scanRecord, int deviceType) {
        this.device = device;
        this.rssi = rssi;
        this.scanRecord = scanRecord;
        this.deviceType = deviceType;
    }

    public BluetoothSearchDevice(Parcel in) {
        this.device = in.readParcelable(BluetoothDevice.class.getClassLoader());
        this.rssi = in.readInt();
        this.isConnected = in.readByte() != 0;
        this.scanRecord = in.createByteArray();
        this.deviceType = in.readInt();
        this.name = in.readString();
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        sb.append("name = " + name);
        sb.append(", mac = " + device.getAddress());
        sb.append(", connected = " + isConnected);
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.device, 0);
        dest.writeInt(this.rssi);
        dest.writeByte(isConnected ? (byte) 1 : (byte) 0);
        dest.writeByteArray(this.scanRecord);
        dest.writeInt(this.deviceType);
        dest.writeString(this.name);
    }
}
