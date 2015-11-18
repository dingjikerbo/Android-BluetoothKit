package com.inuker.bluetooth.utils;

import java.util.Random;

public class RandomUtils {

	 private static Random mRandom;
	
	public static double randFloat() {
        if (mRandom == null) {
            mRandom = new Random();
            mRandom.setSeed(System.currentTimeMillis());
        }
        return mRandom.nextDouble();
    }
}
