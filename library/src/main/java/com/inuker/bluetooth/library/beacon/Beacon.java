package com.inuker.bluetooth.library.beacon;

import com.inuker.bluetooth.library.utils.ByteUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by dingjikerbo on 2016/9/5.
 */
public class Beacon {

    public byte[] mBytes;

    public List<BeaconItem> mItems;

    public Beacon(byte[] scanRecord) {
        mItems = new LinkedList<BeaconItem>();
        if (!ByteUtils.isEmpty(scanRecord)) {
            mBytes = ByteUtils.trimLast(scanRecord);
            mItems.addAll(BeaconParser.parseBeacon(mBytes));
        }
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("preParse: %s\npostParse:\n", ByteUtils.byteToString(mBytes)));

        for (int i = 0; i < mItems.size(); i++) {
            sb.append(mItems.get(i).toString());
            if (i != mItems.size() - 1) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }

}
