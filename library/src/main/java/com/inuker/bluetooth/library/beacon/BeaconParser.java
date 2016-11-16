package com.inuker.bluetooth.library.beacon;

import com.inuker.bluetooth.library.utils.ByteUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dingjikerbo on 2016/11/16.
 */

public class BeaconParser {

    private byte[] bytes;

    private ByteBuffer mByteBuffer;

    public BeaconParser(BeaconItem item) {
        this.bytes = item.bytes;
        mByteBuffer = ByteBuffer.wrap(bytes).order(
                ByteOrder.LITTLE_ENDIAN);
    }

    public void setPosition(int position) {
        mByteBuffer.position(position);
    }

    public int readByte() {
        return mByteBuffer.get() & 0xff;
    }

    public int readShort() {
        return mByteBuffer.getShort() & 0xffff;
    }

    public boolean getBit(int n, int index) {
        return (n & (1 << index)) != 0;
    }

    public static List<BeaconItem> parseBeacon(byte[] bytes) {
        ArrayList<BeaconItem> items = new ArrayList<BeaconItem>();

        for (int i = 0; i < bytes.length; ) {
            BeaconItem item = parse(bytes, i);
            if (item != null) {
                items.add(item);
                i += item.len + 1;
            } else {
                break;
            }
        }

        return items;
    }

    private static BeaconItem parse(byte[] bytes, int startIndex) {
        BeaconItem item = null;

        if (bytes.length - startIndex >= 2) {
            byte length = bytes[startIndex];
            if (length > 0) {
                byte type = bytes[startIndex + 1];
                int firstIndex = startIndex + 2;

                if (firstIndex < bytes.length) {
                    item = new BeaconItem();

                    int endIndex = firstIndex + length - 2;

                    if (endIndex >= bytes.length) {
                        endIndex = bytes.length - 1;
                    }

                    item.type = type & 0xff;
                    item.len = length;

                    item.bytes = ByteUtils.getBytes(bytes, firstIndex, endIndex);
                }
            }
        }

        return item;
    }
}
