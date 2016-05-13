package com.inuker.library.connect;

import android.os.Handler;

import com.inuker.library.connect.request.BleRequest;

public interface IBleDispatch {

    void notifyWorkerResult(BleRequest request, boolean success);
    void notifyHandlerReady(Handler handler);
}
