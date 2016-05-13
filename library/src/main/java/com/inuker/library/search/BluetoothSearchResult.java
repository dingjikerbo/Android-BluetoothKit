package com.inuker.library.search;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.os.Parcel;
import android.os.Parcelable;

import com.inuker.library.BluetoothConstants;
import com.inuker.library.utils.BluetoothUtils;

/**
 * Created by liwentian on 2016/4/14.
 */
public class BluetoothSearchResult implements Parcelable {

    private BluetoothDevice device;

    private int rssi;

    private byte[] scanRecord;

    private int deviceType;

    private String name;

    public BluetoothSearchResult() {

    }

    public BluetoothSearchResult(BluetoothDevice device) {
        this.device = device;
    }

    public BluetoothSearchResult(BluetoothDevice device, int rssi, byte[] scanRecord) {
        this.device = device;
        this.rssi = rssi;
        this.scanRecord = scanRecord;
    }

    public void setBleDevice() {
        this.deviceType = BluetoothConstants.DEVICE_TYPE_BLE;
    }

    public boolean isBleDevice() {
        return deviceType == BluetoothConstants.DEVICE_TYPE_BLE;
    }

    public void setClassicDevice() {
        this.deviceType = BluetoothConstants.DEVICE_TYPE_CLASSIC;
    }

    public boolean isClassicDevice() {
        return deviceType == BluetoothConstants.DEVICE_TYPE_CLASSIC;
    }

    public void setNameFromDevice() {
        if (device != null) {
            this.name = device.getName();
        } else {
            this.name = "";
        }
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public String getAddress() {
        return device != null ? device.getAddress() : "";
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public byte[] getScanRecord() {
        return scanRecord;
    }

    public void setScanRecord(byte[] scanRecord) {
        this.scanRecord = scanRecord;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        sb.append("name = " + name);
        sb.append(", mac = " + device.getAddress());
        sb.append(", deviceType = " + getDeviceType());

        if (isBleDevice()) {
            sb.append(", state = " + BluetoothUtils.getBluetoothManager().getConnectionState(device, BluetoothProfile.GATT));
        } else if (isClassicDevice()) {
            sb.append(", state = " + device.getBondState());
        }
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
        dest.writeByteArray(this.scanRecord);
        dest.writeInt(this.deviceType);
        dest.writeString(this.name);
    }

    public BluetoothSearchResult(Parcel in) {
        this.device = in.readParcelable(BluetoothDevice.class.getClassLoader());
        this.rssi = in.readInt();
        this.scanRecord = in.createByteArray();
        this.deviceType = in.readInt();
        this.name = in.readString();
    }

    public static final Creator<BluetoothSearchResult> CREATOR = new Creator<BluetoothSearchResult>() {
        public BluetoothSearchResult createFromParcel(Parcel source) {
            return new BluetoothSearchResult(source);
        }

        public BluetoothSearchResult[] newArray(int size) {
            return new BluetoothSearchResult[size];
        }
    };
}
