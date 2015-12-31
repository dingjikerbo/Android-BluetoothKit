package com.dingjikerbo.bluetooth.library.search;

import android.bluetooth.BluetoothAdapter;

import com.dingjikerbo.bluetooth.library.connect.XmBluetoothDevice;
import com.dingjikerbo.bluetooth.library.search.classic.BluetoothClassicSearcher;
import com.dingjikerbo.bluetooth.library.search.le.BluetoothLESearcher;
import com.dingjikerbo.bluetooth.library.utils.BluetoothConstants;

public class BluetoothSearcher {

    protected BluetoothAdapter mBluetoothAdapter;
    protected BluetoothSearchResponse mSearchResponse;

    public static BluetoothSearcher newInstance(int type) {
        switch (type) {
            case BluetoothConstants.SEARCH_TYPE_CLASSIC:
                return BluetoothClassicSearcher.getInstance();
            case BluetoothConstants.SEARCH_TYPE_BLE:
                return BluetoothLESearcher.getInstance();
            default:
                throw new IllegalStateException(String.format(
                        "unknown search type %d", type));
        }
    }

    protected void startScanBluetooth(BluetoothSearchResponse callback) {
        mSearchResponse = callback;
        notifySearchStarted();
    }

    protected void stopScanBluetooth() {
        notifySearchStopped();
    }

    protected void cancelScanBluetooth() {
        notifySearchCanceled();
    }

    private void notifySearchStarted() {
        if (mSearchResponse != null) {
            mSearchResponse.onSearchStarted();
        }
    }

    protected void notifyDeviceFounded(XmBluetoothDevice device) {
        if (mSearchResponse != null) {
            mSearchResponse.onDeviceFounded(device);
        }
    }

    private void notifySearchStopped() {
        if (mSearchResponse != null) {
            mSearchResponse.onSearchStopped();
        }
    }

    private void notifySearchCanceled() {
        if (mSearchResponse != null) {
            mSearchResponse.onSearchCanceled();
        }
    }
}
