// IBluetoothManager.aidl
package com.inuker.bluetooth.library;

// Declare any non-default types here with import statements

import com.inuker.bluetooth.library.IResponse;

interface IBluetoothService {
    void callBluetoothApi(int code, inout Bundle args, IResponse response);
}
