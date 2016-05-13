package com.inuker.library.utils;

import java.util.Arrays;

/**
 * Created by liwentian on 2016/5/13.
 */
public class ByteUtils {

    public static final byte[] EMPTY_BYTES = new byte[] {};

    public static final int BYTE_MAX = 0xff;

    public static byte[] getNonEmptyByte(byte[] bytes) {
        return bytes != null ? bytes : EMPTY_BYTES;
    }

    public static String getText(byte[] bytes) {
        return new String(bytes);
    }

    /**
     * @param bytes 原始byte数组
     * @param start 前闭合区间
     * @param end 后闭合区间
     * @return 返回位于区间内的byte子数组
     */
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

    /**
     * @return 两个byte数组是否相等
     */
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

    /**
     * @return lbytes从start开始是否一直和rbytes匹配
     */
    public static boolean byteEquals(byte[] lbytes, int start, byte[] rbytes) {
        if (lbytes == null && rbytes == null) {
            return true;
        }

        if (lbytes == null || rbytes == null) {
            return false;
        }

        if (start < 0 || start >= lbytes.length) {
            return false;
        }

        int end = start + rbytes.length - 1;

        if (end < 0 || end >= lbytes.length) {
            return false;
        }

        for (int i = start; i <= end; i++) {
            if (lbytes[i] != rbytes[i - start]) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param data
     * @param index
     * @param flag true小端， false大端
     * @return
     */
    public static boolean getBit(byte data, int index, boolean flag) {
        if (flag) {
            return (data & (1 << index)) != 0;
        } else {
            return (data & (1 << (7 - index))) != 0;
        }
    }

    public static boolean getBit(byte data, int index) {
        return getBit(data, index, true);
    }

    public static String byteToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        if (!isEmpty(bytes)) {
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02x", bytes[i]));
            }
        }

        return sb.toString();
    }

    public static byte[] stringToBytes(String text) {
        int len = text.length();
        byte[] bytes = new byte[(len + 1) / 2];

        try {
            for (int i = 0; i < len; i += 2) {
                int size = Math.min(2, len - i);
                String sub = text.substring(i, i + size);
                bytes[i / 2] = (byte) Integer.parseInt(sub, 16);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return EMPTY_BYTES;
        }

        return bytes;
    }

    public static boolean isEmpty(byte[] bytes) {
        return bytes == null || bytes.length == 0;
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

    public static boolean isNormalBytes(byte[] data) {
        return !(ByteUtils.isEmpty(data) || ByteUtils.isAllFF(data));
    }

    public static byte[] fromInt(int n) {
        byte[] bytes = new byte[4];

        for (int i = 0; i < 4; i++) {
            bytes[i] = (byte) (n >>> (i * 8));
        }

        return bytes;
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
}
