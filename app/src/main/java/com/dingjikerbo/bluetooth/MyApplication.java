package com.dingjikerbo.bluetooth;

import android.app.Application;
import android.util.Log;

/**
 * Created by liwentian on 2016/1/13.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("bush", String.format("Application onCreate, pid = %d, AGE = %d",
                android.os.Process.myPid(), Global.AGE));
    }
}
