package com.inuker.bluetooth.library.beacon;

import com.inuker.bluetooth.library.utils.ByteUtils;
import com.inuker.bluetooth.library.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwentian on 2016/9/5.
 */
public class Beacon {

    private byte[] mBytes;

    private List<BeaconItem> mItems;

    public Beacon(byte[] scanRecord) {
        mItems = new ArrayList<BeaconItem>();

        if (!ByteUtils.isEmpty(scanRecord)) {
            mBytes = scanRecord;

            try {
                List<BeaconItem> items = parseAdvertisement(mBytes);
                if (!ListUtils.isEmpty(items)) {
                    mItems.addAll(items);
                }
            } catch (Throwable e) {

            }
        }
    }

    private List<BeaconItem> parseAdvertisement(byte[] bytes) {
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

    private BeaconItem parse(byte[] bytes, int startIndex) {
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

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < mItems.size(); i++) {
            sb.append(mItems.get(i).toString());
            if (i != mItems.size() - 1) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }

}
