package com.inuker.library.connect.response;

/**
 * Created by liwentian on 2016/4/13.
 */
public interface BleResponse<T> {
    void onResponse(int code, T data);
}
