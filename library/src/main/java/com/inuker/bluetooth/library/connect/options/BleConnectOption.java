package com.inuker.bluetooth.library.connect.options;

import android.os.Parcel;

/**
 * Created by dingjikerbo on 16/9/28.
 */
public class BleConnectOption extends GeneralOption {

    private int mServiceDiscoverTimeout;

    public BleConnectOption(int maxRetry, int timeoutInMillis, int serviceDiscoverTimeout) {
        super(maxRetry, timeoutInMillis);
        mServiceDiscoverTimeout = serviceDiscoverTimeout;
    }

    public BleConnectOption(Parcel in) {
        super(in);
        mServiceDiscoverTimeout = in.readInt();
    }

    public static final Creator<BleConnectOption> CREATOR = new Creator<BleConnectOption>() {
        @Override
        public BleConnectOption createFromParcel(Parcel in) {
            return new BleConnectOption(in);
        }

        @Override
        public BleConnectOption[] newArray(int size) {
            return new BleConnectOption[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(mServiceDiscoverTimeout);
    }
}
