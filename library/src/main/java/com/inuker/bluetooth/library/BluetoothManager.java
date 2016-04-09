package com.inuker.bluetooth.library;

import android.os.RemoteException;

/**
 * Created by liwentian on 2015/10/29.
 */
public class BluetoothManager extends IBluetoothManager.Stub {

    private static BluetoothManager sInstance;

    private BluetoothManager() {

    }

    public static BluetoothManager getInstance() {
        if (sInstance == null) {
            synchronized (BluetoothManager.class) {
                if (sInstance == null) {
                    sInstance = new BluetoothManager();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void connect(String mac, IBleResponse response) throws RemoteException {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        response.onResponse(1, null);
    }

    @Override
    public void disconnect(String mac) throws RemoteException {

    }

    @Override
    public void read(String mac, int service, int character, IBleResponse response) throws RemoteException {

    }

    @Override
    public void write(String mac, int service, int character, byte[] bytes, IBleResponse response) throws RemoteException {

    }

    @Override
    public void notify(String mac, int service, int character, IBleResponse response) throws RemoteException {

    }

    @Override
    public void unnotify(String mac, int service, int character) throws RemoteException {

    }

    @Override
    public void readRemoteRssi(String mac, IBleResponse response) throws RemoteException {

    }
}
