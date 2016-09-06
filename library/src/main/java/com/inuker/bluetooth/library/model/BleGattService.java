package com.inuker.bluetooth.library.model;

import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by liwentian on 2016/9/5.
 */
public class BleGattService implements Parcelable, Comparable {

    private UUID uuid;

    private List<ParcelUuid> characters;

    public BleGattService(UUID uuid) {
        this.uuid = uuid;
    }

    public void addCharacters(Set<UUID> characters) {
        for (UUID character : characters) {
            getCharacters().add(new ParcelUuid(character));
        }
    }

    protected BleGattService(Parcel in) {
        uuid = (UUID) in.readSerializable();
        in.readTypedList(getCharacters(), ParcelUuid.CREATOR);
    }

    public UUID getUUID() {
        return uuid;
    }

    public List<ParcelUuid> getCharacters() {
        if (characters == null) {
            characters = new ArrayList<ParcelUuid>();
        }
        return characters;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(uuid);
        dest.writeTypedList(getCharacters());
    }

    @Override
    public int describeContents() {
        return 0;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Service: %s\n", uuid));

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
        return uuid.compareTo(anotherService.uuid);
    }
}
