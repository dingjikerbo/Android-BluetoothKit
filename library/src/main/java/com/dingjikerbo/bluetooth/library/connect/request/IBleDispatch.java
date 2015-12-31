package com.dingjikerbo.bluetooth.library.connect.request;

import android.os.Handler;

public interface IBleDispatch {

    void notifyWorkerResult(boolean success);

    void notifyHandlerReady(Handler handler);
}
