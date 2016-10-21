package com.inuker.bluetooth.library.connect.response;

import android.os.Bundle;

import com.inuker.bluetooth.library.IResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.BluetoothUtils;

/**
 * Created by dingjikerbo on 2015/12/31.
 */
public abstract class BluetoothResponse extends IResponse.Stub {

    public void onSafeResponse(int code, Bundle data) {
        try {
            onResponse(code, data);
        } catch (Throwable e) {
            BluetoothLog.e(e);
        }
    }

    public void onMainResponse(final int code, final Bundle data) {
        BluetoothUtils.post(new Runnable() {

            @Override
            public void run() {
                onSafeResponse(code, data);
            }
        });
    }
}
