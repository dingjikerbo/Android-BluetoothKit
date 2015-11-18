
package com.inuker.bluetooth.security.rc4;


public class CryptoUtils {

	// / Utility routine to fill a block with zeros.
	public static void zeroBlock(byte[] block, int off, int len) {
		for (int i = off; i < off + len; ++i)
			block[i] = 0;
	}

	// / Utility routine to fill a block with zeros.
	public static void zeroBlock(byte[] block) {
		zeroBlock(block, 0, block.length);
	}

	// / Utility routine to fill a block with random bytes.
	public static void randomBlock(byte[] block, int off, int len) {
		for (int i = off; i < off + len; ++i)
			block[i] = (byte) (Math.random() * 256.0);
	}

	// / Utility routine to fill a block with random bytes.
	public static void randomBlock(byte[] block) {
		randomBlock(block, 0, block.length);
	}

	// / Utility routine to XOR two blocks.
	public static void xorBlock(byte[] a, int aOff, byte[] b, int bOff,
			byte[] dst, int dstOff, int len) {
		for (int i = 0; i < len; ++i)
			dst[dstOff + i] = (byte) (a[aOff + i] ^ b[bOff + i]);
	}

	// / Utility routine to XOR two blocks.
	public static void xorBlock(byte[] a, byte[] b, byte[] dst) {
		xorBlock(a, 0, b, 0, dst, 0, a.length);
	}

	// / Utility routine to copy one block to another.
	public static void copyBlock(byte[] src, int srcOff, byte[] dst,
			int dstOff, int len) {
		for (int i = 0; i < len; ++i)
			dst[dstOff + i] = src[srcOff + i];
	}

	// / Utility routine to copy one block to another.
	public static void copyBlock(byte[] src, byte[] dst) {
		copyBlock(src, 0, dst, 0, src.length);
	}

	// / Utility routine to check two blocks for equality.
	public static boolean equalsBlock(byte[] a, int aOff, byte[] b, int bOff,
			int len) {
		for (int i = 0; i < len; ++i)
			if (a[aOff + i] != b[bOff + i])
				return false;
		return true;
	}

	// / Utility routine to check two blocks for equality.
	public static boolean equalsBlock(byte[] a, byte[] b) {
		return equalsBlock(a, 0, b, 0, a.length);
	}

	// / Utility routine fill a block with a given byte.
	public static void fillBlock(byte[] block, int blockOff, byte b, int len) {
		for (int i = blockOff; i < blockOff + len; ++i)
			block[i] = b;
	}

	// / Utility routine fill a block with a given byte.
	public static void fillBlock(byte[] block, byte b) {
		fillBlock(block, 0, b, block.length);
	}

	// / Squash bytes down to ints.
	public static void squashBytesToInts(byte[] inBytes, int inOff,
			int[] outInts, int outOff, int intLen) {
		for (int i = 0; i < intLen; ++i)
			outInts[outOff + i] = ((inBytes[inOff + i * 4] & 0xff) << 24)
					| ((inBytes[inOff + i * 4 + 1] & 0xff) << 16)
					| ((inBytes[inOff + i * 4 + 2] & 0xff) << 8)
					| (inBytes[inOff + i * 4 + 3] & 0xff);
	}

	// / Spread ints into bytes.
	public static void spreadIntsToBytes(int[] inInts, int inOff,
			byte[] outBytes, int outOff, int intLen) {
		for (int i = 0; i < intLen; ++i) {
			outBytes[outOff + i * 4] = (byte) (inInts[inOff + i] >>> 24);
			outBytes[outOff + i * 4 + 1] = (byte) (inInts[inOff + i] >>> 16);
			outBytes[outOff + i * 4 + 2] = (byte) (inInts[inOff + i] >>> 8);
			outBytes[outOff + i * 4 + 3] = (byte) inInts[inOff + i];
		}
	}

	// / Squash bytes down to shorts.
	public static void squashBytesToShorts(byte[] inBytes, int inOff,
			int[] outShorts, int outOff, int shortLen) {
		for (int i = 0; i < shortLen; ++i)
			outShorts[outOff + i] = ((inBytes[inOff + i * 2] & 0xff) << 8)
					| (inBytes[inOff + i * 2 + 1] & 0xff);
	}

	// / Spread shorts into bytes.
	public static void spreadShortsToBytes(int[] inShorts, int inOff,
			byte[] outBytes, int outOff, int shortLen) {
		for (int i = 0; i < shortLen; ++i) {
			outBytes[outOff + i * 2] = (byte) (inShorts[inOff + i] >>> 8);
			outBytes[outOff + i * 2 + 1] = (byte) inShorts[inOff + i];
		}
	}

	// / Convert a block to a String representation.
	public static String toStringBlock(byte[] block, int off, int len) {
		String hexits = "0123456789abcdef";
		StringBuffer buf = new StringBuffer();
		for (int i = off; i < off + len; ++i) {
			buf.append(hexits.charAt((block[i] >>> 4) & 0xf));
			buf.append(hexits.charAt(block[i] & 0xf));
		}
		return "[" + buf + "]";
	}

	// / Convert a block to a String representation.
	public static String toStringBlock(byte[] block) {
		return toStringBlock(block, 0, block.length);
	}

}