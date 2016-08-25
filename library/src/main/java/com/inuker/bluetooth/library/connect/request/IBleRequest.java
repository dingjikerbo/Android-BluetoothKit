package com.inuker.bluetooth.library.connect.request;

import com.inuker.bluetooth.library.connect.IBleConnectWorker;

/**
 * Created by dingjikerbo on 16/8/25.
 */
public interface IBleRequest {

    void process(IBleConnectWorker worker);
}
