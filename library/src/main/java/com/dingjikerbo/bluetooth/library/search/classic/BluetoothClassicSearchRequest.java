package com.dingjikerbo.bluetooth.library.search.classic;


import com.dingjikerbo.bluetooth.library.search.BluetoothSearchRequest;

public class BluetoothClassicSearchRequest extends BluetoothSearchRequest {

    private BluetoothClassicSearchRequest() {

    }

    public static BluetoothSearchRequest newInstance() {
        BluetoothSearchRequest request = new BluetoothSearchRequest.Builder()
                .searchBluetoothClassicDevice().build();
        return request;
    }

    public static BluetoothSearchRequest newInstance(int duration) {
        BluetoothSearchRequest request = new BluetoothSearchRequest.Builder()
                .searchBluetoothClassicDevice(duration).build();
        return request;
    }
}
