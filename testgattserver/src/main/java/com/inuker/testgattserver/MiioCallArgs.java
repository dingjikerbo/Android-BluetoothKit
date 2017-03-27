package com.inuker.testgattserver;

import android.os.Binder;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by liwentian on 2017/3/15.
 */

public class MiioCallArgs implements Parcelable {

    public Binder mToken = new Binder();

    protected MiioCallArgs(Parcel in) {
        mToken = (Binder) in.readStrongBinder();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStrongBinder(mToken);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MiioCallArgs> CREATOR = new Creator<MiioCallArgs>() {
        @Override
        public MiioCallArgs createFromParcel(Parcel in) {
            return new MiioCallArgs(in);
        }

        @Override
        public MiioCallArgs[] newArray(int size) {
            return new MiioCallArgs[size];
        }
    };
}
