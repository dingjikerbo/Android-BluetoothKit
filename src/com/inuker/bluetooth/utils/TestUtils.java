package com.inuker.bluetooth.utils;

import java.util.UUID;

import android.text.TextUtils;

import com.inuker.bluetooth.connect.BleConnectDispatcher;
import com.inuker.bluetooth.connect.BleConnectWorker;

public class TestUtils {

	public static String getStatus(int status) {
		switch (status) {
		case BleConnectWorker.STATUS_DEVICE_DISCONNECTED:
			return "STATUS_DEVICE_DISCONNECTED";

		case BleConnectWorker.STATUS_DEVICE_CONNECTED:
			return "STATUS_DEVICE_CONNECTED";

		case BleConnectWorker.STATUS_DEVICE_CONNECTING:
			return "STATUS_DEVICE_CONNECTING";

		case BleConnectWorker.STATUS_DEVICE_DISCONNECTING:
			return "STATUS_DEVICE_DISCONNECTING";

		case BleConnectWorker.STATUS_DEVICE_SERVICE_READY:
			return "STATUS_DEVICE_SERVICE_READY";

		default:
			return "";
		}
	}

	public static String getMsgName(int msg) {
		switch (msg) {
		case BleConnectWorker.MSG_CONNECTED:
			return "MSG_CONNECTED";

		case BleConnectWorker.MSG_GATT_FAILED:
			return "MSG_GATT_FAILED";

		case BleConnectWorker.MSG_SCHEDULE_NEXT:
			return "MSG_SCHEDULE_NEXT";

		case BleConnectWorker.MSG_REQUEST_TIMEOUT:
			return "MSG_REQUEST_TIMEOUT";
			
		case BleConnectDispatcher.MSG_REQUEST_FAILED:
			return "MSG_REQUEST_FAILED";

		case BleConnectDispatcher.MSG_REQUEST_SUCCESS:
			return "MSG_REQUEST_SUCCESS";
			
		case BleConnectDispatcher.MSG_CHARACTER_CHANGED:
			return "MSG_CHARACTER_CHANGED";
			
		default:
			return "";
		}
	}
	
	public static String getUUID(UUID uuid) {
		String text = "";

		if (uuid != null) {
			text = uuid.toString();
			if (!TextUtils.isEmpty(text)) {
				String[] texts = text.split("-");

				try {
					long value = Long.parseLong(texts[0], 16);
					text = String.format("0x%x", value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return text;
	}
}
