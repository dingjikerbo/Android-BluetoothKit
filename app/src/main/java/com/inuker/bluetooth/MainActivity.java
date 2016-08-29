package com.inuker.bluetooth;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.IBluetoothClient;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleReadRssiResponse;
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.connect.response.BluetoothResponse;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResponse;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.security.BleRegisterConnector;

import java.util.UUID;

public class MainActivity extends Activity {

    private static final String MAC = "B0:D5:9D:6F:E7:A5";

    private Button mBtnConnect;
    private Button mBtnDisconnect;

    private IBluetoothClient mClient;

    private BleRegisterConnector mConnector;

    private UUID serviceUUID, characterUUID;

    private byte[] bytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mConnector = new BleRegisterConnector(MAC, 149);

        mClient = BluetoothClient.getInstance(this);

        mBtnConnect = (Button) findViewById(R.id.connect);
        mBtnConnect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                connect();
            }
        });

        mBtnDisconnect = (Button) findViewById(R.id.disconnect);
        mBtnDisconnect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                disconnect();
            }
        });

    }

    private void connect() {
//        mConnector.connect(new BluetoothResponse() {
//            @Override
//            public void onResponse(int code, Bundle data) throws RemoteException {
//                BluetoothLog.v(String.format("MainActivity.onResponse code = %d", code));
//            }
//        });

        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(2000, 1)
                .build();

        mClient.connect(MAC, new BleConnectResponse() {
            @Override
            public void onResponse(int code, Bundle data) {
                if (code == REQUEST_SUCCESS) {

                }
            }
        });

        mClient.read(MAC, serviceUUID, characterUUID, new BleReadResponse() {
            @Override
            public void onResponse(int code, byte[] data) {
                if (code == REQUEST_SUCCESS) {

                }
            }
        });

        mClient.write(MAC, serviceUUID, characterUUID, bytes, new BleWriteResponse() {
            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {

                }
            }
        });

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

        mClient.unnotify(MAC, serviceUUID, characterUUID, new BleUnnotifyResponse() {
            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {

                }
            }
        });

        mClient.readRssi(MAC, new BleReadRssiResponse() {
            @Override
            public void onResponse(int code, Integer rssi) {
                if (code == REQUEST_SUCCESS) {

                }
            }
        });

        mClient.search(request, new SearchResponse() {
            @Override
            public void onSearchStarted() {
                BluetoothLog.v(String.format("MainActivity.onSearchStarted in %s", Thread.currentThread().getName()));
            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                BluetoothLog.v(String.format("MainActivity.onDeviceFound %s", device.device.getAddress()));
            }

            @Override
            public void onSearchStopped() {
                BluetoothLog.v(String.format("MainActivity.onSearchStopped"));
            }

            @Override
            public void onSearchCanceled() {
                BluetoothLog.v(String.format("MainActivity.onSearchCanceled"));
            }
        });
    }

    private void disconnect() {
//        mClient.disconnect(MAC);

        mClient.stopSearch();
    }
}
