package com.inuker.bluetooth.library.search.le;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.os.Build;

import com.inuker.bluetooth.library.search.BluetoothSearchDevice;
import com.inuker.bluetooth.library.search.BluetoothSearchResponse;
import com.inuker.bluetooth.library.search.BluetoothSearcher;
import com.inuker.bluetooth.library.utils.BluetoothUtils;

/**
 * @author liwentian
 */
public class BluetoothLESearcher extends BluetoothSearcher {

    private final LeScanCallback mLeScanCallback = new LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            // TODO Auto-generated method stub
            BluetoothSearchDevice xmDevice = new BluetoothSearchDevice(device, rssi, scanRecord, BluetoothSearchDevice.DEVICE_TYPE_BLE);
            xmDevice.name = device.getName();
            notifyDeviceFounded(xmDevice);
        }

    };

    private BluetoothLESearcher() {
        mBluetoothAdapter = BluetoothUtils.getBluetoothLeAdapter();
    }

    public static BluetoothLESearcher getInstance() {
        return BluetoothLESearcherHolder.instance;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressWarnings("deprecation")
    @Override
    public void startScanBluetooth(BluetoothSearchResponse response) {
        // TODO Auto-generated method stub
        super.startScanBluetooth(response);

        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    @Override
    public void stopScanBluetooth() {
        // TODO Auto-generated method stub
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        super.stopScanBluetooth();
    }

    @Override
    protected void cancelScanBluetooth() {
        // TODO Auto-generated method stub
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        super.cancelScanBluetooth();
    }

    private static class BluetoothLESearcherHolder {
        private static BluetoothLESearcher instance = new BluetoothLESearcher();
    }
}
