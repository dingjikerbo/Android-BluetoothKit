package com.inuker.bluetooth.advertise;

import com.inuker.bluetooth.utils.ByteUtils;

public class Pdu {

	@SuppressWarnings("unused")
	private static final String TAG = "Pdu";

	private byte mType;

	private int mDeclaredLength;

	private int mStartIndex;

	private int mEndIndex;

	private byte[] mBytes;

	/**
	 * Parse a PDU from a byte array looking offset by startIndex
	 * 
	 * @param bytes
	 * @param startIndex
	 * @return
	 */

	public static Pdu parse(byte[] bytes, int startIndex) {
		Pdu pdu = null;
		if (bytes.length - startIndex >= 2) {
			byte length = bytes[startIndex];
			if (length > 0) {
				byte type = bytes[startIndex + 1];
				int firstIndex = startIndex + 2;
				if (firstIndex < bytes.length) {
					pdu = new Pdu();
					pdu.mEndIndex = firstIndex + length - 2;
					if (pdu.mEndIndex >= bytes.length) {
						pdu.mEndIndex = bytes.length - 1;
					}
					pdu.mType = type;
					pdu.mDeclaredLength = length;
					pdu.mStartIndex = firstIndex;
					pdu.mBytes = bytes;
				}
			}
		}

		return pdu;
	}

	/**
	 * PDU type field
	 * 
	 * @return
	 */
	public byte getType() {
		return mType;
	}

	/**
	 * PDU length from header
	 * 
	 * @return
	 */
	public int getDeclaredLength() {
		return mDeclaredLength;
	}

	/**
	 * Actual PDU length (may be less than declared length if fewer bytes are
	 * actually available.)
	 * 
	 * @return
	 */
	public int getActualLength() {
		return mEndIndex - mStartIndex + 1;
	}

	/**
	 * Start index within byte buffer of PDU
	 * 
	 * @return
	 */
	public int getStartIndex() {
		return mStartIndex;
	}

	/**
	 * End index within byte buffer of PDU
	 * 
	 * @return
	 */
	public int getEndIndex() {
		return mEndIndex;
	}

	public byte getByte(int offset) {
		int index = mStartIndex + offset + 2;
		if (index >= mBytes.length) {
			throw new IllegalArgumentException("getByte offset overflow!");
		}
		return mBytes[index];
	}

	public byte[] getBytes(int start, int length) {
        if (start < 0 || length <= 0) {
            return ByteUtils.EMPTY_BYTES;
        }

        int startIndex = mStartIndex + start + 2;

        if (startIndex >= mBytes.length) {
            return ByteUtils.EMPTY_BYTES;
        }

        if (startIndex + length > mBytes.length) {
            length = mBytes.length - startIndex;
        }

        byte[] bytes = new byte[length];

        for (int i = 0; i < length; i++) {
            bytes[i] = getByte(start + i);
        }
        
        return bytes;
    }

	public byte[] getDataBytes() {
		return getBytes(0, mEndIndex - mStartIndex - 1);
	}

	public String getValue() {
		String format = "";

		int type = mType & 0xff;

		switch (type) {
		case 8:
		case 9:
			format = "%c";
			break;
		default:
			format = "%02x ";
			break;
		}

		StringBuilder sb = new StringBuilder();
		for (int i = mStartIndex; i <= mEndIndex; i++) {
			sb.append(String.format(format, mBytes[i]));
		}

		return sb.toString();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("len: %d", mDeclaredLength));
		sb.append(String.format(", type: 0x%x, ", mType));
		sb.append(getValue());

		return sb.toString();
	}
}
