package com.inuker.bluetooth;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;

/**
 * Created by dingjikerbo on 2016/9/7.
 */
public class TestActivity extends Activity {

    private static final String MAC1 = "D8:AC:B5:11:5E:D7";
    private static final String MAC2 = "CF:3B:1E:11:8E:21";

    private Button mBtn1;
    private Button mBtn2;
    private Button mBtn3;
    private Button mBtn4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);

        mBtn1 = (Button) findViewById(R.id.btn1);
        mBtn2 = (Button) findViewById(R.id.btn2);
        mBtn3 = (Button) findViewById(R.id.btn3);
        mBtn4 = (Button) findViewById(R.id.btn4);

        mBtn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                connect1();
            }
        });

        mBtn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                connect2();
            }
        });

        mBtn3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ClientManager.getClient().disconnect(MAC1);
            }
        });

        mBtn4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ClientManager.getClient().disconnect(MAC2);
            }
        });
    }

    private void connect1() {
        ClientManager.getClient().connect(MAC1, new BleConnectResponse() {
            @Override
            public void onResponse(int i, BleGattProfile profile) {

            }
        });
    }

    private void connect2() {
        ClientManager.getClient().connect(MAC2, new BleConnectResponse() {
            @Override
            public void onResponse(int i, BleGattProfile profile) {

            }
        });
    }
}
