package com.inuker.bluetooth.library.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by dingjikerbo on 2016/1/13.
 */
public class MD5Utils {

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
}
