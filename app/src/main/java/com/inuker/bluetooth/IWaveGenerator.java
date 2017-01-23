package com.inuker.bluetooth;

import com.inuker.bluetooth.library.utils.UUIDUtils;

import java.util.UUID;

/**
 * Created by dingjikerbo on 17/1/23.
 */

public interface IWaveGenerator {

    UUID SERVICE = UUIDUtils.makeUUID(0xFEE9);
    UUID CHARACTER = UUIDUtils.makeUUID(0x9805);
    int MODE = 0x06;

    void start(WaveResponse response);

    void stop();

    interface WaveResponse {
        void onRaw(int x, int y, int z);
        void onFail();
    }
}
