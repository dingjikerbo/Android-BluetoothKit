package com.inuker.bluetooth.library.model;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Parcel;
import android.os.Parcelable;

import com.inuker.bluetooth.library.utils.ListUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by dingjikerbo on 2016/9/5.
 */
public class BleGattProfile implements Parcelable {

    private List<BleGattService> services;

    public BleGattProfile(Map< UUID, Map<UUID, BluetoothGattCharacteristic >> map) {
        Iterator itor = map.entrySet().iterator();

        List<BleGattService> serviceList = new ArrayList<BleGattService>();

        while (itor.hasNext()) {
            Map.Entry entry = (Map.Entry) itor.next();
            UUID serviceUUID = (UUID) entry.getKey();
            Map<UUID, BluetoothGattCharacteristic> characters = (Map<UUID, BluetoothGattCharacteristic>) entry.getValue();

            BleGattService service = new BleGattService(serviceUUID, characters);
            if (!serviceList.contains(service)) {
                serviceList.add(service);
            }
        }

        addServices(serviceList);
    }

    public BleGattProfile(Parcel in) {
        in.readTypedList(getServices(), BleGattService.CREATOR);
    }

    public void addServices(List<BleGattService> services) {
        Collections.sort(services);
        getServices().addAll(services);
    }

    public List<BleGattService> getServices() {
        if (services == null) {
            services = new ArrayList<BleGattService>();
        }
        return services;
    }

    public BleGattService getService(UUID serviceId) {
        if (serviceId == null) {
            return null;
        }

        List<BleGattService> services = getServices();
        for (BleGattService service : services) {
            if (service.getUUID().equals(serviceId)) {
                return service;
            }
        }

        return null;
    }

    public boolean containsCharacter(UUID serviceId, UUID characterId) {
        if (serviceId == null || characterId == null) {
            return false;
        }

        BleGattService service = getService(serviceId);
        if (service != null) {
            List<BleGattCharacter> characters = service.getCharacters();
            if (!ListUtils.isEmpty(characters)) {
                for (BleGattCharacter character : characters) {
                    if (characterId.equals(character.getUuid())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(getServices());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BleGattProfile> CREATOR = new Creator<BleGattProfile>() {
        @Override
        public BleGattProfile createFromParcel(Parcel in) {
            return new BleGattProfile(in);
        }

        @Override
        public BleGattProfile[] newArray(int size) {
            return new BleGattProfile[size];
        }
    };

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (BleGattService service : services) {
            sb.append(service).append("\n");
        }
        return sb.toString();
    }
}
