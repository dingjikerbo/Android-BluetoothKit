package com.inuker.library.connect.request;

import com.inuker.library.connect.BleResponser;

/**
 * Created by liwentian on 2015/12/23.
 */
public class BleReadRssiRequest extends BleRequest {

    public BleReadRssiRequest(BleResponser response) {
        super(response);
        mRequestType = REQUEST_TYPE_READ_RSSI;
    }
}
