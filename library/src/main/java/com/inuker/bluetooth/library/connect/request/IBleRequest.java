package com.inuker.bluetooth.library.connect.request;

import com.inuker.bluetooth.library.IBluetoothConstants;
import com.inuker.bluetooth.library.connect.IBleRequestProcessor;

/**
 * Created by dingjikerbo on 16/8/25.
 */
public interface IBleRequest extends IBluetoothConstants {
    void process(IBleRequestProcessor processor);
}
