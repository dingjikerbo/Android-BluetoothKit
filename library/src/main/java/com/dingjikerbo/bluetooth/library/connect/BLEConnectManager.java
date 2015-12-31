package com.dingjikerbo.bluetooth.library.connect;

import android.text.TextUtils;

import com.dingjikerbo.bluetooth.library.connect.request.Code;
import com.dingjikerbo.bluetooth.library.connect.response.BleConnectResponse;
import com.dingjikerbo.bluetooth.library.connect.response.BleNotifyResponse;
import com.dingjikerbo.bluetooth.library.connect.response.BleReadResponse;
import com.dingjikerbo.bluetooth.library.connect.response.BleReadRssiResponse;
import com.dingjikerbo.bluetooth.library.connect.response.BleResponse;
import com.dingjikerbo.bluetooth.library.connect.response.BleWriteResponse;
import com.dingjikerbo.bluetooth.library.utils.BluetoothUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class BLEConnectManager {

    private static HashMap<String, BleConnectMaster> mBleConnectWorkerMap;

    static {
        mBleConnectWorkerMap = new HashMap<String, BleConnectMaster>();
    }

    private static BleConnectMaster getBleConnectMaster(String mac) {
        BleConnectMaster master = mBleConnectWorkerMap.get(mac);
        if (master == null) {
            master = BleConnectMaster.newInstance(mac);
            mBleConnectWorkerMap.put(mac, master);
        }
        return master;
    }

    public static void disconnectAllDevices() {
        Collection<BleConnectMaster> masters = mBleConnectWorkerMap.values();
        Iterator<BleConnectMaster> iterator = masters.iterator();
        while (iterator.hasNext()) {
            BleConnectMaster master = iterator.next();
            if (master != null) {
                master.disconnect();
            }
        }
    }

    public static void connect(String mac, BleConnectResponse response) {
        if (!checkBleAvailable(response)) {
            return;
        }

        if (!TextUtils.isEmpty(mac) && response != null) {
            BleConnectMaster master = getBleConnectMaster(mac);
            master.connect(response);
        } else {
            dispatchResponseResult(response, Code.ILLEGAL_ARGUMENT);
        }
    }

    public static void disconnect(String mac) {
        if (!checkBleAvailable()) {
            return;
        }

        if (!TextUtils.isEmpty(mac)) {
            BleConnectMaster master = getBleConnectMaster(mac);
            master.disconnect();
        }
    }

    public static void read(String mac, UUID service, UUID character, BleReadResponse response) {
        if (!checkBleAvailable()) {
            return;
        }

        if (!TextUtils.isEmpty(mac) && service != null && character != null && response != null) {
            BleConnectMaster master = getBleConnectMaster(mac);
            master.read(service, character, response);
        } else {
            dispatchResponseResult(response, Code.ILLEGAL_ARGUMENT);
        }
    }

    public static void write(String mac, UUID service, UUID character, byte[] bytes, BleWriteResponse response) {
        if (!checkBleAvailable()) {
            return;
        }

        if (!TextUtils.isEmpty(mac) && service != null && character != null && bytes != null && response != null) {
            BleConnectMaster master = getBleConnectMaster(mac);
            master.write(service, character, bytes, response);
        } else {
            dispatchResponseResult(response, Code.ILLEGAL_ARGUMENT);
        }
    }

    public static void notify(String mac, UUID service, UUID character, BleNotifyResponse response) {
        if (!checkBleAvailable()) {
            return;
        }

        if (!TextUtils.isEmpty(mac) && service != null && character != null && response != null) {
            BleConnectMaster master = getBleConnectMaster(mac);
            master.notify(service, character, response);
        } else {
            dispatchResponseResult(response, Code.ILLEGAL_ARGUMENT);
        }
    }

    public static void unnotify(String mac, UUID service, UUID character) {
        if (!checkBleAvailable()) {
            return;
        }

        if (!TextUtils.isEmpty(mac) && service != null && character != null) {
            BleConnectMaster master = getBleConnectMaster(mac);
            master.unnotify(service, character);
        }
    }

    public static void readRemoteRssi(String mac, BleReadRssiResponse response) {
        if (!checkBleAvailable()) {
            return;
        }

        if (!TextUtils.isEmpty(mac) && response != null) {
            BleConnectMaster master = getBleConnectMaster(mac);
            master.readRemoteRssi(response);
        } else {
            dispatchResponseResult(response, Code.ILLEGAL_ARGUMENT);
        }
    }

    private static boolean checkBleAvailable() {
        return checkBleAvailable(null);
    }

    private static boolean checkBleAvailable(BleResponse response) {
        boolean result = false;

        if (!BluetoothUtils.isBluetoothEnabled()) {
            dispatchResponseResult(response, Code.BLUETOOTH_DISABLED);
        } else if (!BluetoothUtils.isBleSupported()) {
            dispatchResponseResult(response, Code.BLE_NOT_SUPPORTED);
        } else {
            result = true;
        }

        return result;
    }

    private static void dispatchResponseResult(BleResponse response, int code) {
        if (response != null) {
            try {
                response.onResponse(code, null);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
