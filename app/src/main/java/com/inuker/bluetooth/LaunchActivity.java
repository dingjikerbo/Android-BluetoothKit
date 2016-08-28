package com.inuker.bluetooth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.IBluetoothClient;
import com.inuker.bluetooth.library.connect.response.BluetoothResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.security.BleRegisterConnector;

public class LaunchActivity extends Activity {

    private static final String MAC = "B0:D5:9D:6F:E7:A5";

    private Button mBtnConnect;
    private Button mBtnDisconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

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
        startActivity(new Intent(this, MainActivity.class));
    }

    private void disconnect() {
    }
}
