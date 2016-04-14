package com.inuker.bluetooth.library.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.inuker.bluetooth.library.BaseManager;
import com.inuker.bluetooth.library.utils.BluetoothLog;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Created by liwentian on 2016/3/29.
 */
public final class BleDevicePropCache extends BaseManager implements IBlePropCacher {

    private static final String PREFS_TAG = "ble_device_prop_cache";

    private String mCurrentTag;
    private SharedPreferences mSharedPreferences;
    private HashMap<String, BleDeviceProp> mPropCache;

    private static BleDevicePropCache sInstance;

    private static final Executor SERIAL_EXECUTOR = new SerialExecutor();

    public static BleDevicePropCache getInstance() {
        if (sInstance == null) {
            synchronized (BleDevicePropCache.class) {
                if (sInstance == null) {
                    sInstance = new BleDevicePropCache();
                }
            }
        }
        return sInstance;
    }

    private BleDevicePropCache() {
        mPropCache = new HashMap<String, BleDeviceProp>();
    }

    public void reloadIfNeeded() {
        executeCacheTask(new BleCacheTask() {
            @Override
            void doInBackground() {
                reloadPropCache();
            }
        });
    }

    /**
     * 当APP启动或切换账号时刷新缓存
     */
    private void reloadPropCache() {
        String tag = getPrefsTag();

        if (TextUtils.isEmpty(tag)) {
            BluetoothLog.w("BleDevicePropCache clear");
            mPropCache.clear();
        } else if (!tag.equalsIgnoreCase(mCurrentTag)) {
            mCurrentTag = tag;

            long start = System.currentTimeMillis();
            mSharedPreferences = getContext().getSharedPreferences(mCurrentTag, Context.MODE_PRIVATE);

            Map<String, String> map = (Map<String, String>) mSharedPreferences.getAll();

            HashMap<String, BleDeviceProp> cache = new HashMap<String, BleDeviceProp>();
            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                BleDeviceProp prop = BleDeviceProp.fromJson(entry.getValue());

                if (prop != null) {
                    cache.put(entry.getKey(), prop);
                }
            }

            synchronized (mPropCache) {
                mPropCache.clear();
                mPropCache.putAll(cache);
            }

            long now = System.currentTimeMillis();
            BluetoothLog.w(String.format("BleDevicePropCache load takes %dms", now - start));
        }
    }

    @Override
    public void traverse(IPropTraverse traverse) {
        synchronized (mPropCache) {
            Iterator iterator = mPropCache.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, BleDeviceProp> entry = (Map.Entry<String, BleDeviceProp>) iterator.next();
                if (traverse.onTraverse(entry.getKey(), entry.getValue())) {
                    break;
                }
            }
        }
    }

    @Override
    public void setPropName(String mac, final String name) {
        setProp(mac, new IPropSetter() {
            @Override
            public void setProp(BleDeviceProp prop) {
                prop.setName(name);
            }
        });
    }

    @Override
    public String getPropName(String mac) {
        return getProp(mac, new IPropGetter<String>() {
            @Override
            public String getProp(BleDeviceProp prop) {
                return prop.getName();
            }
        });
    }

    @Override
    public void setPropDid(String mac, final String did) {
        setProp(mac, new IPropSetter() {
            @Override
            public void setProp(BleDeviceProp prop) {
                prop.setDid(did);
            }
        });
    }

    @Override
    public String getPropDid(String mac) {
        return getProp(mac, new IPropGetter<String>() {
            @Override
            public String getProp(BleDeviceProp prop) {
                return prop.getDid();
            }
        });
    }

    @Override
    public void setPropDesc(String mac, final String desc) {
        setProp(mac, new IPropSetter() {
            @Override
            public void setProp(BleDeviceProp prop) {
                prop.setDesc(desc);
            }
        });
    }

    @Override
    public String getPropDesc(String mac) {
        return getProp(mac, new IPropGetter<String>() {
            @Override
            public String getProp(BleDeviceProp prop) {
                return prop.getDesc();
            }
        });
    }

    @Override
    public void setPropModel(String mac, final String model) {
        setProp(mac, new IPropSetter() {
            @Override
            public void setProp(BleDeviceProp prop) {
                prop.setModel(model);
            }
        });
    }

    @Override
    public String getPropModel(String mac) {
        return getProp(mac, new IPropGetter<String>() {
            @Override
            public String getProp(BleDeviceProp prop) {
                return prop.getModel();
            }
        });
    }

    @Override
    public void setPropProductId(String mac, final int productId) {
        setProp(mac, new IPropSetter() {
            @Override
            public void setProp(BleDeviceProp prop) {
                prop.setProductId(productId);
            }
        });
    }

    @Override
    public int getPropProductId(String mac) {
        return getProp(mac, new IPropGetter<Integer>() {
            @Override
            public Integer getProp(BleDeviceProp prop) {
                return prop.getProductId();
            }
        });
    }

    @Override
    public void setPropBoundStatus(String mac, final int boundStatus) {
        setProp(mac, new IPropSetter() {
            @Override
            public void setProp(BleDeviceProp prop) {
                prop.setBoundStatus(boundStatus);
            }
        });
    }

    @Override
    public int getPropBoundStatus(String mac) {
        return getProp(mac, new IPropGetter<Integer>() {
            @Override
            public Integer getProp(BleDeviceProp prop) {
                return prop.getBoundStatus();
            }
        });
    }

    @Override
    public int getPropExtra(String mac, final String key, final int defaultValue) {
        return getProp(mac, new IPropGetter<Integer>() {
            @Override
            public Integer getProp(BleDeviceProp prop) {
                return prop.getExtra(key, defaultValue);
            }
        });
    }

    @Override
    public void setPropExtra(String mac, final String key, final int value) {
        setProp(mac, new IPropSetter() {
            @Override
            public void setProp(BleDeviceProp prop) {
                prop.setExtra(key, value);
            }
        });
    }

    @Override
    public boolean getPropExtra(String mac, final String key, final boolean defaultValue) {
        return getProp(mac, new IPropGetter<Boolean>() {
            @Override
            public Boolean getProp(BleDeviceProp prop) {
                return prop.getExtra(key, defaultValue);
            }
        });
    }

    @Override
    public void setPropExtra(String mac, final String key, final boolean value) {
        setProp(mac, new IPropSetter() {
            @Override
            public void setProp(BleDeviceProp prop) {
                prop.setExtra(key, value);
            }
        });
    }

    @Override
    public String getPropExtra(String mac, final String key) {
        return getProp(mac, new IPropGetter<String>() {
            @Override
            public String getProp(BleDeviceProp prop) {
                return prop.getExtra(key);
            }
        });
    }

    @Override
    public void setPropExtra(String mac, final String key, final String value) {
        setProp(mac, new IPropSetter() {
            @Override
            public void setProp(BleDeviceProp prop) {
                prop.setExtra(key, value);
            }
        });
    }

    @Override
    public void removePropExtra(String mac, final String key) {
        getProp(mac, new IPropGetter<Void>() {
            @Override
            public Void getProp(BleDeviceProp prop) {
                prop.removeExtra(key);
                return null;
            }
        });
    }

    private interface IPropSetter {
        void setProp(BleDeviceProp prop);
    }

    private interface IPropGetter<T> {
        T getProp(BleDeviceProp prop);
    }

    public interface IPropTraverse {
        boolean onTraverse(String mac, BleDeviceProp prop);
    }

    private <T> T getProp(String mac, IPropGetter<T> getter) {
        if (TextUtils.isEmpty(mac)) {
            return null;
        }

        synchronized (mPropCache) {
            BleDeviceProp prop = mPropCache.get(mac);
            if (prop != null) {
                return getter.getProp(prop);
            }
            return null;
        }
    }

    private void setProp(String mac, IPropSetter setter) {
        if (TextUtils.isEmpty(mac)) {
            return;
        }

        synchronized (mPropCache) {
            BleDeviceProp prop = mPropCache.get(mac);
            if (prop == null) {
                prop = new BleDeviceProp();
                mPropCache.put(mac, prop);
            }
            setter.setProp(prop);
            savePropCacheAsync(mac, prop);
        }
    }

    private void savePropCacheAsync(final String mac, final BleDeviceProp prop) {
        executeSerialCacheTask(new BleCacheTask() {
            @Override
            void doInBackground() {
                compatibleSave(mSharedPreferences, mac, prop.toJson());
            }
        });
    }

    public static void compatibleSave(SharedPreferences sp, final String key, final String value) {
        sp.edit().putString(key, value).commit();
    }

    private static class SerialExecutor implements Executor {
        final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();
        Runnable mActive;

        public synchronized void execute(final Runnable r) {
            mTasks.offer(new Runnable() {
                public void run() {
                    try {
                        r.run();
                    } finally {
                        scheduleNext();
                    }
                }
            });
            if (mActive == null) {
                scheduleNext();
            }
        }

        protected synchronized void scheduleNext() {
            if ((mActive = mTasks.poll()) != null) {
                AsyncTask.THREAD_POOL_EXECUTOR.execute(mActive);
            }
        }
    }

    private abstract class BleCacheTask extends AsyncTask<Void, Void, Void> {

        abstract void doInBackground();

        @Override
        protected Void doInBackground(Void... params) {
            doInBackground();
            return null;
        }
    }

    private void executeSerialCacheTask(final BleCacheTask task) {
        post(new Runnable() {

            @Override
            public void run() {
                task.executeOnExecutor(SERIAL_EXECUTOR);
            }
        });
    }

    private void executeCacheTask(final BleCacheTask task) {
        post(new Runnable() {

            @Override
            public void run() {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
    }

    private static String getPrefsTag() {
        return String.format("%s.%s", PREFS_TAG, "bush");
    }
}
