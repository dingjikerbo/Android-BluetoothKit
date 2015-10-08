# BluetoothCommon
Android蓝牙通用框架，包括BLE及经典蓝牙扫描，广播解析，蓝牙连接等

一、蓝牙扫描

可设置一系列经典和BLE的扫描组合，配置扫描时长。
```Java
BluetoothSearchRequest request = new BluetoothSearchRequest.Builder()
						.searchBluetoothLeDevice(5000)
						.searchBluetoothClassicDevice(5000)
						.searchBluetoothLeDevice(5000).build();

BluetoothSearchManager.getInstance().startSearch(request,
		new BluetoothSearchResponse() {

			@Override
			public void onSearchStarted() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDeviceFounded(XmBluetoothDevice device) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSearchStopped() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSearchCanceled() {
				// TODO Auto-generated method stub

			}

		});
```
