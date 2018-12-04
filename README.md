BluetoothKit---Android Bluetooth Framework
===========================

这个库用于Android蓝牙BLE设备通信，支持设备扫描，连接，读写，通知。


## 这套框架存在的意义

一、统一解决Android蓝牙通信过程中的兼容性问题

二、提供尽可能简单易用的接口，屏蔽蓝牙通信中的技术细节，只开放连接，读写，通知等语义。

三、实现串行化任务队列，统一处理蓝牙通信中的失败以及超时，支持可配置的容错处理

四、统一管理连接句柄，避免句柄泄露

五、方便监控各设备连接状态，在尽可能维持连接的情况下，将最不活跃的设备自动断开。

六、便于多进程APP架构下蓝牙连接的统一管理

七、支持拦截所有对蓝牙原生接口的调用


## 本框架源码讲解，可参考 **[Android BLE蓝牙通信教程](https://study.163.com/course/introduction/1006381079.htm)**

# **用法**

1、在Android Studio的build.gradle中，在dependencies里添加一行:

```groovy
compile 'com.inuker.bluetooth:library:1.4.0'
```

如果是Eclipse，可以导入bluetoothkit.jar，在AndroidManifest.xml中添加如下：
```
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

<uses-feature
    android:name="android.hardware.bluetooth_le"
    android:required="true" />

<application
    android:label="@string/app_name">

    <service
        android:name="com.inuker.bluetooth.library.BluetoothService" />
</application>
```

2、创建一个BluetoothClient，建议作为一个全局单例，管理所有BLE设备的连接。 

```Java
BluetoothClient mClient = new BluetoothClient(context);
```

所有接口都通过BluetoothClient调用，涉及的常量如回调的错误码都在Constants类中。

## **设备扫描** 

支持经典蓝牙和BLE设备混合扫描，可自定义扫描策略。每次扫描都要创建新的SearchRequest，不能复用。

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
        Beacon beacon = new Beacon(device.scanRecord);
        BluetoothLog.v(String.format("beacon for %s\n%s", device.getAddress(), beacon.toString()));
    }

    @Override
    public void onSearchStopped() {

    }

    @Override
    public void onSearchCanceled() {

    }
});
```

如果扫描不出来，可将targetSdk调到低于6.0.

可以随时停止扫描:

```Java
mClient.stopSearch();
```

## **蓝牙开关**

打开关闭蓝牙：

```
mClient.openBluetooth();
mClient.closeBluetooth();
```

判断蓝牙是否打开：

```
mClient.isBluetoothOpened();
```

蓝牙打开或关闭需要一段时间，可以注册回调监听状态，回调的参数如果是true表示蓝牙已打开，false表示蓝牙关闭

```
mClient.registerBluetoothStateListener(mBluetoothStateListener);

private final BluetoothStateListener mBluetoothStateListener = new BluetoothStateListener() {
    @Override
    public void onBluetoothStateChanged(boolean openOrClosed) {
        
    }

};

mClient.unregisterBluetoothStateListener(mBluetoothStateListener);
```

## **设备配对**

监听设备配对状态变化

```
private final BluetoothBondListener mBluetoothBondListener = new BluetoothBondListener() {
    @Override
    public void onBondStateChanged(String mac, int bondState) {
        // bondState = Constants.BOND_NONE, BOND_BONDING, BOND_BONDED
    }
};

mClient.registerBluetoothBondListener(mBluetoothBondListener);
mClient.unregisterBluetoothBondListener(mBluetoothBondListener);
```

## **Beacon解析**

可以在广播中携带设备的自定义数据，用于设备识别，数据广播，事件通知等，这样手机端无需连接设备就可以获取设备推送的数据。

扫描到的beacon数据为byte[]，在SearchResult的scanRecord中，按如下形式生成Beacon对象，

```
Beacon beacon = new Beacon(device.scanRecord);
```

Beacon数据结构如下:

```
public class Beacon {

    public byte[] mBytes;

    public List<BeaconItem> mItems;
}
```

BeaconItem是按type来区分的，

```
public class BeaconItem {
    /**
     * 广播中声明的长度
     */
    public int len;

    /**
     * 广播中声明的type
     */
    public int type;

