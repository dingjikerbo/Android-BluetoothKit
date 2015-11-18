package com.inuker.bluetooth.utils;

import com.inuker.bluetooth.security.rc4.Rc4Cipher;

public class Rc4Utils {
	
	private static Rc4Cipher mRc4Cipher;
	
	private static void initIfNeeded(String key) {
		if (mRc4Cipher == null) {
			mRc4Cipher = new Rc4Cipher(key);
		} else {
			mRc4Cipher.setKey(key);
		}
	}
	
	private static void initIfNeeded(byte[] key) {
		if (mRc4Cipher == null) {
			mRc4Cipher = new Rc4Cipher(key);
		} else {
			mRc4Cipher.setKey(key);
		}
	}
	
	public static byte[] encrypt(String key, byte[] input) {
		initIfNeeded(key);
		byte[] output = new byte[input.length];
		mRc4Cipher.encrypt(input, output);
		return output;
	}
	
	public static byte[] encrypt(byte[] key, byte[] input) {
		initIfNeeded(key);
		byte[] output = new byte[input.length];
		mRc4Cipher.encrypt(input, output);
		return output;
	} 
	
	public static byte[] decrypt(byte[] key, byte[] input) {
		initIfNeeded(key);
		byte[] output = new byte[input.length];
		mRc4Cipher.decrypt(input, output);
		return output;
	} 
}
