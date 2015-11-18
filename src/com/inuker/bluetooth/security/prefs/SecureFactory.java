package com.inuker.bluetooth.security.prefs;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import com.inuker.bluetooth.security.encryption.Encryption;
import com.inuker.bluetooth.security.encryption.EncryptionAlgorithm;
import com.inuker.bluetooth.utils.SecureUtils;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * A factory class to ease the creation of the SecureSharedPreferences instance.
 * @author NoTiCe
 */
public final class SecureFactory {
    private static final String INITIALIZATION_ERROR = "Can not initialize SecureSharedPreferences";
    public static final int VERSION_1 = 1;
    public static final int LATEST_VERSION = VERSION_1;

    /**
     * Hidden util constructor.
     */
    private SecureFactory() {
    }

    /**
     * Creates the {@link SecurePreferences} instance with a given original and an {@link EncryptionAlgorithm}.
     * This function does a version check and the required migrations when the local structure is outdated or not encrypted yet.
     * @param original The original {@link SharedPreferences}, which can be also a {@link SecurePreferences} instance.
     * @param encryption The {@link EncryptionAlgorithm} to use.
     * @return A {@link SecurePreferences} instance.
     */
    public static SecurePreferences getPreferences(SharedPreferences original, EncryptionAlgorithm encryption, boolean encryptKeys) {
        SecurePreferences sharedPreferences;
        if (original instanceof SecurePreferences) {
            sharedPreferences = (SecurePreferences) original;
        } else {
            sharedPreferences = new SecurePreferences(original, encryption, encryptKeys);
        }
        
        if (SecureUtils.getVersion(sharedPreferences) < VERSION_1) {
            SecureUtils.migrateData(original, sharedPreferences, VERSION_1);
        }
        return sharedPreferences;
    }

    /**
     * Creates the {@link SecurePreferences} instance with a given original and an {@link EncryptionAlgorithm}.
     * This function does a version check and the required migrations when the local structure is outdated or not encrypted yet.
     * @param original The original {@link SharedPreferences}, which can be also a {@link SecurePreferences} instance.
     * @param password The password to use. This will use the {@link Encryption} implementation of the {@link EncryptionAlgorithm}.
     * @return A {@link SecurePreferences} instance.
     * @throws SecurityException When the {@link EncryptionAlgorithm} can not be initialized.
     */
    public static SecurePreferences getPreferences(SharedPreferences original, String password, boolean encryptKeys) throws SecurityException {
        try {
            EncryptionAlgorithm encryption = new Encryption(password);
            return getPreferences(original, encryption, encryptKeys);
        } catch (UnsupportedEncodingException e) {
            throw new SecurityException(INITIALIZATION_ERROR, e);
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException(INITIALIZATION_ERROR, e);
        } catch (NoSuchPaddingException e) {
            throw new SecurityException(INITIALIZATION_ERROR, e);
        }
    }

    /**
     * Creates a {@link SecurePreferences} instance.
     * @param context The current context.
     * @param preferencesName The name of the {@link SharedPreferences}.
     * @param password The password
     * @return The initialized {@link SecurePreferences}.
     */
    public static SecurePreferences getPreferences(Context context, String preferencesName, String password, boolean encryptKeys) {
        try {
            return getPreferences(context, preferencesName, new Encryption(password), encryptKeys);
        } catch (UnsupportedEncodingException e) {
            throw new SecurityException(INITIALIZATION_ERROR, e);
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException(INITIALIZATION_ERROR, e);
        } catch (NoSuchPaddingException e) {
            throw new SecurityException(INITIALIZATION_ERROR, e);
        }
    }

    /**
     * Creates a {@link SecurePreferences} instance.
     * @param context The current context.
     * @param preferencesName The name of the {@link SharedPreferences}.
     * @param encryption The {@link EncryptionAlgorithm} to use.
     * @return The initialized {@link SecurePreferences}.
     */
    public static SecurePreferences getPreferences(Context context, String preferencesName, EncryptionAlgorithm encryption, boolean encryptKeys) {
        return getPreferences(context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE), encryption, encryptKeys);
    }
}
