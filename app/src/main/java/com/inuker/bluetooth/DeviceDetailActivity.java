package com.inuker.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.BluetoothUtils;

/**
 * Created by liwentian on 2016/9/2.
 */
public class DeviceDetailActivity extends Activity {

    private TextView mTvTitle;

    private ListView mListView;
    private DeviceDetailAdapter mAdapter;

    private BluetoothDevice mDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_detail_activity);

        Intent intent = getIntent();
        String mac = intent.getStringExtra("mac");

        mDevice = BluetoothUtils.getRemoteDevice(mac);

        mTvTitle = (TextView) findViewById(R.id.title);

        mTvTitle.setText(mDevice.getAddress());

        connectDevice();
    }

    private void connectDevice() {
        Toast.makeText(this, "connecting ...", Toast.LENGTH_LONG).show();

        ClientManager.getClient().connect(mDevice.getAddress(), new BleConnectResponse() {
            @Override
            public void onResponse(int code, Bundle data) {
                BluetoothLog.v(String.format("onResponse code = %d", code));

                if (code == REQUEST_SUCCESS) {
                    BleGattProfile profile = data.getParcelable(EXTRA_GATT_PROFILE);
                    BluetoothLog.v(String.format("Profiles: \n%s", profile));
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        ClientManager.getClient().disconnect(mDevice.getAddress());
    }
}
