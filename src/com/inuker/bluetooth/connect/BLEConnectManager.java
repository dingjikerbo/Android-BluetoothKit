package com.inuker.bluetooth.connect;

import java.util.HashMap;
import java.util.UUID;

import android.text.TextUtils;

import com.inuker.bluetooth.connect.request.Code;
import com.inuker.bluetooth.connect.response.BleConnectResponse;
import com.inuker.bluetooth.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.connect.response.BleReadResponse;
import com.inuker.bluetooth.connect.response.BleWriteResponse;

public class BLEConnectManager {
	
	private static HashMap<String, BleConnectMaster> mBleConnectWorkerMap;
	
	private static BleConnectMaster getBleConnectMaster(String mac) {
		if (mBleConnectWorkerMap == null) {
			mBleConnectWorkerMap = new HashMap<String, BleConnectMaster>();
		}
		
		BleConnectMaster master = mBleConnectWorkerMap.get(mac);
		if (master == null) {
			master = BleConnectMaster.newInstance(mac);
			mBleConnectWorkerMap.put(mac, master);
		}
		return master;
	}
	
	public static void connect(String mac, BleConnectResponse response) {
        if (!TextUtils.isEmpty(mac)) {
            BleConnectMaster master = getBleConnectMaster(mac);
            master.connect(response);
        } else if (response != null) {
            response.onResponse(Code.ILLEGAL_ARGUMENT, null);
        }
	}
	
	public static void disconnect(String mac) {
        if (!TextUtils.isEmpty(mac)) {
            BleConnectMaster master = getBleConnectMaster(mac);
            master.disconnect();
        }
	}
	
	public static void read(String mac, UUID service, UUID character, BleReadResponse response) {
        if (!TextUtils.isEmpty(mac) && service != null && character != null) {
            BleConnectMaster master = getBleConnectMaster(mac);
            master.read(service, character, response);
        } else if (response != null) {
            response.onResponse(Code.ILLEGAL_ARGUMENT, null);
        }
	}

	public static void write(String mac, UUID service, UUID character, int value, BleWriteResponse response) {
        if (!TextUtils.isEmpty(mac) && service != null && character != null) {
            BleConnectMaster master = getBleConnectMaster(mac);
            master.write(service, character, value, response);
        } else if (response != null) {
            response.onResponse(Code.ILLEGAL_ARGUMENT, null);
        }
	}

	public static void write(String mac, UUID service, UUID character, byte[] bytes, BleWriteResponse response) {
        if (!TextUtils.isEmpty(mac) && service != null && character != null && bytes != null) {
            BleConnectMaster master = getBleConnectMaster(mac);
            master.write(service, character, bytes, response);
        } else if (response != null) {
            response.onResponse(Code.ILLEGAL_ARGUMENT, null);
        }
	}

	public static void notify(String mac, UUID service, UUID character, BleNotifyResponse response) {
        if (!TextUtils.isEmpty(mac) && service != null && character != null) {
            BleConnectMaster master = getBleConnectMaster(mac);
            master.notify(service, character, response);
        } else if (response != null) {
            response.onResponse(Code.ILLEGAL_ARGUMENT, null);
        }
	}
	
	public static void unnotify(String mac, UUID service, UUID character) {
        if (!TextUtils.isEmpty(mac) && service != null && character != null) {
            BleConnectMaster master = getBleConnectMaster(mac);
            master.unnotify(service, character);
        }
	}
}
