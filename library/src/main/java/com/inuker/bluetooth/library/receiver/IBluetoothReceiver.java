package com.inuker.bluetooth.library.receiver;

import android.content.Context;
import android.content.Intent;

import java.util.List;

/**
 * Created by liwentian on 2016/11/25.
 */

public interface IBluetoothReceiver {

    boolean onReceive(Context context, Intent intent);

    List<String> getActions();
}
