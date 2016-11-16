package com.inuker.bluetooth.library.utils.hook.utils;

/**
 * Created by dingjikerbo on 2016/9/26.
 */
public class Validate {
    public static void isTrue(final boolean expression, final String message, final Object... values) {
        if (expression == false) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }
}
