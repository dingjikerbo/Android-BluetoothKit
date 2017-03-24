package com.inuker.bluetooth.library.model;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by dingjikerbo on 2016/9/5.
 */
public class BleGattService implements Parcelable, Comparable {

    private ParcelUuid uuid;

    private List<BleGattCharacter> characters;

    public BleGattService(UUID uuid, Map<UUID, BluetoothGattCharacteristic> characters) {
        this.uuid = new ParcelUuid(uuid);

        Iterator<BluetoothGattCharacteristic> itor = characters.values().iterator();
        while (itor.hasNext()) {
            BluetoothGattCharacteristic characteristic = itor.next();
            getCharacters().add(new BleGattCharacter(characteristic));
        }
    }


    protected BleGattService(Parcel in) {
        uuid = in.readParcelable(ParcelUuid.class.getClassLoader());
        characters = in.createTypedArrayList(BleGattCharacter.CREATOR);
    }

    public static final Creator<BleGattService> CREATOR = new Creator<BleGattService>() {
        @Override
        public BleGattService createFromParcel(Parcel in) {
            return new BleGattService(in);
        }

        @Override
        public BleGattService[] newArray(int size) {
            return new BleGattService[size];
        }
    };

    public UUID getUUID() {
        return uuid.getUuid();
    }

    public List<BleGattCharacter> getCharacters() {
        if (characters == null) {
            characters = new ArrayList<BleGattCharacter>();
        }
        return characters;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Service: %s\n", uuid));

        List<BleGattCharacter> characters = getCharacters();
        int size = characters.size();
        for (int i = 0; i < size; i++) {
            sb.append(String.format(">>> Character: %s", characters.get(i)));
            if (i != size - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    @Override
    public int compareTo(Object another) {
        if (another == null) {
            return 1;
        }

        BleGattService anotherService = (BleGattService) another;
        return getUUID().compareTo(anotherService.getUUID());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(uuid, flags);
        dest.writeTypedList(characters);
    }
}
