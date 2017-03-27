package com.example;

public class MyClass {


    public static int toInt(byte[] bytes) {
        int val = 0;

        for (int i = 0; i < 4; i++) {
            long mask = (1L << ((i + 1) * 8)) - 1;
            val += (bytes[i] << (i * 8)) & mask;
        }

        return val;
    }

    public static void main(String[] args) {
        byte[] bytes = fromInt(0x90CA85DE);
        int value = toInt(bytes);
        System.out.println(String.format("%X", value));
    }

    public static byte[] fromInt(int n) {
        byte[] bytes = new byte[4];

        for (int i = 0; i < 4; i++) {
            bytes[i] = (byte) (n >>> (i * 8));
        }

        return bytes;
    }
}
