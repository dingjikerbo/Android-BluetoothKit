package com.inuker.bluetooth.library;

import android.content.Context;

/**
 * Created by dingjikerbo on 2016/10/26.
 */

public class BluetoothContext {

    private static Context mContext;

    public static void setContext(Context context) {
        mContext = context;
    }

    public static Context getContext() {
        return mContext;
    }
}
