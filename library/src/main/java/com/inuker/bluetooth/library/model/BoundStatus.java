package com.inuker.bluetooth.library.model;

/**
 * Created by liwentian on 2016/3/29.
 */
public class BoundStatus {

    /**
     * 尚未绑定
     */
    public static final int NOT_BOUNDED = 0;

    /**
     * 本地绑定，表示安全连接成功，只适用于无需远端绑定的设备
     */
    public static final int LOCAL_BOUNDED = 1;

    /**
     * 云端绑定
     */
    public static final int REMOTE_BOUNDED = 2;
}
