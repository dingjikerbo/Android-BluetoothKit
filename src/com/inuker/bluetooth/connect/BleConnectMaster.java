package com.inuker.bluetooth.connect;

import java.util.UUID;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.inuker.bluetooth.connect.request.IBleRunner;
import com.inuker.bluetooth.connect.response.BleConnectResponse;
import com.inuker.bluetooth.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.connect.response.BleReadResponse;
import com.inuker.bluetooth.connect.response.BleResponse;
import com.inuker.bluetooth.connect.response.BleWriteResponse;

public class BleConnectMaster implements IBleRunner {

	private static final int MSG_CONNECT = 0x10;
	private static final int MSG_READ = 0x20;
	private static final int MSG_WRITE = 0x30;
	private static final int MSG_DISCONNECT = 0x40;
	private static final int MSG_NOTIFY = 0x80;
	private static final int MSG_UNNOTIFY = 0x90;

	private HandlerThread mThread;
	private Handler mHandler;

	private BleConnectDispatcher mBleConnectDispatcher;

	public static BleConnectMaster newInstance(String mac) {
		return new BleConnectMaster(mac);
	}

	private BleConnectMaster(String mac) {
		mBleConnectDispatcher = BleConnectDispatcher.newInstance(mac, this);
	}

	private void startMasterLooper() {
		if (mThread == null) {
			mThread = new HandlerThread(String.format("BleConnectMaster"));
			mThread.start();

			mHandler = new Handler(mThread.getLooper()) {

				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					processDispatcherMessage(msg);
				}
			};
		}
	}

	@SuppressWarnings("rawtypes")
	private void processDispatcherMessage(Message msg) {
		BleResponse response = (BleResponse) msg.obj;
		Bundle data = msg.getData();
		
		switch (msg.what) {
		case MSG_CONNECT:
			mBleConnectDispatcher.connect(response);
			break;
			
		case MSG_DISCONNECT:
			mBleConnectDispatcher.disconnect();
			break;

		case MSG_READ:
			if (data != null) {
				UUID service = (UUID) data.getSerializable("service");
				UUID character = (UUID) data.getSerializable("character");
				mBleConnectDispatcher.read(service, character, response);
			}
			
			break;

		case MSG_WRITE:
			if (data != null) {
				UUID service = (UUID) data.getSerializable("service");
				UUID character = (UUID) data.getSerializable("character");
				
				byte[] bytes = data.getByteArray("bytes");
				
				if (bytes != null) {
					mBleConnectDispatcher.write(service, character, bytes, response);
				} else {
					int value = data.getInt("value");
					mBleConnectDispatcher.write(service, character, value, response);
				}
			}
			
			break;
			
		case MSG_NOTIFY:
            if (data != null) {
                UUID service = (UUID) data.getSerializable("service");
                UUID character = (UUID) data.getSerializable("character");
                mBleConnectDispatcher.notify(service, character, response);
            }
            break;
            
		case MSG_UNNOTIFY:
			if (data != null) {
				UUID service = (UUID) data.getSerializable("service");
                UUID character = (UUID) data.getSerializable("character");
                mBleConnectDispatcher.unnotify(service, character);
			}
			break;
		}
	}

	public void connect(BleConnectResponse callback) {
		sendMessageToDispatcher(MSG_CONNECT, callback);
	}
	
	public void disconnect() {
		sendMessageToDispatcher(MSG_DISCONNECT);
	}

	public void read(UUID serviceId, UUID characterId, BleReadResponse callback) {
		Bundle data = new Bundle();
		data.putSerializable("service", serviceId);
		data.putSerializable("character", characterId);
		sendMessageToDispatcher(MSG_READ, callback, data);
	}
	
	public void write(UUID serviceId, UUID characterId, int value, BleWriteResponse callback) {
		Bundle data = new Bundle();
		data.putSerializable("service", serviceId);
		data.putSerializable("character", characterId);
		data.putLong("value", value);
		sendMessageToDispatcher(MSG_WRITE, callback, data);
	}
	
	public void write(UUID serviceId, UUID characterId, byte[] bytes, BleWriteResponse callback) {
		Bundle data = new Bundle();
		data.putSerializable("service", serviceId);
		data.putSerializable("character", characterId);
		data.putByteArray("bytes", bytes);
		sendMessageToDispatcher(MSG_WRITE, callback, data);
	}
	
	public void notify(UUID serviceId, UUID characterId, BleNotifyResponse response) {
        Bundle data = new Bundle();
        data.putSerializable("service", serviceId);
        data.putSerializable("character", characterId);
        sendMessageToDispatcher(MSG_NOTIFY, response, data);
    }
	
	public void unnotify(UUID serviceId, UUID characterId) {
        Bundle data = new Bundle();
        data.putSerializable("service", serviceId);
        data.putSerializable("character", characterId);
        sendMessageToDispatcher(MSG_UNNOTIFY, null, data);
    }
	
	private void sendMessageToDispatcher(int what) {
		sendMessageToDispatcher(what, null);
	}

	private void sendMessageToDispatcher(int what, Object obj) {
		sendMessageToDispatcher(what, obj, null);
	}

	private void sendMessageToDispatcher(int what, Object obj, Bundle data) {
		if (mHandler == null) {
			startMasterLooper();
		}
		if (mHandler != null) {
			Message msg = mHandler.obtainMessage(what, obj);

			if (data != null) {
				msg.setData(data);
			}

			msg.sendToTarget();
		}
	}

	@Override
	public Looper getLooper() {
		if (mHandler == null) {
			startMasterLooper();
		}
		return mHandler.getLooper();
	}
}
