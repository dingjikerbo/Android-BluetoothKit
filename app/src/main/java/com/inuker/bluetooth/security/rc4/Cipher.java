package com.inuker.bluetooth.security.rc4;

/**
 * Created by dingjikerbo on 2015/11/17.
 */
public abstract class Cipher extends CryptoUtils {

    // / Constructor.
    public Cipher(int keySize) {
        this.keySize = keySize;
    }

    // / How big a key is. Keyless ciphers use 0. Variable-length-key ciphers
    // also use 0.
    public int keySize;

    // / Return how big a key is.
    public int keySize() {
        return keySize;
    }

    // / Set the key from a block of bytes.
    public abstract void setKey(byte[] key);

    // Utility routines.

    // / Utility routine to set the key from a string.
    public void setKey(String keyStr) {
        setKey(makeKey(keyStr));
    }

    // / Utility routine to turn a string into a key of the right length.
    public byte[] makeKey(String keyStr) {
        byte[] key;
        if (keySize == 0)
            key = new byte[keyStr.length()];
        else
            key = new byte[keySize];
        int i, j;

        for (j = 0; j < key.length; ++j)
            key[j] = 0;

        for (i = 0, j = 0; i < keyStr.length(); ++i, j = (j + 1) % key.length)
            key[j] ^= (byte) keyStr.charAt(i);

        return key;
    }

    protected void swap(byte[] bytes, int i, int j) {
        byte temp = bytes[i];
        bytes[i] = bytes[j];
        bytes[j] = temp;
    }

}
