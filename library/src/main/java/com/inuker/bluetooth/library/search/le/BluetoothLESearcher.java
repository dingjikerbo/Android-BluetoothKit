package com.inuker.bluetooth.library.search.le;

import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;

import com.inuker.bluetooth.library.connect.XmBluetoothDevice;
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
            XmBluetoothDevice xmDevice = new XmBluetoothDevice(device, rssi, scanRecord, XmBluetoothDevice.DEVICE_TYPE_BLE);
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

    @SuppressWarnings("deprecation")
    @Override
    public void startScanBluetooth(BluetoothSearchResponse response) {
        // TODO Auto-generated method stub
        super.startScanBluetooth(response);

        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void stopScanBluetooth() {
        // TODO Auto-generated method stub
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        super.stopScanBluetooth();
    }

    @SuppressWarnings("deprecation")
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
