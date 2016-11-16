package com.inuker.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.utils.BluetoothLog;

import java.lang.reflect.Method;

/**
 * Created by dingjikerbo on 2016/9/7.
 */
public class TestActivity extends Activity implements View.OnClickListener {

    private static final String MAC1 = "80:EA:CA:00:00:72";
    private static final String MAC2 = "CF:3B:1E:11:8E:21";

    private Button mBtn1;
    private Button mBtn2;
    private Button mBtn3;

    private BluetoothManager mBluetoothManager;
    private  BluetoothAdapter  mBluetoothLeAdapter;
    private BluetoothDevice mDevice;

    private BluetoothGatt mGatt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);

        mBluetoothManager = (android.bluetooth.BluetoothManager)
                getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothLeAdapter = mBluetoothManager.getAdapter();
        mDevice = mBluetoothLeAdapter.getRemoteDevice(MAC1);

        mBtn1 = (Button) findViewById(R.id.btn1);
        mBtn2 = (Button) findViewById(R.id.btn2);
        mBtn3 = (Button) findViewById(R.id.btn3);

        mBtn1.setOnClickListener(this);
        mBtn2.setOnClickListener(this);
        mBtn3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                BluetoothLog.v(String.format("connectGatt"));
                mGatt = mDevice.connectGatt(this, false, new BluetoothGattCallback() {
                    @Override
                    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                        super.onConnectionStateChange(gatt, status, newState);

                        BluetoothLog.v(String.format("onConnectionStateChange: status = %d, newState = %d", status, newState));

//                        if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
//                            BluetoothLog.v(String.format("discoverServices"));
//                            gatt.discoverServices();
//                        }
                    }

                    @Override
                    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                        super.onServicesDiscovered(gatt, status);
                        BluetoothLog.v(String.format("onServicesDiscovered, status = %d", status));
                    }
                });
//                refreshDeviceCache(mGatt);
                break;

            case R.id.btn2:
                BluetoothLog.v(String.format("disconnect"));
                if (mGatt != null) {
                    mGatt.disconnect();
                }
                break;

            case R.id.btn3:
//                BluetoothLog.v(String.format("close"));
//                if (mGatt != null) {
//                    mGatt.close();
//                    mGatt = null;
//                }

                break;
        }
    }

    public boolean refreshDeviceCache(BluetoothGatt gatt){
        boolean result = false;
        try {
            if (gatt != null) {
                Method refresh = BluetoothGatt.class.getMethod("refresh");
                if (refresh != null) {
                    refresh.setAccessible(true);
                    result = (boolean) refresh.invoke(gatt, new Object[0]);
                }
            }
        } catch (Exception e) {
            BluetoothLog.e(e);
        }

        BluetoothLog.v(String.format("refreshDeviceCache return %b", result));

        return result;
    }
}
