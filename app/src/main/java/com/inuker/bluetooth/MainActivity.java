package com.inuker.bluetooth;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.response.BleResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;

public class MainActivity extends Activity {

    private static final String MAC = "B0:D5:9D:6F:E7:A5";

    private Button mBtnConnect;
    private Button mBtnDisconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        BluetoothClient.getInstance(this).connect(MAC, new BleResponse() {
            @Override
            public void onResponse(int code, Bundle data) throws RemoteException {
                BluetoothLog.v(String.format("MainActivity.onResponse: code = %d", code));
            }
        });
    }

    private void disconnect() {
        BluetoothClient.getInstance(this).disconnect(MAC);
    }
}
