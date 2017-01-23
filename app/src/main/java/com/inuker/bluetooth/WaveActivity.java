package com.inuker.bluetooth;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.BluetoothContext;
import com.inuker.bluetooth.library.Code;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.UUIDUtils;
import com.inuker.bluetooth.view.WaveView;

import java.util.UUID;

/**
 * Created by dingjikerbo on 17/1/23.
 */

public class WaveActivity extends Activity implements IWaveGenerator.WaveResponse {

    private static final boolean DEBUG = true;

    private IWaveGenerator mGenerator;

    private WaveView mWaveX, mWaveY, mWaveZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wave_activity);

        mWaveX = (WaveView) findViewById(R.id.wavex);
        WaveView.Config config1 = new WaveView.Config.Builder()
                .setBackgroundColor(Color.WHITE)
                .setForegroundColor(Color.BLACK)
                .setSliceCount(20)
                .setStrokeWidth(5)
                .setRange(Short.MIN_VALUE, Short.MAX_VALUE)
                .build();
        mWaveX.setConfig(config1);

        mWaveY = (WaveView) findViewById(R.id.wavey);
        WaveView.Config config2 = new WaveView.Config.Builder()
                .setBackgroundColor(Color.WHITE)
                .setForegroundColor(Color.RED)
                .setSliceCount(20)
                .setStrokeWidth(5)
                .setRange(Short.MIN_VALUE, Short.MAX_VALUE)
                .build();
        mWaveY.setConfig(config2);

        mWaveZ = (WaveView) findViewById(R.id.wavez);
        WaveView.Config config3 = new WaveView.Config.Builder()
                .setBackgroundColor(Color.WHITE)
                .setForegroundColor(Color.GREEN)
                .setSliceCount(20)
                .setStrokeWidth(5)
                .setRange(Short.MIN_VALUE, Short.MAX_VALUE)
                .build();
        mWaveZ.setConfig(config3);

        String mac = getIntent().getStringExtra("mac");

        mGenerator = DEBUG ? new WaveTester() : new WaveGenerator(mac);
        mGenerator.start(this);
    }

    @Override
    protected void onDestroy() {
        mGenerator.stop();
        super.onDestroy();
    }

    @Override
    public void onRaw(int x, int y, int z) {
        BluetoothLog.v(String.format("onRaw x = %d, y = %d, z = %d", x, y, z));
        mWaveX.setValue(x);
        mWaveY.setValue(y);
        mWaveZ.setValue(z);
    }

    @Override
    public void onFail() {
        BluetoothContext.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000);
    }
}
