package com.inuker.bluetooth.prefs;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.inuker.bluetooth.utils.BluetoothUtils;

/**
 * Created by liwentian on 2015/4/20.
 */
public class SharePrefsManager {
    private static HashMap<String, SharedPreferences> mPreferenceMap;

    public static SharedPreferences getSharedPrefs(String name) {
        SharedPreferences sharedPreferences = null;

        if (!TextUtils.isEmpty(name)) {
            if (mPreferenceMap == null) {
                mPreferenceMap = new HashMap<String, SharedPreferences>();
            }

            if (!mPreferenceMap.containsKey(name)) {
                sharedPreferences = BluetoothUtils.getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
                mPreferenceMap.put(name, sharedPreferences);
            } else {
                sharedPreferences = mPreferenceMap.get(name);
            }
        }

        return sharedPreferences;
    }

    public static void setSettingBoolean(SharedPreferences sharedPreferences, final String key, final boolean value) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putBoolean(key, value).commit();
        }
    }

    public static void setSettingBoolean(final String name, final String key, final boolean value) {
        SharedPreferences sharedPreferences = getSharedPrefs(name);
        if (sharedPreferences != null) {
            sharedPreferences.edit().putBoolean(key, value).commit();
        }
    }

    public static boolean getSettingBoolean(SharedPreferences sharedPreferences, final String key, final boolean defaultValue) {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(key, defaultValue);
        }
        return defaultValue;
    }

    public static boolean getSettingBoolean(final String name, final String key, final boolean defaultValue) {
        SharedPreferences sharedPreferences = getSharedPrefs(name);
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(key, defaultValue);
        }
        return defaultValue;
    }

    public static void setSettingString(SharedPreferences sharedPreferences, final String key, final String value) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(key, value).commit();
        }
    }

    public static void setSettingString(final String name, final String key, final String value) {
        SharedPreferences sharedPreferences = getSharedPrefs(name);
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(key, value).commit();
        }
    }

    public static String getSettingString(SharedPreferences sharedPreferences, final String key, final String defaultValue) {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(key, defaultValue);
        }
        return defaultValue;
    }

    public static String getSettingString(final String name, final String key, final String defaultValue) {
        SharedPreferences sharedPreferences = getSharedPrefs(name);
        if (sharedPreferences != null) {
            return sharedPreferences.getString(key, defaultValue);
        }
        return defaultValue;
    }

    public static long getSettingLong(final String name, final String key, final long defaultValue) {
        SharedPreferences sharedPreferences = getSharedPrefs(name);
        if (sharedPreferences != null) {
            return sharedPreferences.getLong(key, defaultValue);
        }
        return defaultValue;
    }

    public static void setSettingLong(final String name, final String key, final long value) {
        SharedPreferences sharedPreferences = getSharedPrefs(name);
        if (sharedPreferences != null) {
            sharedPreferences.edit().putLong(key, value).commit();
        }
    }
}
