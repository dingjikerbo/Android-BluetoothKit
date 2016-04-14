package com.inuker.bluetooth.library.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by liwentian on 2016/3/29.
 */
public class BleDeviceProp implements Serializable {

    /**
     * 设备名称
     */
    private String name;

    /**
     * 设备did
     */
    private String did;

    /**
     * 副标题
     */
    private String desc;

    /**
     * model
     */
    private String model;

    /**
     * product id
     */
    private int productId;

    /**
     * 绑定状态
     */
    private int boundStatus;

    /**
     * 扩展项，某些设备特定的一些缓存数据，Json格式
     *
     * @return
     */
    private JSONObject extras;

    public BleDeviceProp() {
        extras = new JSONObject();
    }

    public static BleDeviceProp fromJson(String json) {
        try {
            BleDeviceProp prop = new BleDeviceProp();
            JSONObject jsonObj = new JSONObject(json);
            prop.name = jsonObj.optString("name");
            prop.did = jsonObj.optString("did");
            prop.desc = jsonObj.optString("desc");
            prop.model = jsonObj.optString("model");
            prop.productId = jsonObj.optInt("productId");
            prop.boundStatus = jsonObj.optInt("boundStatus");

            JSONObject extras = jsonObj.optJSONObject("extras");
            if (extras != null) {
                prop.extras = extras;
            }

            return prop;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getBoundStatus() {
        return boundStatus;
    }

    public void setBoundStatus(int boundStatus) {
        this.boundStatus = boundStatus;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setExtra(String key, int value) {
        try {
            extras.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setExtra(String key, boolean value) {
        try {
            extras.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setExtra(String key, String value) {
        try {
            extras.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getExtra(String key, int defaultValue) {
        try {
            return extras.getInt(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public boolean getExtra(String key, boolean defaultValue) {
        try {
            return extras.getBoolean(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public String getExtra(String key) {
        try {
            return extras.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void removeExtra(String key) {
        extras.remove(key);
    }

    public String toJson() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("name", name);
            jsonObj.put("did", did);
            jsonObj.put("desc", desc);
            jsonObj.put("model", model);
            jsonObj.put("productId", productId);
            jsonObj.put("boundStatus", boundStatus);
            jsonObj.put("extras", extras);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj.toString();
    }
}
