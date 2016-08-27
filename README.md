BluetoothKit---Android Bluetooth Framework
===========================

This library allows for easy access to a Bluetooth LE device's connection, solved many android bluetooth inherent compatibility and stability problems refer to [Android 4.3 Bluetooth Low Energy unstable](http://stackoverflow.com/questions/17870189/android-4-3-bluetooth-low-energy-unstable)

Usage
-----------------------

First, you should initial BluetoothClient as below:

```Java
BluetoothClient mClient = BluetoothClient.getInstance(context);
```

## **1. connect**

```Java
mClient.connect(MAC, new BluetoothResponse() {
    @Override
    public void onResponse(int code, Bundle data) {
        if (code == REQUEST_SUCCESS) {

        }
    }
});
```

## **2. disconnect**
```Java
mClient.disconnect(MAC);
```

## **3. read**
```Java
mClient.read(MAC, serviceUUID, characterUUID, new BluetoothResponse() {
    @Override
    public void onResponse(int code, Bundle data) {
        if (code == REQUEST_SUCCESS) {
            byte[] value = data.getByteArray(EXTRA_BYTE_VALUE);
        }
    }
});
```

## **4. write**
```Java
mClient.write(MAC, serviceUUID, characterUUID, bytes, new BluetoothResponse() {
    @Override
    public void onResponse(int code, Bundle data)  {
        if (code == REQUEST_SUCCESS) {

        }
    }
});
```

## **5. notify**
```Java
mClient.notify(MAC, serviceUUID, characterUUID, new BluetoothResponse() {
    @Override
    public void onResponse(int code, Bundle data) {
        if (code == REQUEST_SUCCESS) {

        }
    }
});
```

## **6. unnotify**
```Java
mClient.unnotify(MAC, serviceUUID, characterUUID, new BluetoothResponse() {
    @Override
    public void onResponse(int code, Bundle data) {
        if (code == REQUEST_SUCCESS) {

        }
    }
});
```

## **7. read rssi**
```Java
mClient.readRssi(MAC, new BluetoothResponse() {
    @Override
    public void onResponse(int code, Bundle data) {
        if (code == REQUEST_SUCCESS) {
            int rssi = data.getInt(EXTRA_RSSI);
        }
    }
});
```
