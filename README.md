# BluetoothCommon

一、蓝牙扫描
------

支持Bluetooth LE和BluetoothClassic综合扫描，可灵活配置扫描策略。
如果启动扫描任务时，之前的扫描任务还未完成，则会自动取消之前的任务。

```Java
BluetoothSearchRequest request = new BluetoothSearchRequest.Builder()
        .searchBluetoothLeDevice(10000)  // 先扫描BLE 10s
        .searchBluetoothClassicDevice(5000) // 然后扫描classic 5s
        .multiSearchBluetoothLeDevice(1000, 30) // 然后扫描BLE 30次，每次1s
        .multiSearchBluetoothClassicDevice(1000, 20) // 最后扫描classic 20次，每次1s
        .build();

BluetoothSearchHelper.getInstance().startSearch(request, new BluetoothSearchResponse() {
    @Override
    public void onSearchStarted() {

    }

    @Override
    public void onDeviceFounded(XmBluetoothDevice device) {

    }

    @Override
    public void onSearchStopped() {

    }

    @Override
    public void onSearchCanceled() {

    }
});

// 取消扫描任务
BluetoothSearchHelper.getInstance().cancelSearch(request);
```

二、连接
----

```Java
BLEConnectManager.connect(mDeviceMac, new BleConnectResponse() {

    @Override
    public void onResponse(int code, Bundle data) {
        if (code == Code.REQUEST_SUCCESS) {

        } else {
            
        }
    }
});

BLEConnectManager.disconnect(mDeviceMac);
```

三、数据读写
------

```Java
BLEConnectManager.read(mDeviceMac, serviceUUID, characterUUID, new BleReadResponse() {

    @Override
    public void onResponse(int code, byte[] data) {
        if (code == Code.REQUEST_SUCCESS) {

        } else {

        }
    }
});

byte[] bytes = new byte[] { 0x1, 0x3 };
        
BLEConnectManager.write(mDeviceMac, serviceUUID, characterUUID, bytes, new BleWriteResponse() {

    @Override
    public void onResponse(int code, Void data) {
        if (code == Code.REQUEST_SUCCESS) {

        } else {

        }
    }
});
```

四、打开通知
----

```
BLEConnectManager.notify(mDeviceMac, serviceUUID, characterUUID, new BleNotifyResponse() {

    @Override
    public void onResponse(int code, Void data) {
        if (code == Code.REQUEST_SUCCESS) {

        } else {

        }
    }
});

BLEConnectManager.unnotify(mDeviceMac, serviceUUID, characterUUID);
```

五、读取RSSI
--------

```
BLEConnectManager.readRemoteRssi(mDeviceMac, new BleReadRssiResponse() {
    @Override
    public void onResponse(int code, Integer rssi) {
        if (code == Code.REQUEST_SUCCESS) {

        } else {

        }
    }
});
```

六、状态通知
--
状态通知包括蓝牙连接状态变化和notify通知，两者都是通过BroadcastReceiver实现的，如果要关注相关的状态，则需要注册Receiver，如下：

```Java
IntentFilter filter = new IntentFilter(XmBluetoothManager.ACTION_CHARACTER_CHANGED);
filter.addAction(XmBluetoothManager.ACTION_CONNECT_STATUS_CHANGED);
LocalBroadcastManager.getInstance(context).registerReceiver(mReceiver, filter);

private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent == null) {
			return;
		}

		String mac = intent
				.getStringExtra(XmBluetoothManager.KEY_DEVICE_ADDRESS);

		String action = intent.getAction();

		if (XmBluetoothManager.ACTION_CHARACTER_CHANGED
				.equalsIgnoreCase(action)) {
			UUID service = (UUID) intent
					.getSerializableExtra(XmBluetoothManager.KEY_SERVICE_UUID);
			UUID character = (UUID) intent
					.getSerializableExtra(XmBluetoothManager.KEY_CHARACTER_UUID);
			byte[] value = intent
					.getByteArrayExtra(XmBluetoothManager.KEY_CHARACTER_VALUE);

			if (service != null && character != null) {
				processNotify(service, character, value);
			}
		} else if (XmBluetoothManager.ACTION_CONNECT_STATUS_CHANGED
				.equalsIgnoreCase(action)) {
			int status = intent.getIntExtra(
					XmBluetoothManager.KEY_CONNECT_STATUS,
					XmBluetoothManager.STATUS_UNKNOWN);
			processConnectStatusChanged(status);
		}
	}
};
```

