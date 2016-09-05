package com.inuker.bluetooth.library.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by liwentian on 2016/9/5.
 */
public class BleGattProfile implements Parcelable {

    private List<BleGattService> services;

    public BleGattProfile() {

    }

    public BleGattProfile(Parcel in) {
        in.readTypedList(getServices(), BleGattService.CREATOR);
    }

    public void addServices(List<BleGattService> services) {
        Collections.sort(services);
        getServices().addAll(services);
    }

    private List<BleGattService> getServices() {
        if (services == null) {
            services = new ArrayList<BleGattService>();
        }
        return services;
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
