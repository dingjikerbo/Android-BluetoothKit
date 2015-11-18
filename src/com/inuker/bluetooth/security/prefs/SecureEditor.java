package com.inuker.bluetooth.security.prefs;

import java.util.Set;

import com.inuker.bluetooth.security.encryption.EncryptionHelper;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;

/**
 * An {@link Editor} decorator using AES encription.
 *
 * @author NoTiCe
 */
public class SecureEditor implements Editor {
    private Editor editor;
    private EncryptionHelper helper;

    /**
     * Initializes with the {@link EncryptionHelper} an the original
     * {@link Editor}.
     * @param helper
     *            The helper to use.
     * @param edit
     *            The editor to use.
     */
    public SecureEditor(EncryptionHelper helper, Editor edit) {
        this.helper = helper;
        this.editor = edit;
    }

    @Override
    public SecureEditor putString(String key, String value) {
        editor.putString(helper.getKey(key), helper.encode(value));
        return this;
    }

    @Override
    public SecureEditor putStringSet(String key, Set<String> values) {
        editor.putString(helper.getKey(key), helper.encode(values));
        return this;
    }

    @Override
    public SecureEditor putInt(String key, int value) {
        editor.putString(helper.getKey(key), helper.encode(value));
        return this;
    }

    @Override
    public SecureEditor putLong(String key, long value) {
        editor.putString(helper.getKey(key), helper.encode(value));
        return this;
    }

    @Override
    public SecureEditor putFloat(String key, float value) {
        editor.putString(helper.getKey(key), helper.encode(value));
        return this;
    }

    @Override
    public SecureEditor putBoolean(String key, boolean value) {
        editor.putString(helper.getKey(key), helper.encode(value));
        return this;
    }

    @Override
    public SecureEditor remove(String key) {
        editor.remove(helper.encode(key));
        return this;
    }

    @Override
    public SecureEditor clear() {
        editor.clear();
        return this;
    }

    @Override
    public boolean commit() {
        return editor.commit();
    }

    @Override
    public void apply() {
        editor.apply();
    }

    /**
     * Compatibility version of original {@link android.content.SharedPreferences.Editor#apply()}
     * method that simply call {@link android.content.SharedPreferences.Editor#commit()} for pre Android Honeycomb (API 11).
     * This method is thread safe also on pre API 11.
     * Note that when two editors are modifying preferences at the same time, the last one to call apply wins. (Android Doc)
     */
    public void save() {
        compatilitySave(this);
    }

    /**
     * Saves the {@link SharedPreferences}. See save method.
     * @param editor The editor to save/commit.
     */
    public static void compatilitySave(Editor editor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            editor.apply();
        } else {
            synchronized (SecureEditor.class) {
                editor.commit();
            }
        }
    }

}