    /**
     * 广播中的数据部分
     */
    public byte[] bytes;
}
```

然后根据自定义的协议，解析对应的BeaconItem中的bytes，首先创建一个BeaconParser，传入对应的BeaconItem，然后根据协议不断读取数据，
如果协议中某个字段占1个字节，则调用readByte，若占用两个字节则调用readShort，如果要取某个字节的某个bit则调用getBit。注意parser
每读一次数据，指针就会相应向后移动，可以调用setPosition设置当前指针的位置。

```
BeaconItem beaconItem; // 设置成beacon中对应的item
BeaconParser beaconParser = new BeaconParser(beaconItem);
int firstByte = beaconParser.readByte(); // 读取第1个字节
int secondByte = beaconParser.readByte(); // 读取第2个字节
int productId = beaconParser.readShort(); // 读取第3,4个字节
boolean bit1 = beaconParser.getBit(firstByte, 0); // 获取第1字节的第1bit
boolean bit2 = beaconParser.getBit(firstByte, 1); // 获取第1字节的第2bit
beaconParser.setPosition(0); // 将读取起点设置到第1字节处
```

## **BLE设备通信** 
### **● 连接**

连接过程包括了普通的连接(connectGatt)和发现服务(discoverServices)，这里收到回调时表明服务发现已完成。回调参数BleGattProfile包括了所有的service和characteristic的uuid。返回的code表示操作状态，包括成功，失败或超时等，所有常量都在Constants类中。

```Java
mClient.connect(MAC, new BleConnectResponse() {
    @Override
    public void onResponse(int code, BleGattProfile profile) {
        if (code == REQUEST_SUCCESS) {
        
        }
    }
});
```

可以配置连接参数如下，

```
BleConnectOptions options = new BleConnectOptions.Builder()
        .setConnectRetry(3)   // 连接如果失败重试3次
        .setConnectTimeout(30000)   // 连接超时30s
        .setServiceDiscoverRetry(3)  // 发现服务如果失败重试3次
        .setServiceDiscoverTimeout(20000)  // 发现服务超时20s
        .build();

mClient.connect(MAC, options, new BleConnectResponse() {
    @Override
    public void onResponse(int code, BleGattProfile data) {

    }
});
```

### **● 连接状态**

如果要监听蓝牙连接状态可以注册回调，只有两个状态：连接和断开。

```
mClient.registerConnectStatusListener(MAC, mBleConnectStatusListener);

private final BleConnectStatusListener mBleConnectStatusListener = new BleConnectStatusListener() {

    @Override
    public void onConnectStatusChanged(String mac, int status) {
        if (status == STATUS_CONNECTED) {

        } else if (status == STATUS_DISCONNECTED) {

        }
    }
};

mClient.unregisterConnectStatusListener(MAC, mBleConnectStatusListener);
```

也可以主动获取连接状态：

```
int status = mClient.getConnectStatus(MAC);
// Constants.STATUS_UNKNOWN
// Constants.STATUS_DEVICE_CONNECTED
// Constants.STATUS_DEVICE_CONNECTING
// Constants.STATUS_DEVICE_DISCONNECTING
// Constants.STATUS_DEVICE_DISCONNECTED
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

### **● 读Descriptor**

```Java
mClient.readDescriptor(MAC, serviceUUID, characterUUID, descriptorUUID, new BleReadResponse() {
    @Override
    public void onResponse(int code, byte[] data) {

    }
});
```

### **● 写Descriptor**

```Java
mClient.writeDescriptor(MAC, serviceUUID, characterUUID, descriptorUUID, bytes, new BleWriteResponse() {
    @Override
    public void onResponse(int code) {

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

### **● 打开Indicate**

和Notify类似，

```Java
mClient.indicate(MAC, serviceUUID, characterUUID, new BleNotifyResponse() {
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

### **● 关闭Indicate**

```Java
mClient.unindicate(MAC, serviceUUID, characterUUID, new BleUnnotifyResponse() {
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

### **● 清理请求队列**

如果发送给设备的请求设备来不及处理，则这些请求会保存到队列中，如果在某些场景下需要清除这些请求，可以调用

```
mClient.clearRequest(MAC, clearType);
// Constants.REQUEST_READ，所有读请求
// Constants.REQUEST_WRITE，所有写请求
// Constants.REQUEST_NOTIFY，所有通知相关的请求
// Constants.REQUEST_RSSI，所有读信号强度的请求
```

clearType表示要清除的请求类型，如果要清除多种请求，可以将多种类型取或，如果要清除所有请求，则传入0。

### **● 刷新缓存**

```
mClient.refreshCache(MAC);
```

---
有问题或建议可以给我邮件，到我的博客留言，或者加QQ群

 - Email: dingjikerbo@gmail.com

 - Blog: http://blog.csdn.net/dingjikerbo

 - QQ群: 112408886