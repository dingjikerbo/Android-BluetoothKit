package com.dingjikerbo.bluetooth.library.response;

/**
 * Created by liwentian on 2015/12/31.
 */
public interface BleResponse<T> {
    public void onResponse(int code, T data);
}
