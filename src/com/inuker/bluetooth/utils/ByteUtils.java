package com.inuker.bluetooth.utils;

public class ByteUtils {

	public static final byte[] EMPTY_BYTES = new byte[] {};

	public static boolean isEmpty(byte[] bytes) {
		return bytes == null || bytes.length == 0;
	}

	public static String byte2String(byte[] bytes) {
		StringBuilder sb = new StringBuilder();

		if (!isEmpty(bytes)) {
			for (int i = 0; i < bytes.length; i++) {
				sb.append(String.format("%02x ", bytes[i]));
			}
		}

		return sb.toString();
	}

	public static boolean equals(byte[] lbytes, byte[] rbytes) {
		if (lbytes == null && rbytes == null) {
			return true;
		}

		if (lbytes == null || rbytes == null) {
			return false;
		}

		int llen = lbytes.length;
		int rlen = rbytes.length;

		if (llen != rlen) {
			return false;
		}

		for (int i = 0; i < llen; i++) {
			if (lbytes[i] != rbytes[i]) {
				return false;
			}
		}

		return true;
	}

	public static byte[] fromInt(int n) {
		byte[] bytes = new byte[4];

		for (int i = 0; i < 4; i++) {
			bytes[i] = (byte) (n >>> (i * 8));
		}

		return bytes;
	}
	
}
