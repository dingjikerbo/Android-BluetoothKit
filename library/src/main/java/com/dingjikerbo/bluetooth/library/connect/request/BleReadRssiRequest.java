package com.dingjikerbo.bluetooth.library.connect.request;

import com.dingjikerbo.bluetooth.library.connect.response.BleReadRssiResponse;

/**
 * Created by liwentian on 2015/12/23.
 */
public class BleReadRssiRequest extends BleRequest<Integer> {

    public BleReadRssiRequest(BleReadRssiResponse response) {
        super(response);
        mRequestType = REQUEST_TYPE_READ_RSSI;
    }
}
