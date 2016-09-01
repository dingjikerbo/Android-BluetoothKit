package com.inuker.bluetooth.library.connect.request;

import com.inuker.bluetooth.library.IBluetoothBase;
import com.inuker.bluetooth.library.connect.IBleRequestProcessor;

/**
 * Created by dingjikerbo on 16/8/25.
 */
public interface IBleRequest extends IBluetoothBase {
    void process(IBleRequestProcessor processor);
}
