package com.inuker.bluetooth.library.connect.response;

/**
 * Created by liwentian on 2015/12/31.
 */
public interface BleResponse<T> {
    void onResponse(int code, T data);
}
