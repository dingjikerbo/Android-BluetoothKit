BluetoothKit---Android Bluetooth Framework
===========================

This library allows for easy access to Bluetooth device scan and connection, support customizing scan policy and solved many android bluetooth inherent compatibility and stability problems refer to [Android 4.3 Bluetooth Low Energy unstable](http://stackoverflow.com/questions/17870189/android-4-3-bluetooth-low-energy-unstable)

# **Usage**

1、Import [bluetooth.jar](https://github.com/dingjikerbo/BluetoothKit/blob/master/bluetooth.jar) to your project

2、Add following permission to AndroidManifest.xml 

```
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>

<uses-feature
    android:name="android.hardware.bluetooth_le"
    android:required="true" />
```

3、Declare BluetoothService in AndroidManifest, you can set the process in which service runs in.

```
<service android:name="com.inuker.bluetooth.library.BluetoothService"
    android:process=":bluetoothkit"/>
```

4、Initial BluetoothClient as below:

```Java
BluetoothClient mClient = BluetoothClient.getInstance(context);
```

Note, android:minSdkVersion should be not less than 18

<br/>
## **Scan Device** 

This library support both Bluetooth LE device scan and Classic device scan, you could customize the scan policy as below:

```Java
SearchRequest request = new SearchRequest.Builder()
        .searchBluetoothLeDevice(3000, 3)   // scan Bluetooth LE device for 3000ms, 3 times
        .searchBluetoothClassicDevice(5000) // then scan Bluetooth Classic device for 5000ms, 1 time
        .searchBluetoothLeDevice(2000)      // at last scan Bluetooth LE device for 2000ms
        .build();

mClient.search(request, new SearchResponse() {
    @Override
    public void onSearchStarted() {

    }

    @Override
    public void onDeviceFounded(SearchResult device) {

    }

    @Override
    public void onSearchStopped() {

    }

    @Override
    public void onSearchCanceled() {

    }
});
```

You could stop the whole scan by just one line:

```Java
mClient.stopSearch();
```
<br/>
## **Bluetooth LE Connection** 

### **1. Connect**

```Java
mClient.connect(MAC, new BleConnectResponse() {
    @Override
    public void onResponse(int code, Bundle data) {
        if (code == REQUEST_SUCCESS) {
            
        }
    }
});
```

### **2. Disconnect**
```Java
mClient.disconnect(MAC);
```

### **3. Read Characteristic**
```Java
mClient.read(MAC, serviceUUID, characterUUID, new BleReadResponse() {
    @Override
    public void onResponse(int code, byte[] data) {
        if (code == REQUEST_SUCCESS) {

        }
    }
});
```

### **4. Write Characteristic**
```Java
mClient.write(MAC, serviceUUID, characterUUID, bytes, new BleWriteResponse() {
    @Override
    public void onResponse(int code) {
        if (code == REQUEST_SUCCESS) {

        }
    }
});
```

### **5. Open Notify**

```Java
mClient.notify(MAC, serviceUUID, characterUUID, new BleNotifyResponse() {
    @Override
    public void onNotify(UUID service, UUID character, byte[] value) {
        
    }

    @Override
    public void onResponse(int code) {
        if (code == REQUEST_SUCCESS) {

        }
    }
});
```

### **6. Close Notify**
```Java
mClient.unnotify(MAC, serviceUUID, characterUUID, new BleUnnotifyResponse() {
    @Override
    public void onResponse(int code) {
        if (code == REQUEST_SUCCESS) {

        }
    }
});
```

### **7. Read Rssi**
```Java
mClient.readRssi(MAC, new BleReadRssiResponse() {
    @Override
    public void onResponse(int code, Integer rssi) {
        if (code == REQUEST_SUCCESS) {

        }
    }
});
```
