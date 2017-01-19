package com.inuker.wave;

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

    WaveView mWave;

    private float X;

    private static final int MAX_VAL = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWave = (WaveView) findViewById(R.id.view);
        mWave.setMaxValue(MAX_VAL);
        mWave.start();

        mHandler = new Handler(this);
        mHandler.sendEmptyMessage(1);
    }

    @Override
    public boolean handleMessage(Message msg) {
        mWave.setValue((int) (Math.sin(X) * MAX_VAL));
        X += 0.01f;
        mHandler.sendEmptyMessageDelayed(1, 5);
        return true;
    }
}
