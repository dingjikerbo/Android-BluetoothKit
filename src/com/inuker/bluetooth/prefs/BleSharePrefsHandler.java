package com.inuker.bluetooth.prefs;

import java.util.Map;

import android.content.SharedPreferences;

/**
 * Created by liwentian on 2015/10/13.
 */
public class BleSharePrefsHandler implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharePrefsHandler mSharePrefsHandler;

    public BleSharePrefsHandler(SharePrefsHandler handler) {
        mSharePrefsHandler = handler;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // TODO Auto-generated method stub

        if (mSharePrefsHandler != null) {
            try {
                mSharePrefsHandler.onSharedPreferenceChanged(sharedPreferences, key);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void onSharePrefsGetAll(Map<String, ?> map) {
        if (mSharePrefsHandler != null) {
            try {
                mSharePrefsHandler.onSharePrefsGetAll(map);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public SharePrefsHandler getSharePrefsHandler() {
        return mSharePrefsHandler;
    }
}
