package com.inuker.bluetooth.library.connect.request;

import android.os.Handler;

public interface IBleDispatch {

    void notifyWorkerResult(BleRequest request, boolean success);
    void notifyHandlerReady(Handler handler);
}