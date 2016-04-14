package com.inuker.bluetooth.library.connect.response;

/**
 * Created by liwentian on 2016/4/14.
 */
public interface BleResponse<T> {
    public void onResponse(int code, T data);
}
