BluetoothKit---Android Bluetooth Framework
===========================

这个库用于Android蓝牙BLE设备通信，支持常用的连接，读写，通知。在系统API基础上封装了一层异步任务队列，使所有任务串行化，并支持对每个任务配置超时和出错重试，同时解决了BLE蓝牙通信中可能会遇到的一系列坑，使得Android蓝牙开发非常方便。

# **用法**

1、在Android Studio的build.gradle中，在dependencies里添加一行:

```groovy
compile 'com.inuker.bluetooth:library:1.1.2'
```

2、创建一个BluetoothClient，建议作为一个单例: 

```Java
BluetoothClient mClient = new BluetoothClient(context);
```

## **设备扫描** 

支持经典蓝牙和BLE设备混合扫描，可自定义扫描策略:

```Java
SearchRequest request = new SearchRequest.Builder()
        .searchBluetoothLeDevice(3000, 3)   // 先扫BLE设备3次，每次3s
        .searchBluetoothClassicDevice(5000) // 再扫经典蓝牙5s
        .searchBluetoothLeDevice(2000)      // 再扫BLE设备2s
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

可以随时停止扫描:

```Java
mClient.stopSearch();
```

## **BLE设备通信** 

### **● 连接**

连接过程包括了普通的连接(connectGatt)和发现服务(discoverServices)，这里收到回调时表明服务发现已完成。回调参数BleGattProfile包括了所有的service和characteristic的uuid。

```Java
mClient.connect(MAC, new BleConnectResponse() {
    @Override
    public void onResponse(int code, BleGattProfile profile) {
        if (code == REQUEST_SUCCESS) {
        
        }
    }
});
```

### **● 连接状态**

如果要监听蓝牙连接状态可以注册回调，只有两个状态：连接和断开。

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

### **● 断开连接**
```Java
mClient.disconnect(MAC);
```

### **● 读Characteristic**
```Java
mClient.read(MAC, serviceUUID, characterUUID, new BleReadResponse() {
    @Override
    public void onResponse(int code, byte[] data) {
        if (code == REQUEST_SUCCESS) {

        }
    }
});
```

### **● 写Characteristic**

要注意这里写的byte[]不能超过20字节，如果超过了需要自己分成几次写。建议的办法是第一个byte放剩余要写的字节的长度。

```Java
mClient.write(MAC, serviceUUID, characterUUID, bytes, new BleWriteResponse() {
    @Override
    public void onResponse(int code) {
        if (code == REQUEST_SUCCESS) {

        }
    }
});
```

这个写是带了WRITE_TYPE_NO_RESPONSE标志的，实践中发现比普通的write快2~3倍，建议用于固件升级。

```Java
mClient.writeNoRsp(MAC, serviceUUID, characterUUID, bytes, new BleWriteResponse() {
    @Override
    public void onResponse(int code) {
        if (code == REQUEST_SUCCESS) {

        }
    }
});
```

### **● 打开Notify**

这里有两个回调，onNotify是接收通知的。

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

### **● 关闭Notify**
```Java
mClient.unnotify(MAC, serviceUUID, characterUUID, new BleUnnotifyResponse() {
    @Override
    public void onResponse(int code) {
        if (code == REQUEST_SUCCESS) {

        }
    }
});
```

### **● 读Rssi**
```Java
mClient.readRssi(MAC, new BleReadRssiResponse() {
    @Override
    public void onResponse(int code, Integer rssi) {
        if (code == REQUEST_SUCCESS) {

        }
    }
});
```

<br/>
# **作者**
 - Email: dingjikerbo@gmail.com
 - Blog: http://blog.csdn.net/dingjikerbo
 - QQ: 715876307
