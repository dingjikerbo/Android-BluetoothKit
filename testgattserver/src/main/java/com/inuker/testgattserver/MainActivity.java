package com.inuker.testgattserver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.UUIDUtils;

import java.nio.charset.Charset;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Button mBtnStart, mBtnStop;

    private BluetoothLeAdvertiser mAdvertiser;

    private AdvertiseSettings mSettings;

    private BluetoothManager mManager;

    private BluetoothGattServer mServer;

    private TextView mTvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnStart = (Button) findViewById(R.id.start);
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startAdvertise();
            }
        });

        mBtnStop = (Button) findViewById(R.id.stop);
        mBtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAdvertiser != null) {
                    mAdvertiser.stopAdvertising(advertisingCallback);
                }
            }
        });

        if (!BluetoothAdapter.getDefaultAdapter().isMultipleAdvertisementSupported()) {
            Toast.makeText(this, "Multiple advertisement not supported", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Multiple advertisement supported", Toast.LENGTH_SHORT).show();
        }

        mAdvertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();

        mSettings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(true)
//                .setTimeout(60000)
                .build();

    }

    private void log(String msg) {
        BluetoothLog.v(msg);
    }

    private void startAdvertise() {
        ParcelUuid uuid1 = new ParcelUuid(UUIDUtils.makeUUID(0x9AAF));

        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .addServiceUuid(uuid1)
                .addServiceData(uuid1, new byte[] {1, 2})
//                .addManufacturerData(1, new byte[]{5, 6})
//                .setIncludeDeviceName(true)
//                .setIncludeTxPowerLevel(true)
                .build();

        mAdvertiser.startAdvertising(mSettings, data, advertisingCallback);

        mManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);

        mServer = mManager.openGattServer(this, new BluetoothGattServerCallback() {
            @Override
            public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
                super.onConnectionStateChange(device, status, newState);
                log(String.format("onConnectionStateChange mac = %s, name = %s, status = %d, newState = %d",
                        device.getAddress(), device.getName(), status, newState));
            }

            @Override
            public void onServiceAdded(int status, BluetoothGattService service) {
                super.onServiceAdded(status, service);
                log(String.format("onServiceAdded status = %d, service = %s",
                        status, service.getUuid().toString()));
            }

            @Override
            public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
                log(String.format("onCharacteristicReadRequest %s, requestId = %d, offset = %d, uuid = %s", device.getAddress(), requestId,
                        offset, characteristic.getUuid().toString()));
            }

            @Override
            public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
                super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
                log(String.format("onCharacteristicWriteRequest %s, requestId = %d, uuid = %s, prepareWrite = %b, responseNeeded = %b, offset = %d, value = %s", device.getAddress(), requestId,
                        characteristic.getUuid().toString(), preparedWrite, responseNeeded, offset, new String(value)));
            }

            @Override
            public void onNotificationSent(BluetoothDevice device, int status) {
                super.onNotificationSent(device, status);
            }

            @Override
            public void onMtuChanged(BluetoothDevice device, int mtu) {
                super.onMtuChanged(device, mtu);
                log(String.format("onMtuChanged %s, mtu = %d", device.getAddress(), mtu));
            }
        });

        BluetoothGattService service = new BluetoothGattService(UUIDUtils.makeUUID(0x1234), BluetoothGattService.SERVICE_TYPE_PRIMARY);

        BluetoothGattCharacteristic characteristic1 = new BluetoothGattCharacteristic(UUIDUtils.makeUUID(0x1),
                BluetoothGattCharacteristic.FORMAT_UINT8, BluetoothGattCharacteristic.PERMISSION_WRITE);

        BluetoothGattCharacteristic characteristic2 = new BluetoothGattCharacteristic(UUIDUtils.makeUUID(0x2),
                BluetoothGattCharacteristic.FORMAT_UINT8, BluetoothGattCharacteristic.PERMISSION_WRITE);

        BluetoothGattCharacteristic characteristic3 = new BluetoothGattCharacteristic(UUIDUtils.makeUUID(0x3),
                BluetoothGattCharacteristic.FORMAT_UINT8, BluetoothGattCharacteristic.PERMISSION_WRITE);

        service.addCharacteristic(characteristic1);
        service.addCharacteristic(characteristic2);
        service.addCharacteristic(characteristic3);

        mServer.addService(service);
    }

    private final AdvertiseCallback advertisingCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            log("Advertising onStartSuccess: " + settingsInEffect);
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            log("Advertising onStartFailure: " + errorCode);
        }
    };
}
