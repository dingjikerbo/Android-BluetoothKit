// IBluetoothManager.aidl
package com.inuker.bluetooth.library;

// Declare any non-default types here with import statements

import com.inuker.bluetooth.library.IBleResponse;

interface IBluetoothManager {

    void connect(String mac, IBleResponse response);

    void disconnect(String mac);

    void read(String mac, int service, int character, IBleResponse response);

    void write(String mac, int service, int character, in byte[] bytes, IBleResponse response);

    void notify(String mac, int service, int character, IBleResponse response);

    void unnotify(String mac, int service, int character);

    void readRemoteRssi(String mac, IBleResponse response);
}
