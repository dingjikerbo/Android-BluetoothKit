package com.inuker.bluetooth.library.model;

/**
 * Created by liwentian on 2016/3/29.
 */
public interface IBlePropCacher {

    void setPropName(String mac, String name);

    String getPropName(String mac);

    void setPropDid(String mac, String did);

    String getPropDid(String mac);

    void setPropDesc(String mac, String desc);

    String getPropDesc(String mac);

    void setPropModel(String mac, String model);

    String getPropModel(String mac);

    void setPropProductId(String mac, int productId);

    int getPropProductId(String mac);

    void setPropBoundStatus(String mac, int boundStatus);

    int getPropBoundStatus(String mac);

    int getPropExtra(String mac, String key, int defaultValue);

    void setPropExtra(String mac, String key, int value);

    boolean getPropExtra(String mac, String key, boolean defaultValue);

    void setPropExtra(String mac, String key, boolean value);

    String getPropExtra(String mac, String key);

    void setPropExtra(String mac, String key, String value);

    void removePropExtra(String mac, String key);

    void traverse(BleDevicePropCache.IPropTraverse traverse);
}
