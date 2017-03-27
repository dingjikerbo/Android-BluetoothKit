package com.inuker.bluetooth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.inuker.bluetooth.library.utils.BluetoothLog;

import java.lang.ref.PhantomReference;

/**
 * Created by liwentian on 2017/3/9.
 */

public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
        BluetoothLog.v(String.format("%s onCreate", this.getClass().getSimpleName()));

        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestActivity.this, TestActivity1.class);
                startActivity(intent);
                BluetoothLog.v("finish");
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BluetoothLog.v(String.format("%s onResume", this.getClass().getSimpleName()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        BluetoothLog.v(String.format("%s onStart", this.getClass().getSimpleName()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        BluetoothLog.v(String.format("%s onPause", this.getClass().getSimpleName()));
    }

    @Override
    protected void onStop() {
        super.onStop();
        BluetoothLog.v(String.format("%s onStop", this.getClass().getSimpleName()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothLog.v(String.format("%s onDestroy", this.getClass().getSimpleName()));
    }
}
