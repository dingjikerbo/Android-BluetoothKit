package com.inuker.bluetooth.library;

import android.os.Handler;

import com.inuker.bluetooth.library.connect.request.BleRequest;

public interface IBleDispatch {

    void notifyWorkerResult(BleRequest request, boolean success);
    void notifyHandlerReady(Handler handler);
}
