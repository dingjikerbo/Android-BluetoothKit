package com.inuker.bluetooth.security.prefs;

import java.util.Map;
import java.util.Set;
import com.inuker.bluetooth.security.encryption.EncryptionAlgorithm;
import com.inuker.bluetooth.security.encryption.EncryptionHelper;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;

/**
 * Decorates SharedPreferences with AES Encryption.
 * @author NoTiCe
 */
public class SecurePreferences implements SharedPreferences {
    private SharedPreferences prefs;
    private EncryptionAlgorithm encryption;
    private EncryptionHelper helper;
    
    /**
     * Initializes with a single {@link SharedPreferences}
     * and the {@link edu.gmu.tec.scout.utilities.Encryption} to use.
     *
     * @param preferences
     *            The {@link SharedPreferences} to use.
     * @param encryption
     *            The {@link edu.gmu.tec.scout.utilities.Encryption} to use.
     */
    public SecurePreferences(SharedPreferences preferences, EncryptionAlgorithm encryption, boolean encryptKeys) {
        this.prefs = preferences;
        this.encryption = encryption;
        helper = new EncryptionHelper(encryption, encryptKeys);
    }

    @Override
    public boolean contains(String key) {
        return prefs.contains(key);
    }

    @SuppressLint("CommitPrefEdits")
	@Override
    public SecureEditor edit() {
        return new SecureEditor(helper, prefs.edit());
    }

    @Override
    public Map<String, ?> getAll() {
        return prefs.getAll();
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return helper.readAndDecodeTemplate(prefs, helper.getKey(key), defValue);
    }

    public void putBoolean(String key, boolean value) {
    	edit().putBoolean(key, value).save();
    }
    
    @Override
    public float getFloat(String key, float defValue) {
        return helper.readAndDecodeTemplate(prefs, helper.getKey(key), defValue);
    }
    
    public void putFloat(String key, float value) {
    	edit().putFloat(key, value).save();
    }

    @Override
    public int getInt(String key, int defValue) {
        return helper.readAndDecodeTemplate(prefs, helper.getKey(key), defValue);
    }
    
    public void putInt(String key, int value) {
    	edit().putInt(key, value).save();
    }

    @Override
    public long getLong(String key, long defValue) {
        return helper.readAndDecodeTemplate(prefs, helper.getKey(key), defValue);
    }
    
    public void putLong(String key, long value) {
    	edit().putLong(key, value).save();
    }

    @Override
    public String getString(String key, String defValue) {
        return helper.readAndDecodeTemplate(prefs, helper.getKey(key), defValue);
    }
    
    public void putString(String key, String value) {
    	edit().putString(key, value).save();
    }

    @TargetApi(value = Build.VERSION_CODES.HONEYCOMB)
    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        return helper.readAndDecodeTemplate(prefs, helper.getKey(key), defValues);
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    protected EncryptionAlgorithm getEncryption() {
        return encryption;
    }

    protected SharedPreferences getPrefs() {
        return prefs;
    }

    protected void setHelper(EncryptionHelper helper) {
        this.helper = helper;
    }
}
