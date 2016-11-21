package com.inuker.bluetooth.library.model;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

/**
 * Created by dingjikerbo on 2016/11/17.
 */

public class BleGattCharacter implements Parcelable {

    private UUID uuid;

    private int property;

    private int permissions;

    protected BleGattCharacter(Parcel in) {
        uuid = (UUID) in.readSerializable();
        property = in.readInt();
        permissions = in.readInt();
    }

    public BleGattCharacter(BluetoothGattCharacteristic characteristic) {
        this.uuid = characteristic.getUuid();
        this.property = characteristic.getProperties();
        this.permissions = characteristic.getPermissions();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(uuid);
        dest.writeInt(property);
        dest.writeInt(permissions);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BleGattCharacter> CREATOR = new Creator<BleGattCharacter>() {
        @Override
        public BleGattCharacter createFromParcel(Parcel in) {
            return new BleGattCharacter(in);
        }

        @Override
        public BleGattCharacter[] newArray(int size) {
            return new BleGattCharacter[size];
        }
    };

    public int getProperty() {
        return property;
    }

    public void setProperty(int property) {
        this.property = property;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getPermissions() {
        return permissions;
    }

    public void setPermissions(int permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "BleGattCharacter{" +
                "uuid=" + uuid +
                ", property=" + property +
                ", permissions=" + permissions +
                '}';
    }
}
