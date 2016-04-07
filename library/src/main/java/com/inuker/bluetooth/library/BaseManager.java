package com.inuker.bluetooth.library;

import android.content.Context;

/**
 * Created by dingjikerbo on 16/4/8.
 */
public class BaseManager {

    private static Context mContext;

    static void init(Context context) {
        mContext = context;
    }

    public static Context getContext() {
        return mContext;
    }
}
