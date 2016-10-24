package com.inuker.bluetooth.library.connect.options;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dingjikerbo on 16/9/28.
 */
public class GeneralOption implements Parcelable {

    private int maxRetry;

    private int timeoutInMillis;

    public GeneralOption(int maxRetry, int timeoutInMillis) {
        this.maxRetry = maxRetry;
        this.timeoutInMillis = timeoutInMillis;
    }

    public int getMaxRetry() {
        return maxRetry;
    }

    public void setMaxRetry(int maxRetry) {
        this.maxRetry = Math.max(maxRetry, 0);
    }

    public int getTimeoutInMillis() {
        return timeoutInMillis;
    }

    public void setTimeoutInMillis(int timeoutInMillis) {
        this.timeoutInMillis = Math.max(timeoutInMillis, 1000);
    }

    protected GeneralOption(Parcel in) {
        maxRetry = in.readInt();
        timeoutInMillis = in.readInt();
    }

    public static final Creator<GeneralOption> CREATOR = new Creator<GeneralOption>() {
        @Override
        public GeneralOption createFromParcel(Parcel in) {
            return new GeneralOption(in);
        }

        @Override
        public GeneralOption[] newArray(int size) {
            return new GeneralOption[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(maxRetry);
        dest.writeInt(timeoutInMillis);
    }
}
