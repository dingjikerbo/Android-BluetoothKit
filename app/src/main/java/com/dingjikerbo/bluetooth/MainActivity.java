package com.dingjikerbo.bluetooth;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.inuker.library.connect.BLEConnectManager;
import com.inuker.library.connect.Code;
import com.inuker.library.connect.response.BleConnectResponse;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BLEConnectManager.connect("11:22:33:44:55:66", new BleConnectResponse() {

            @Override
            public void onResponse(int code, Bundle data) {
                test(code);
            }
        });
    }

    private void test(int code) {
        if (code == Code.REQUEST_SUCCESS) {
            Toast.makeText(MainActivity.this, "success", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_LONG).show();
        }
    }
}
