package com.inuker.bluetooth.library.beacon;

import com.inuker.bluetooth.library.utils.ByteUtils;

/**
 * Created by dingjikerbo on 2016/9/5.
 */
public class BeaconItem {

    /**
     * 广播中声明的长度
     */
    public int len;

    /**
     * 广播中声明的type
     */
    public int type;

    /**
     * 广播中的数据部分
     */
    public byte[] bytes;

    @Override
    public String toString() {
        String format = "";

        StringBuilder sb = new StringBuilder();

//        sb.append(String.format("len: %02d", len));
        sb.append(String.format("@Len = %02X, @Type = 0x%02X", len, type));

        switch (type) {
            case 8:
            case 9:
                format = "%c";
                break;
            default:
                format = "%02X ";
                break;
        }

        sb.append(" -> ");

        StringBuilder sbSub = new StringBuilder();
        try {
            for (byte b : bytes) {
                sbSub.append(String.format(format, b & 0xff));
            }
            sb.append(sbSub.toString());
        } catch (Exception e) {
            sb.append(ByteUtils.byteToString(bytes));
        }

        return sb.toString();
    }
}
