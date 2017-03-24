package com.inuker.bluetooth.library.model;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.UUID;

/**
 * Created by liwentian on 2017/3/24.
 */

public class BleGattDescriptor implements Parcelable {

    private ParcelUuid mUuid;

    private int mPermissions;

    private byte[] mValue;

    protected BleGattDescriptor(Parcel in) {
        mUuid = in.readParcelable(ParcelUuid.class.getClassLoader());
        mPermissions = in.readInt();
        mValue = in.createByteArray();
    }

    public BleGattDescriptor(BluetoothGattDescriptor descriptor) {
        this.mUuid = new ParcelUuid(descriptor.getUuid());
        this.mPermissions = descriptor.getPermissions();
        this.mValue = descriptor.getValue();
    }

    public static final Creator<BleGattDescriptor> CREATOR = new Creator<BleGattDescriptor>() {
        @Override
        public BleGattDescriptor createFromParcel(Parcel in) {
            return new BleGattDescriptor(in);
        }

        @Override
        public BleGattDescriptor[] newArray(int size) {
            return new BleGattDescriptor[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mUuid, flags);
        dest.writeInt(mPermissions);
        dest.writeByteArray(mValue);
    }

    public ParcelUuid getmUuid() {
        return mUuid;
    }

    public void setmUuid(ParcelUuid mUuid) {
        this.mUuid = mUuid;
    }

    public int getmPermissions() {
        return mPermissions;
    }

    public void setmPermissions(int mPermissions) {
        this.mPermissions = mPermissions;
    }

    public byte[] getmValue() {
        return mValue;
    }

    public void setmValue(byte[] mValue) {
        this.mValue = mValue;
    }

    @Override
    public String toString() {
        return "BleGattDescriptor{" +
                "mUuid=" + mUuid +
                ", mPermissions=" + mPermissions +
                ", mValue=" + Arrays.toString(mValue) +
                '}';
    }
}
