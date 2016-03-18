package com.inuker.bluetooth.library.search;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.inuker.bluetooth.library.utils.BluetoothConstants;

public abstract class BluetoothSearchTask {

    private static final int MSG_SEARCH_TIMEOUT = 0x22;

    private int searchType;
    private int searchDuration;

    private BluetoothSearcher mBluetoothSearcher;

    private Handler mHandler = new Handler(Looper.myLooper()) {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case MSG_SEARCH_TIMEOUT:
                    mBluetoothSearcher.stopScanBluetooth();
                    break;
            }

        }

    };

    public BluetoothSearchTask(int type) {
        this(type, BluetoothConstants.DEFAULT_DURATION);
    }

    public BluetoothSearchTask(int type, int duration) {
        setSearchType(type);
        setSearchDuration(duration);
    }

    public int getSearchType() {
        return searchType;
    }

    public void setSearchType(int searchType) {
        this.searchType = searchType;
    }

    public int getSearchDuration() {
        return searchDuration;
    }

    public void setSearchDuration(int searchDuration) {
        this.searchDuration = searchDuration;
    }

    public boolean isBluetoothLeSearch() {
        return searchType == BluetoothConstants.SEARCH_TYPE_BLE;
    }

    public boolean isBluetoothClassicSearch() {
        return searchType == BluetoothConstants.SEARCH_TYPE_CLASSIC;
    }

    public void start(BluetoothSearchResponse response) {
        mBluetoothSearcher = BluetoothSearcher.newInstance(searchType);
        mBluetoothSearcher.startScanBluetooth(response);

        mHandler.sendEmptyMessageDelayed(MSG_SEARCH_TIMEOUT, searchDuration);
    }

    public void cancel() {
        mHandler.removeCallbacksAndMessages(null);
        mBluetoothSearcher.cancelScanBluetooth();
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        String type = "";
        if (isBluetoothLeSearch()) {
            type = "Ble";
        } else if (isBluetoothClassicSearch()) {
            type = "classic";
        } else {
            type = "unknown";
        }

        if (searchDuration >= 1000) {
            return String.format("%s search (%ds)", type, searchDuration / 1000);
        } else {
            return String.format("%s search (%.1fs)", type, 1.0 * searchDuration / 1000);
        }
    }

}
