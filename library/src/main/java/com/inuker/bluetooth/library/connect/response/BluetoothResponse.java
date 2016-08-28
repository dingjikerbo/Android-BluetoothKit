package com.inuker.bluetooth.library.connect.response;

import android.os.Bundle;
import android.os.RemoteException;

import com.inuker.bluetooth.library.IBluetoothConstants;
import com.inuker.bluetooth.library.IResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.BluetoothUtils;

/**
 * Created by dingjikerbo on 2015/12/31.
 */
public abstract class BluetoothResponse extends IResponse.Stub implements IBluetoothConstants {

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
