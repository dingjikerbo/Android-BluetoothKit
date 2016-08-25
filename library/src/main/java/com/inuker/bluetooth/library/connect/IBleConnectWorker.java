package com.inuker.bluetooth.library.connect;

import android.os.Bundle;

/**
 * Created by dingjikerbo on 16/8/25.
 */
public interface IBleConnectWorker {

    void registerGattResponseListener(int responseId, GattResponseListener listener);

    void unregisterGattResponseListener(int responseId);

    void notifyRequestResult(int code, Bundle data);

}
