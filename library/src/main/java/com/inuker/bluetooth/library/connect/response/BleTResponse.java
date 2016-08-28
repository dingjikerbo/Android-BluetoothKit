package com.inuker.bluetooth.library.connect.response;

import com.inuker.bluetooth.library.IBluetoothConstants;

/**
 * Created by liwentian on 2016/8/28.
 */
public interface BleTResponse<T> extends IBluetoothConstants {
    void onResponse(int code, T data);
}
