package com.dingjikerbo.bluetooth;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;

public class MainActivity extends Activity {

    private BluetoothClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mClient = new BluetoothClient(this);

        mClient.connect("12", new BleConnectResponse() {
            @Override
            public void onResponse(int code, Bundle data) {
                Log.i("bush", String.format("onResponse code = %d", code));
            }
        });
    }
}
