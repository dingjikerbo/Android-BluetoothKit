package com.inuker.bluetooth.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MD5Utils {

	public static byte[] MD5_12(String text) {
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		md5.update(text.getBytes(), 0, text.length());

		BluetoothUtils.log("MD5_12: " + ByteUtils.byte2String(md5.digest()));

		int length = md5.digest().length;
		if (length >= 12) {
			return Arrays.copyOfRange(md5.digest(), length / 2 - 6,
					length / 2 + 6);
		} else {
			return ByteUtils.EMPTY_BYTES;
		}
	}

	public static byte[] MD5_4(String text) {
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		md5.update(text.getBytes(), 0, text.length());
		return Arrays.copyOfRange(md5.digest(), 12, 16);
	}
}
