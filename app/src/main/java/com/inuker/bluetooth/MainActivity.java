package com.inuker.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCallback;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.IBluetoothClient;
import com.inuker.bluetooth.library.connect.response.BluetoothResponse;
import com.inuker.bluetooth.library.hook.BluetoothHooker;
import com.inuker.bluetooth.library.utils.BluetoothLog;

import java.util.UUID;

public class MainActivity extends Activity {

    private static final String MAC = "B0:D5:9D:6F:E7:A5";

    private Button mBtnConnect;
    private Button mBtnDisconnect;

    private IBluetoothClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        mClient.connect(MAC, new BluetoothResponse() {
            @Override
            public void onResponse(int code, Bundle data) throws RemoteException {
                BluetoothLog.v(String.format("MainActivity connect.onResponse: code = %d", code));
            }
        });
    }

    private void disconnect() {
//        mClient.disconnect(MAC);
        mClient.read(MAC, UUID.fromString("123"), UUID.fromString("456"), new BluetoothResponse() {
            @Override
            public void onResponse(int code, Bundle data) throws RemoteException {
                BluetoothLog.v(String.format("MainActivity read.onResponse: code = %d", code));
            }
        });
    }
}
