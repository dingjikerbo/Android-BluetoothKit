package com.inuker.bluetooth.library.receiver;

import android.content.Context;
import android.content.Intent;

import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.receiver.listener.BleCharacterChangeListener;
import com.inuker.bluetooth.library.receiver.listener.BluetoothReceiverListener;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by dingjikerbo on 16/11/26.
 */

public class BleCharacterChangeReceiver extends AbsBluetoothReceiver {

    private static final String[] ACTIONS = {
            Constants.ACTION_CHARACTER_CHANGED
    };

    protected BleCharacterChangeReceiver(IReceiverDispatcher dispatcher) {
        super(dispatcher);
    }

    public static BleCharacterChangeReceiver newInstance(IReceiverDispatcher dispatcher) {
        return new BleCharacterChangeReceiver(dispatcher);
    }

    @Override
    List<String> getActions() {
        return Arrays.asList(ACTIONS);
    }

    @Override
    boolean onReceive(Context context, Intent intent) {
        String mac = intent.getStringExtra(Constants.EXTRA_MAC);
        UUID service = (UUID) intent.getSerializableExtra(Constants.EXTRA_SERVICE_UUID);
        UUID character = (UUID) intent.getSerializableExtra(Constants.EXTRA_CHARACTER_UUID);
        byte[] value = intent.getByteArrayExtra(Constants.EXTRA_BYTE_VALUE);
        onCharacterChanged(mac, service, character, value);
        return true;
    }

    private void onCharacterChanged(String mac, UUID service, UUID character, byte[] value) {
        List<BluetoothReceiverListener> listeners = getListeners(BleCharacterChangeListener.class);
        for (BluetoothReceiverListener listener : listeners) {
            listener.invoke(mac, service, character, value);
        }
    }
}
