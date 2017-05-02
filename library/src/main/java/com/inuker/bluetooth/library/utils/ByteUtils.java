package com.inuker.bluetooth.library.utils;

import java.util.Arrays;

/**
 * Created by dingjikerbo on 2015/12/31.
 */
public class ByteUtils {

    public static final byte[] EMPTY_BYTES = new byte[]{};

    public static final int BYTE_MAX = 0xff;

    public static byte[] getNonEmptyByte(byte[] bytes) {
        return bytes != null ? bytes : EMPTY_BYTES;
    }

    public static String byteToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        if (!isEmpty(bytes)) {
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02X", bytes[i]));
            }
        }

        return sb.toString();
    }

    public static byte[] trimLast(byte[] bytes) {
        int i = bytes.length - 1;
        for ( ; i >= 0; i--) {
            if (bytes[i] != 0) {
                break;
            }
        }
        return Arrays.copyOfRange(bytes, 0, i + 1);
    }

    public static byte[] stringToBytes(String text) {
        int len = text.length();
        byte[] bytes = new byte[(len + 1) / 2];
        for (int i = 0; i < len; i += 2) {
            int size = Math.min(2, len - i);
            String sub = text.substring(i, i + size);
            bytes[i / 2] = (byte) Integer.parseInt(sub, 16);
        }
        return bytes;
    }

    public static boolean isEmpty(byte[] bytes) {
        return bytes == null || bytes.length == 0;
    }

    public static byte[] fromInt(int n) {
        byte[] bytes = new byte[4];

        for (int i = 0; i < 4; i++) {
            bytes[i] = (byte) (n >>> (i * 8));
        }

        return bytes;
    }

    public static boolean byteEquals(byte[] lbytes, byte[] rbytes) {
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

    public static byte[] fillBeforeBytes(byte[] bytes, int len, byte fill) {

        byte[] result = bytes;
        int oldLen = (bytes != null ? bytes.length : 0);

        if (oldLen < len) {
            result = new byte[len];

            for (int i = len - 1, j = oldLen - 1; i >= 0; i--, j--) {
                if (j >= 0) {
                    result[i] = bytes[j];
                } else {
                    result[i] = fill;
                }
            }
        }

        return result;
    }

    public static byte[] cutBeforeBytes(byte[] bytes, byte cut) {
        if (ByteUtils.isEmpty(bytes)) {
            return bytes;
        }

        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] != cut) {
                return Arrays.copyOfRange(bytes, i, bytes.length);
            }
        }

        return EMPTY_BYTES;
    }

    public static byte[] cutAfterBytes(byte[] bytes, byte cut) {
        if (ByteUtils.isEmpty(bytes)) {
            return bytes;
        }

        for (int i = bytes.length - 1; i >= 0; i--) {
            if (bytes[i] != cut) {
                return Arrays.copyOfRange(bytes, 0, i + 1);
            }
        }

        return EMPTY_BYTES;
    }

    public static byte[] getBytes(byte[] bytes, int start, int end) {
        if (bytes == null) {
            return null;
        }

        if (start < 0 || start >= bytes.length) {
            return null;
        }

        if (end < 0 || end >= bytes.length) {
            return null;
        }

        if (start > end) {
            return null;
        }

        byte[] newBytes = new byte[end - start + 1];

        for (int i = start; i <= end; i++) {
            newBytes[i - start] = bytes[i];
        }

        return newBytes;
    }

    public static int ubyteToInt(byte b) {
        return (int) b & 0xFF;
    }

    public static boolean isAllFF(byte[] bytes) {
        int len = (bytes != null ? bytes.length : 0);

        for (int i = 0; i < len; i++) {
            if (ubyteToInt(bytes[i]) != BYTE_MAX) {
                return false;
            }
        }

        return true;
    }

    public static byte[] fromLong(long n) {
        byte[] bytes = new byte[8];

        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (n >>> (i * 8));
        }

        return bytes;
    }

    public static void copy(byte[] lbytes, byte[] rbytes, int lstart, int rstart) {
        if (lbytes != null && rbytes != null && lstart >= 0) {
            for (int i = lstart, j = rstart; j < rbytes.length && i < lbytes.length; i++, j++) {
                lbytes[i] = rbytes[j];
            }
        }
    }

    public static boolean equals(byte[] array1, byte[] array2) {
        return equals(array1, array2, Math.min(array1.length, array2.length));
    }

    public static boolean equals(byte[] array1, byte[] array2, int len) {
        if (array1 == array2) {
            return true;
        }
        if (array1 == null || array2 == null || array1.length < len || array2.length < len) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            if (array1[i] != array2[i]) {
                return false;
            }
        }
        return true;
    }

    public static byte[] get(byte[] bytes, int offset) {
        return get(bytes, offset, bytes.length - offset);
    }

    public static byte[] get(byte[] bytes, int offset, int len) {
        byte[] result = new byte[len];
        System.arraycopy(bytes, offset, result, 0, len);
        return result;
    }

    public static byte[] fromShort(short n) {
        return new byte[] {
                (byte) n, (byte) (n >>> 8)
        };
    }
}
