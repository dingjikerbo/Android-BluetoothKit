package com.inuker.testgattserver;

import android.app.Activity;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by liwentian on 2017/3/15.
 */

public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);

        Intent intent = getIntent();
        int pid = Binder.getCallingPid();
        int uid = Binder.getCallingUid();
        Log.i("bush", String.format("pid = %d, uid = %d", pid, uid));
    }
}
