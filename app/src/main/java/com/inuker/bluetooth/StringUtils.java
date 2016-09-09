package com.inuker.bluetooth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

/**
 * Created by dingjikerbo on 2016/9/1.
 */
public class StringUtils {

    private static Random rnd = new Random();

    public static String toMd5(String src) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(src.getBytes());
            return toHexString(algorithm.digest(), "");
        } catch (NoSuchAlgorithmException e) {
            return "error";
        }
    }

    public static String toHexString(byte[] bytes, String separator) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            int byteValue = 0xFF & b;
            if (byteValue < 0x10) {
                hexString.append("0" + Integer.toHexString(0xFF & b)).append(separator);
            } else {
                hexString.append(Integer.toHexString(0xFF & b)).append(separator);
            }
        }
        return hexString.toString();
    }

    public static String urlEncode(String str) throws UnsupportedEncodingException {
        if (str == null) {
            str = "";
        }

        return URLEncoder.encode(str, "utf-8").replaceAll("\\+", "%20").replaceAll("%7E", "~")
                .replaceAll("\\*", "%2A");
    }

    /**
     * ��ȡָ�����������ַ�һ�����ĳ���Ϊ2��Ӣ���ַ�ȣ�
     *
     * @param str
     * @param subLength
     * @return
     */
    public static String subString(String str, int subLength) {
        int n = 0;
        int i = 0;
        int j = 0;
        int byteNum = subLength * 2;
        boolean flag = true;
        if (str == null) {
            return "";
        }

        for (i = 0; i < str.length(); i++) {
            if ((int) (str.charAt(i)) < 128) {
                n += 1;
            } else {
                n += 2;
            }
            if (n > byteNum && flag) {
                j = i;
                flag = false;
            }
            if (n >= byteNum + 2) {
                break;
            }
        }

        if (n >= byteNum + 2 && i != str.length() - 1) {
            str = str.substring(0, j);
            str += "...";
        }
        return str;
    }

    public static String getTimeDisplayName(long ctimelong) {
        String r = "";

        Calendar currentTime = Calendar.getInstance();
        long currentTimelong = System.currentTimeMillis();

        Calendar publicCal = Calendar.getInstance();
        publicCal.setTimeInMillis(ctimelong);

        long timeDelta = currentTimelong - ctimelong;

        if (timeDelta < 60 * 60 * 1000L) {

            r = "今天";

        } else if (timeDelta < 24 * 60 * 60 * 1000L) {

            if (currentTime.get(Calendar.DAY_OF_YEAR) == publicCal.get(Calendar.DAY_OF_YEAR)) {
                r = "今天";
            } else {
                r = "昨天";
            }

        } else if (timeDelta < 2 * 24 * 60 * 60 * 1000L) {

            if (currentTime.get(Calendar.DAY_OF_YEAR) == publicCal.get(Calendar.DAY_OF_YEAR) + 1) {
                r = "昨天";
            } else {
                r = "前天";
            }

        } else if (timeDelta < 3 * 24 * 60 * 60 * 1000L) {

            if (currentTime.get(Calendar.DAY_OF_YEAR) == publicCal.get(Calendar.DAY_OF_YEAR) + 2) {
                r = "前天";
            } else {
                r = new SimpleDateFormat("yy年MM月dd日", Locale.CHINA).format(ctimelong);
            }
        } else {
            r = new SimpleDateFormat("yy年MM月dd日", Locale.CHINA).format(ctimelong);
        }
        return r;
    }

    public static String getTimeDisplayNameNormal(long ctimelong) {
        String r = "";

        Calendar currentTime = Calendar.getInstance();
        long currentTimelong = System.currentTimeMillis();

        Calendar publicCal = Calendar.getInstance();
        publicCal.setTimeInMillis(ctimelong);

        long timeDelta = currentTimelong - ctimelong;

        if (timeDelta < 60 * 1000L) {

            r = "刚刚";

        } else if (timeDelta < 60 * 60 * 1000L) {

            r = timeDelta / (60 * 1000L) + "分钟前";

        } else if (timeDelta < 24 * 60 * 60 * 1000L) {

            if (currentTime.get(Calendar.DAY_OF_YEAR) == publicCal.get(Calendar.DAY_OF_YEAR)) {
                r = timeDelta / (60 * 60 * 1000L) + "小时前";
            } else {
                r = "昨天";
            }

        } else if (timeDelta < 2 * 24 * 60 * 60 * 1000L) {

            if (currentTime.get(Calendar.DAY_OF_YEAR) == publicCal.get(Calendar.DAY_OF_YEAR) + 1) {
                r = "昨天";
            } else {
                r = "前天";
            }

        } else if (timeDelta < 3 * 24 * 60 * 60 * 1000L) {

            if (currentTime.get(Calendar.DAY_OF_YEAR) == publicCal.get(Calendar.DAY_OF_YEAR) + 2) {
                r = "前天";
            } else {
                r = new SimpleDateFormat("yy年MM月dd日", Locale.CHINA).format(ctimelong);
            }
        } else {
            r = new SimpleDateFormat("yy年MM月dd日", Locale.CHINA).format(ctimelong);
        }
        return r;
    }

    public static String getTimeDisplayNameCompact(long ctimelong) {
        String r = "";

        Calendar currentTime = Calendar.getInstance();
        long currentTimelong = System.currentTimeMillis();

        Calendar publicCal = Calendar.getInstance();
        publicCal.setTimeInMillis(ctimelong);

        long timeDelta = currentTimelong - ctimelong;

        if (timeDelta <= 0L) {

            r = "刚刚";

        } else if (timeDelta < 60 * 1000L) {

            r = timeDelta / 1000L + "秒前";

        } else if (timeDelta < 60 * 60 * 1000L) {

            r = timeDelta / (60 * 1000L) + "分钟前";

        } else if (timeDelta < 24 * 60 * 60 * 1000L) {

            // if (currentTime.get(Calendar.DAY_OF_YEAR) ==
            // publicCal.get(Calendar.DAY_OF_YEAR)) {
            r = timeDelta / (60 * 60 * 1000L) + "小时前";
            // } else {
            // r = "昨天 " + new SimpleDateFormat("HH:mm").format(ctimelong);
            // }

        } else if (timeDelta < 2 * 24 * 60 * 60 * 1000L) {

            if (currentTime.get(Calendar.DAY_OF_YEAR) == publicCal.get(Calendar.DAY_OF_YEAR) + 1) {
                r = "昨天" + new SimpleDateFormat("HH:mm").format(ctimelong);
            } else {
                r = "前天" + new SimpleDateFormat("HH:mm").format(ctimelong);
            }

        } else if (timeDelta < 3 * 24 * 60 * 60 * 1000L) {

            if (currentTime.get(Calendar.DAY_OF_YEAR) == publicCal.get(Calendar.DAY_OF_YEAR) + 2) {
                r = "前天" + new SimpleDateFormat("HH:mm").format(ctimelong);
            } else {
                r = new SimpleDateFormat("MM月dd日").format(ctimelong);
            }

        } else {
            r = new SimpleDateFormat("MM月dd日").format(ctimelong);
        }
        return r;
    }

    public static boolean isNullOrEmpty(String str) {
        if (str == null || "".equals(str)) {
            return true;
        } else {
            return false;
        }
    }

    public static String getRandomNum() {
        return rnd.nextInt(Integer.MAX_VALUE) + "";
    }

    public static String getNonNullString(String text) {
        return text != null ? text : "";
    }

    public static String getNonNullNumString(String text) {
        return text != null ? text : "0";
    }
}
