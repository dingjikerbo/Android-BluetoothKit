package com.inuker.bluetooth.prefs;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.inuker.bluetooth.utils.AsyncTaskUtils;
import com.inuker.bluetooth.utils.BluetoothUtils;

/**
 * Created by liwentian on 2015/10/13.
 */
public abstract class BluetoothPrefsManager implements SharePrefsHandler {

    protected SharedPreferences mSharedPreferences;
    protected BleSharePrefsHandler mBleSharePrefsHandler;

    protected BluetoothPrefsManager() {
        mBleSharePrefsHandler = new BleSharePrefsHandler(this);

        mSharedPreferences = BluetoothUtils.getContext().getSharedPreferences(getPrefsTag(),
                Context.MODE_PRIVATE);

        mSharedPreferences.registerOnSharedPreferenceChangeListener(mBleSharePrefsHandler);

        refreshSharePrefsAsync();
    }

    public void refreshSharePrefsAsync() {
        if (mBleSharePrefsHandler != null) {
            AsyncTaskUtils.exe(new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    // TODO Auto-generated method stub
                    refreshSharePrefs();
                    return null;
                }
            });
        }
    }

    private void refreshSharePrefs() {
        if (mSharedPreferences != null) {
            Map<String, ?> map = mSharedPreferences.getAll();
            dispatchSharePrefsGetAll(map);
        }
    }

    private void dispatchSharePrefsGetAll(final Map<String, ?> map) {
        if (mBleSharePrefsHandler != null) {
        	mBleSharePrefsHandler.onSharePrefsGetAll(map);
        }

    }

    private void putSharePrefs(String key, String value) {
        if (mSharedPreferences != null) {
            SharePrefsManager.setSettingString(mSharedPreferences, key, value);
        }
    }

    protected void putSharePrefsAsync(final String key, final String value) {
        AsyncTaskUtils.exe(new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                // TODO Auto-generated method stub
                putSharePrefs(key, value);
                return null;
            }

        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        refreshSharePrefsAsync();
    }

    protected abstract String getPrefsTag();
}
