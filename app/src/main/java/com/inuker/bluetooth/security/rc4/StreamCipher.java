package com.inuker.bluetooth.security.rc4;

/**
 * Created by liwentian on 2015/11/17.
 */
public abstract class StreamCipher extends Cipher {

    // / Constructor.
    public StreamCipher(int keySize) {
        super(keySize);
    }

    // / Encrypt a byte.
    public abstract byte encrypt(byte clearText);

    // / Decrypt a byte.
    public abstract byte decrypt(byte cipherText);

    // / Encrypt an array of bytes.
    public void encrypt(byte[] clearText, byte[] cipherText) {
        encrypt(clearText, 0, cipherText, 0, clearText.length);
    }

    // / Decrypt an array of bytes.
    public void decrypt(byte[] cipherText, byte[] clearText) {
        decrypt(cipherText, 0, clearText, 0, cipherText.length);
    }

    // / Encrypt some bytes.
    // The default implementation just calls encrypt(byte) repeatedly.
    // This can be overridden for speed.
    public void encrypt(byte[] clearText, int clearOff, byte[] cipherText,
                        int cipherOff, int len) {
        for (int i = 0; i < len; ++i)
            cipherText[cipherOff + i] = encrypt(clearText[clearOff + i]);
    }

    // / Decrypt some bytes.
    // The default implementation just calls decrypt(byte) repeatedly.
    // This can be overridden for speed.
    public void decrypt(byte[] cipherText, int cipherOff, byte[] clearText,
                        int clearOff, int len) {
        for (int i = 0; i < len; ++i)
            clearText[clearOff + i] = decrypt(cipherText[cipherOff + i]);
    }

}
