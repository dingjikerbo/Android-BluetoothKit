BluetoothKit---Android Bluetooth Framework
===========================

This library allows for easy access to Bluetooth device scan and connection, support customizing scan policy and solved many android bluetooth inherent compatibility and stability problems refer to [Android 4.3 Bluetooth Low Energy unstable](http://stackoverflow.com/questions/17870189/android-4-3-bluetooth-low-energy-unstable)

# **Requirements**

 - minSdkVersion should be not less than 18

 - Permission in AndroidManifest.xml
```
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>

<uses-feature
    android:name="android.hardware.bluetooth_le"
    android:required="true" />
```

# **Usage**

1、If you are building with Gradle, simply add the following line to the `dependencies` section of your `build.gradle` file:

```groovy
compile 'com.inuker.bluetooth:library:1.1.4'
```

2、Create a BluetoothClient as below: 

```Java
BluetoothClient mClient = new BluetoothClient(context);
```

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

## **Bluetooth LE Connection** 

### **● Connect**

BleGattProfile contains all service and characteristic uuid.

```Java
mClient.connect(MAC, new BleConnectResponse() {
    @Override
    public void onResponse(int code, BleGattProfile profile) {
        if (code == REQUEST_SUCCESS) {
        
        }
    }
});
```

### **● Connect Status**

```
mClient.registerConnectStatusListener(MAC, mBleConnectStatusListener);

private final BleConnectStatusListener mBleConnectStatusListener = new BleConnectStatusListener() {

    @Override
    public void onConnectStatusChanged(int status) {
        if (status == STATUS_CONNECTED) {

        } else if (status == STATUS_DISCONNECTED) {

        }
    }
};

mClient.unregisterConnectStatusListener(MAC, mBleConnectStatusListener);
```

### **● Disconnect**
```Java
mClient.disconnect(MAC);
```

### **● Read Characteristic**
```Java
mClient.read(MAC, serviceUUID, characterUUID, new BleReadResponse() {
    @Override
    public void onResponse(int code, byte[] data) {
        if (code == REQUEST_SUCCESS) {

        }
    }
});
```

### **● Write Characteristic**

The data to write should be no more than 20 bytes.

```Java
mClient.write(MAC, serviceUUID, characterUUID, bytes, new BleWriteResponse() {
    @Override
    public void onResponse(int code) {
        if (code == REQUEST_SUCCESS) {

        }
    }
});

// with WRITE_TYPE_NO_RESPONSE
mClient.writeNoRsp(MAC, serviceUUID, characterUUID, bytes, new BleWriteResponse() {
    @Override
    public void onResponse(int code) {
        if (code == REQUEST_SUCCESS) {

        }
    }
});
```

### **● Open Notify**

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

### **● Close Notify**
```Java
mClient.unnotify(MAC, serviceUUID, characterUUID, new BleUnnotifyResponse() {
    @Override
    public void onResponse(int code) {
        if (code == REQUEST_SUCCESS) {

        }
    }
});
```

### **● Read Rssi**
```Java
mClient.readRssi(MAC, new BleReadRssiResponse() {
    @Override
    public void onResponse(int code, Integer rssi) {
        if (code == REQUEST_SUCCESS) {

        }
    }
});
```

### **● Refresh Cache**

Refresh cache at the beginning of next connection.

```Java
mClient.refreshCache(MAC);
```
<br/>
# **Author**
 - Email: dingjikerbo@gmail.com
 - Blog: http://blog.csdn.net/dingjikerbo
 - Welcome to contact me with any suggestions or ideas.
