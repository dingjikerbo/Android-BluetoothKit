package com.inuker.bluetooth.prefs;

import java.util.Map;

import android.content.SharedPreferences;

/**
 * Created by liwentian on 2015/10/13.
 */
public interface SharePrefsHandler {
    public void onSharePrefsGetAll(Map<String, ?> map);

    public void onSharedPreferenceChanged(
            SharedPreferences sharedPreferences, String key);
}
