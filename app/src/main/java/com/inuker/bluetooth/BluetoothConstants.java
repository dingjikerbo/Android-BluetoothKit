package com.inuker.bluetooth;

import com.inuker.bluetooth.library.utils.UUIDUtils;

import java.util.UUID;

/**
 * Created by dingjikerbo on 2016/8/27.
 */
public class BluetoothConstants {

    public static final int INUKER_UUID = 0xFE95;

    public static final UUID MISERVICE = UUIDUtils
            .makeUUID(BluetoothConstants.INUKER_UUID);

    /**
     * 蓝牙安全连接token通知
     */
    public static final UUID CHARACTER_TOKEN = UUIDUtils.makeUUID(0x01);

    /**
     * 固件版本
     */
    public static final UUID CHARACTER_FIRMWARE_VERSION = UUIDUtils.makeUUID(0x04);

    /**
     * 蓝牙快连时发送wifi ssid
     */
    public static final UUID CHARACTER_WIFIAPSSID = UUIDUtils.makeUUID(0x5);

    /**
     * 蓝牙快连时发送wifi密码
     */
    public static final UUID CHARACTER_WIFIAPPW = UUIDUtils.makeUUID(0x6);

    /**
     * 蓝牙安全连接时事件通知
     */
    public static final UUID CHARACTER_EVENT = UUIDUtils.makeUUID(0x10);

    /**
     * 蓝牙快连时发送用户账号ID
     */
    public static final UUID CHARACTER_WIFIUID = UUIDUtils.makeUUID(0x11);

    /**
     * 蓝牙快连时设备状态通知
     */
    public static final UUID CHARACTER_WIFISTATUS = UUIDUtils.makeUUID(0x5);

    /**
     * 设备SN
     */
    public static final UUID CHARACTER_SN = UUIDUtils.makeUUID(0x13);

    /**
     * 设备BeaconKey
     */
    public static final UUID CHARACTER_BEACONKEY = UUIDUtils.makeUUID(0x14);
}
