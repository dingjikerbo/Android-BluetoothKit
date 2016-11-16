package com.inuker.bluetooth.library.connect;

import com.inuker.bluetooth.library.connect.request.BleRequest;

public interface IBleConnectDispatcher {

    void onRequestCompleted(BleRequest request);
}
