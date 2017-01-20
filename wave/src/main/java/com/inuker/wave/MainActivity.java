package com.inuker.wave;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telecom.Call;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements Handler.Callback {

    private Handler mHandler;

    WaveView mWave1, mWave2, mWave3;

    private float X;

    private static final int MAX_VAL = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWave1 = (WaveView) findViewById(R.id.view1);
        mWave2 = (WaveView) findViewById(R.id.view2);
        mWave3 = (WaveView) findViewById(R.id.view3);

        WaveView.Config config1 = new WaveView.Config.Builder()
                .setBackgroundColor(Color.BLACK)
                .setForegroundColor(Color.WHITE)
                .setRange(-MAX_VAL, MAX_VAL)
                .setSliceCount(50)
                .setStrokeWidth(5)
                .build();

        mWave1.setConfig(config1);

        WaveView.Config config2 = new WaveView.Config.Builder()
                .setBackgroundColor(Color.BLACK)
                .setForegroundColor(Color.GREEN)
                .setRange(-MAX_VAL, MAX_VAL)
                .setSliceCount(50)
                .setStrokeWidth(5)
                .build();

        mWave2.setConfig(config2);

        WaveView.Config config3 = new WaveView.Config.Builder()
                .setBackgroundColor(Color.BLACK)
                .setForegroundColor(Color.RED)
                .setRange(-MAX_VAL, MAX_VAL)
                .setSliceCount(50)
                .setStrokeWidth(5)
                .build();

        mWave3.setConfig(config3);

        mHandler = new Handler(this);
        mHandler.sendEmptyMessageDelayed(1, 500);
    }

    @Override
    public boolean handleMessage(Message msg) {
        mWave1.setValue((int) (Math.sin(X) * MAX_VAL));
        mWave2.setValue((int) (Math.sin(2 * X) * MAX_VAL));
        mWave3.setValue((int) (Math.sin(4 * X) * MAX_VAL));

        X += 0.2f;

        mHandler.removeMessages(1);
        mHandler.sendEmptyMessageDelayed(1, 100);
        return true;
    }
}
