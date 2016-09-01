package com.inuker.bluetooth.library.connect.response;

import com.inuker.bluetooth.library.IBluetoothBase;

/**
 * Created by dingjikerbo on 2016/8/28.
 */
public interface BleTResponse<T> extends IBluetoothBase {
    void onResponse(int code, T data);
}
