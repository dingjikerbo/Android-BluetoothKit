
package com.xiaomi.smarthome.device.bluetooth.security;

import android.text.TextUtils;

import com.inuker.bluetooth.library.utils.ByteUtils;

public class BLECipher {

    static {
        System.loadLibrary("blecipher");
    }

    public static byte[] encrypt(byte[] key, byte[] input) {
        if (ByteUtils.isEmpty(key) || ByteUtils.isEmpty(input)) {
            return ByteUtils.EMPTY_BYTES;
        }
        byte[] output = new byte[input.length];

        if (nativeEncrypt(key, input, output) != 0) {
            return ByteUtils.EMPTY_BYTES;
        }

        return output;
    }

    /**
     * 生成t1
     * @param mac
     * @param pid
     * @return
     */
    public static byte[] mixA(String mac, int pid) {
        if (TextUtils.isEmpty(mac) || pid < 0) {
            return ByteUtils.EMPTY_BYTES;
        }

        byte[] key = new byte[8];
        byte[] _mac = mac2Bytes(mac);
        byte[] _pid = pid2Bytes(pid);

        if (nativeMixA(_mac, _pid, key) != 0) {
            return ByteUtils.EMPTY_BYTES;
        }

        return key;
    }

    /**
     * 生成t2
     * @param mac
     * @param pid
     * @return
     */
    public static byte[] mixB(String mac, int pid) {
        if (TextUtils.isEmpty(mac) || pid < 0) {
            return ByteUtils.EMPTY_BYTES;
        }

        byte[] key = new byte[8];
        byte[] _mac = mac2Bytes(mac);
        byte[] _pid = pid2Bytes(pid);

        if (nativeMixB(_mac, _pid, key) != 0) {
            return ByteUtils.EMPTY_BYTES;
        }

        return key;
    }

    private native static int nativeEncrypt(byte[] key, byte[] input, byte[] output);

    private native static int nativeMixA(byte[] mac, byte[] pid, byte[] key);
    private native static int nativeMixB(byte[] mac, byte[] pid, byte[] key);

    private static byte[] mac2Bytes(String mac) {
        String[] macs = mac.split(":");

        int length = macs.length;
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[length - i - 1] = int2Byte(Integer.parseInt(macs[i], 16));
        }
        return bytes;
    }

    private static byte[] pid2Bytes(int pid) {
        byte[] bytes = new byte[2];
        bytes[0] = int2Byte(pid);
        bytes[1] = int2Byte(pid >>> 8);
        return bytes;
    }

    private static byte int2Byte(int n) {
        return (byte) (n & 0xff);
    }
}