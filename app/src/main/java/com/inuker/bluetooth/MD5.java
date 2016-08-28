package com.inuker.bluetooth;

import com.inuker.bluetooth.library.utils.ByteUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by dingjikerbo on 2016/8/27.
 */
public class MD5 {

    private static String byte2Hex(byte b) {
        int value = (b & 0x7F) + (b < 0 ? 0x80 : 0);
        return (value < 0x10 ? "0" : "") + Integer.toHexString(value).toLowerCase();
    }

    public static String MD5_32(String passwd) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        StringBuilder strbuf = new StringBuilder();

        md5.update(passwd.getBytes(), 0, passwd.length());
        byte[] digest = md5.digest();

        for (byte aDigest : digest) {
            strbuf.append(byte2Hex(aDigest));
        }

        return strbuf.toString();
    }

    public static byte[] MD5_4_bytes(String text) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        md5.update(text.getBytes(), 0, text.length());
        return Arrays.copyOfRange(md5.digest(), 6, 10);
    }

    public static byte[] MD5_12(String text) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        md5.update(text.getBytes(), 0, text.length());

        byte[] bytes = md5.digest();
        int length = bytes.length;
        if (length >= 12) {
            return Arrays.copyOfRange(bytes, length / 2 - 6,
                    length / 2 + 6);
        } else {
            return ByteUtils.EMPTY_BYTES;
        }
    }

    public static String MD5_16(String passwd) {
        return MD5_32(passwd).subSequence(8, 24).toString();
    }
}
