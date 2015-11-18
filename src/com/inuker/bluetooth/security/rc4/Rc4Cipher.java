package com.inuker.bluetooth.security.rc4;


public class Rc4Cipher extends StreamCipher {

	// Constructor, string key.
	public Rc4Cipher(String keyStr) {
		super(256); // (typically, not all key bits are used)
		setKey(keyStr);
	}

	// Constructor, byte-array key.
	public Rc4Cipher(byte[] key) {
		super(256); // (typically, not all key bits are used)
		setKey(key);
	}

	// Key routines.

	private byte[] state = new byte[256];
	private int x, y;

	// / Set the key.
	public void setKey(byte[] key) {
		x = 0;
		y = 0;
		
		for (int i = 0; i < 256; ++i)
			state[i] = (byte) i;

		int j = 0;

		for (int i = 0; i < 256; ++i) {
			j = (j + state[i] + key[i % key.length]) & 0xff;
			swap(state, i, j);
		}
	}

	// / Encrypt a byte.
	public byte encrypt(byte clearText) {
		return (byte) (clearText ^ state[nextState()]);
	}

	// / Decrypt a byte.
	public byte decrypt(byte cipherText) {
		return (byte) (cipherText ^ state[nextState()]);
	}

	// / Encrypt some bytes.
	public void encrypt(byte[] clearText, int clearOff, byte[] cipherText,
			int cipherOff, int len) {
		for (int i = 0; i < len; ++i) {
			cipherText[cipherOff + i] = (byte) (clearText[clearOff + i] ^ state[nextState()]);
		}
	}

	// / Decrypt some bytes.
	public void decrypt(byte[] cipherText, int cipherOff, byte[] clearText,
			int clearOff, int len) {
		for (int i = 0; i < len; ++i) {
			clearText[clearOff + i] = (byte) (cipherText[cipherOff + i] ^ state[nextState()]);
		}
	}

	private int nextState() {
		x = (x + 1) & 0xff;
		y = (y + state[x]) & 0xff;
		swap(state, x, y);
		return (state[x] + state[y]) & 0xff;
	}

}