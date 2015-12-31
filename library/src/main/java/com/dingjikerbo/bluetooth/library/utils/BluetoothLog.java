package com.dingjikerbo.bluetooth.library.utils;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by liwentian on 2015/12/16.
 */
public class BluetoothLog {

    private static final String LOG_TAG = "miio-bluetooth";

    /**
     * 这个是模块级别或类级别的记录
     */
    public static void i(String msg) {
        Log.i(LOG_TAG, msg);
    }

    /**
     * 错误日志
     *
     * @param msg
     */
    public static void e(String msg) {
        Log.e(LOG_TAG, msg);
    }

    /**
     * 这个是能看到函数内部的运行状况
     *
     * @param msg
     */
    public static void v(String msg) {
        Log.v(LOG_TAG, msg);
    }

    /**
     * 这个是能看到函数的执行路径
     *
     * @param msg
     */
    public static void d(String msg) {
        Log.d(LOG_TAG, msg);
    }

    /**
     * 重要日志，为了醒目所以标注warning
     *
     * @param msg
     */
    public static void w(String msg) {
        Log.w(LOG_TAG, msg);
    }

    public static void e(Throwable e) {
        e(getThrowableString(e));
    }

    public static void w(Throwable e) {
        w(getThrowableString(e));
    }

    private static String getThrowableString(Throwable e) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);

        while (e != null) {
            e.printStackTrace(printWriter);
            e = e.getCause();
        }

        String text = writer.toString();

        printWriter.close();

        return text;
    }
}
