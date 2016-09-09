package com.inuker.bluetooth.library.connect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.SparseArray;

import com.inuker.bluetooth.library.BluetoothService;
import com.inuker.bluetooth.library.connect.listener.DisconnectListener;
import com.inuker.bluetooth.library.connect.listener.GattResponseListener;
import com.inuker.bluetooth.library.connect.listener.IBluetoothGattResponse;
import com.inuker.bluetooth.library.connect.listener.ReadCharacterListener;
import com.inuker.bluetooth.library.connect.listener.ReadRssiListener;
import com.inuker.bluetooth.library.connect.listener.ServiceDiscoverListener;
import com.inuker.bluetooth.library.connect.listener.WriteCharacterListener;
import com.inuker.bluetooth.library.connect.listener.WriteDescriptorListener;
import com.inuker.bluetooth.library.connect.request.BleRequest;
import com.inuker.bluetooth.library.connect.response.BluetoothGattResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.model.BleGattService;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.inuker.bluetooth.library.utils.ProxyUtils;
import com.inuker.bluetooth.library.utils.ProxyUtils.ProxyBulk;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Created by dingjikerbo on 16/4/8.
 */
public class BleConnectWorker implements Handler.Callback, IBleRequestProcessor, IBluetoothGattResponse, ProxyUtils.ProxyHandler, GattResponseListener {

    public static final int MSG_SCHEDULE_NEXT = 0x160;
    public static final int MSG_GATT_RESPONSE = 0x180;

    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice mBluetoothDevice;

    private IBleConnectDispatcher mBleConnectDispatcher;

    private BleRequest mCurrentRequest;

    private Handler mWorkerHandler;

    private volatile int mConnectStatus;

    private SparseArray<GattResponseListener> mGattResponseListeners;

    private IBluetoothGattResponse mBluetoothGattResponse;

    private Map<UUID, Map<UUID, BluetoothGattCharacteristic>> mDeviceProfile;

    public static BleConnectWorker attch(String mac, IBleConnectDispatcher dispatcher) {
        return new BleConnectWorker(mac, dispatcher);
    }

    private BleConnectWorker(String mac, IBleConnectDispatcher dispatcher) {
        mBleConnectDispatcher = dispatcher;

        BluetoothAdapter adapter = BluetoothUtils.getBluetoothLeAdapter();
        mBluetoothDevice = adapter.getRemoteDevice(mac);

        mWorkerHandler = new Handler(Looper.myLooper(), this);
        mBleConnectDispatcher.notifyHandlerReady(mWorkerHandler);

        mGattResponseListeners = new SparseArray<GattResponseListener>();
        mBluetoothGattResponse = ProxyUtils.getProxy(this, IBluetoothGattResponse.class, this);

        mDeviceProfile = new HashMap<UUID, Map<UUID, BluetoothGattCharacteristic>>();
    }

    private void refreshServiceProfile() {
        List<BluetoothGattService> services = mBluetoothGatt.getServices();

        Map<UUID, Map<UUID, BluetoothGattCharacteristic>> newProfiles = new HashMap<UUID, Map<UUID, BluetoothGattCharacteristic>>();

        for (BluetoothGattService service : services) {

//            BluetoothLog.v("Service: " + service.getUuid());

            Map<UUID, BluetoothGattCharacteristic> map = new HashMap<UUID, BluetoothGattCharacteristic>();
            newProfiles.put(service.getUuid(), map);

            List<BluetoothGattCharacteristic> characters = service
                    .getCharacteristics();

            for (BluetoothGattCharacteristic character : characters) {
//                BluetoothLog.v("character: uuid = "
//                        + character.getUuid());

                map.put(character.getUuid(), character);
            }
        }

        mDeviceProfile.clear();
        mDeviceProfile.putAll(newProfiles);
    }

    private BluetoothGattCharacteristic getCharacter(UUID service, UUID character, byte[] value) {
        BluetoothGattCharacteristic characteristic = getCharacter(service, character);
        if (characteristic != null) {
            characteristic.setValue(value);
        }
        return characteristic;
    }

    private BluetoothGattCharacteristic getCharacter(UUID service, UUID character) {
        BluetoothGattCharacteristic characteristic = null;

        if (service != null && character != null) {
            Map<UUID, BluetoothGattCharacteristic> characters = mDeviceProfile.get(service);
            if (characters != null) {
                characteristic = characters.get(character);
            }
        }

        return characteristic;
    }

    private void processRequest(BleRequest request) {
        mCurrentRequest = request;
        mCurrentRequest.process(this);
    }

    @Override
    public boolean readCharacteristic(UUID service, UUID character) {
        BluetoothGattCharacteristic characteristic = getCharacter(service, character);
        return characteristic != null ? mBluetoothGatt.readCharacteristic(characteristic) : false;
    }

    @Override
    public boolean writeCharacteristic(UUID service, UUID character, byte[] value) {
        BluetoothGattCharacteristic characteristic = getCharacter(service, character, value);
        return characteristic != null ? mBluetoothGatt.writeCharacteristic(characteristic) : false;
    }

    @Override
    public boolean writeCharacteristicWithNoRsp(UUID service, UUID character, byte[] value) {
        BluetoothGattCharacteristic characteristic = getCharacter(service, character, value);
        if (characteristic != null) {
            characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            return mBluetoothGatt.writeCharacteristic(characteristic);
        }
        return false;
    }

    @Override
    public boolean setCharacteristicNotification(UUID service, UUID character, boolean enable) {
        BluetoothGattCharacteristic characteristic = getCharacter(service, character);

        if (characteristic == null || !mBluetoothGatt.setCharacteristicNotification(characteristic, enable)) {
            return false;
        }

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);

        byte[] value = (enable ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);

        if (descriptor == null || !descriptor.setValue(value)) {
            return false;
        }

        return mBluetoothGatt.writeDescriptor(descriptor);
    }

    @Override
    public boolean readRemoteRssi() {
        return mBluetoothGatt.readRemoteRssi();
    }

    @Override
    public void refreshCache() {
        try {
            if (mBluetoothGatt != null) {
                Method refresh = BluetoothGatt.class.getMethod("refresh");
                if (refresh != null) {
                    refresh.setAccessible(true);
                    refresh.invoke(mBluetoothGatt, new Object[0]);
                }
            }
        } catch (Exception e) {
            BluetoothLog.e(e);
        }
    }

    private void setConnectStatus(int status) {
        mConnectStatus = status;
    }

    private void onScheduleNext(BleRequest newRequest) {
        if (mCurrentRequest != null) {
            throw new IllegalStateException("previous request ongoing");
        } else if (newRequest == null) {
            throw new NullPointerException("new request null");
        } else {
            processRequest(newRequest);
        }
    }

    @Override
    public void registerGattResponseListener(int responseId, GattResponseListener listener) {
        if (responseId > 0 && listener != null) {
            mGattResponseListeners.put(responseId, listener);
        }
    }

    @Override
    public void unregisterGattResponseListener(int responseId) {
        if (responseId > 0) {
            mGattResponseListeners.remove(responseId);
        }
    }

    @Override
    public void notifyRequestResult() {
        BleRequest request = mCurrentRequest;

        mCurrentRequest = null;

        mBleConnectDispatcher.notifyWorkerResult(request);
    }

    @Override
    public int getConnectStatus() {
        return mConnectStatus;
    }

    @Override
    public BleGattProfile getGattProfile() {
        BleGattProfile profile = new BleGattProfile();

        Iterator itor = mDeviceProfile.entrySet().iterator();

        List<BleGattService> services = new ArrayList<BleGattService>();

        while (itor.hasNext()) {
            Map.Entry entry = (Map.Entry) itor.next();

            UUID serviceUUID = (UUID) entry.getKey();

            BleGattService service = new BleGattService(serviceUUID);
            services.add(service);

            Map map = (Map) entry.getValue();
            service.addCharacters(map.keySet());
        }

        profile.addServices(services);

        return profile;
    }

    @Override
    public boolean openBluetoothGatt() {
        if (mBluetoothGatt != null) {
            closeBluetoothGatt();
            mBluetoothGatt = null;
        }

        if (mBluetoothGatt == null) {
            BluetoothLog.v(String.format("openBluetoothGatt for %s", mBluetoothDevice.getAddress()));
            mBluetoothGatt = mBluetoothDevice.connectGatt(getContext(), false,
                    new BluetoothGattResponse(mBluetoothGattResponse));
        }

        return mBluetoothGatt != null;
    }

    @Override
    public void disconnect() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
        }
    }

    @Override
    public void closeBluetoothGatt() {
        if (mBluetoothGatt != null) {
            BluetoothLog.v(String.format("closeBluetoothGatt for %s", mBluetoothDevice.getAddress()));

            mBluetoothGatt.close();

            mBluetoothGatt = null;

            mDeviceProfile.clear();

            setConnectStatus(STATUS_DEVICE_DISCONNECTED);
            broadcastConnectStatus(STATUS_DISCONNECTED);
        }
    }

    private void broadcastConnectStatus(int status) {
        Intent intent = new Intent(ACTION_CONNECT_STATUS_CHANGED);
        intent.putExtra(EXTRA_MAC, mBluetoothDevice.getAddress());
        intent.putExtra(EXTRA_STATUS, status);
        BluetoothUtils.sendBroadcast(intent);
    }

    @Override
    public void onConnectionStateChange(int status, int newState) {
        BluetoothLog.v(String.format("onConnectionStateChange for %s status = %d, newState = %d",
                mBluetoothDevice.getAddress(), status, newState));

        if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
            setConnectStatus(STATUS_DEVICE_CONNECTED);
            BluetoothLog.v(String.format("discoverServices for %s", mBluetoothDevice.getAddress()));
            mBluetoothGatt.discoverServices();
        } else {
            DisconnectListener listener = getGattResponseListener(GATT_RESP_DISCONNECT);

            if (listener != null) {
                listener.onDisconnected();
            } else {
                closeBluetoothGatt();
            }
        }
    }

    private <T> T getGattResponseListener(int id) {
        return (T) mGattResponseListeners.get(id);
    }

    @Override
    public void onServicesDiscovered(int status) {
        BluetoothLog.v(String.format("onServicesDiscovered for %s status = %d",
                mBluetoothDevice.getAddress(), status));

        if (status == BluetoothGatt.GATT_SUCCESS) {
            setConnectStatus(STATUS_DEVICE_SERVICE_READY);
            broadcastConnectStatus(STATUS_CONNECTED);

            refreshServiceProfile();
        } else {
            closeBluetoothGatt();
        }

        ServiceDiscoverListener listener = getGattResponseListener(GATT_RESP_SERVICE_DISCOVER);

        if (listener != null) {
            listener.onServicesDiscovered(status);
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGattCharacteristic characteristic, int status, byte[] value) {
        BluetoothLog.v(String.format("onCharacteristicRead status = %d, value = %s",
                status, ByteUtils.byteToString(value)));

        ReadCharacterListener listener = getGattResponseListener(GATT_RESP_CHARACTER_READ);
        if (listener != null) {
            listener.onCharacteristicRead(characteristic, status, value);
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGattCharacteristic characteristic, int status) {
        BluetoothLog.v(String.format("onCharacteristicWrite status = %d", status));

        WriteCharacterListener listener = getGattResponseListener(GATT_RESP_CHARACTER_WRITE);
        if (listener != null) {
            listener.onCharacteristicWrite(characteristic, status);
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGattCharacteristic characteristic, byte[] value) {
        BluetoothLog.v(String.format("onCharacteristicChanged service %s, character %s, value = %s",
                characteristic.getService().getUuid(), characteristic.getUuid(), ByteUtils.byteToString(value)));

        Intent intent = new Intent(ACTION_CHARACTER_CHANGED);
        intent.putExtra(EXTRA_MAC, mBluetoothDevice.getAddress());
        intent.putExtra(EXTRA_SERVICE_UUID, characteristic.getService().getUuid());
        intent.putExtra(EXTRA_CHARACTER_UUID, characteristic.getUuid());
        intent.putExtra(EXTRA_BYTE_VALUE, value);
        BluetoothUtils.sendBroadcast(intent);
    }

    @Override
    public void onDescriptorWrite(BluetoothGattDescriptor descriptor, int status) {
        WriteDescriptorListener listener = getGattResponseListener(GATT_RESP_DESCRIPTOR_WRITE);
        if (listener != null) {
            listener.onDescriptorWrite(status, descriptor);
        }
    }

    @Override
    public void onReadRemoteRssi(int rssi, int status) {
        ReadRssiListener listener = getGattResponseListener(GATT_RESP_READ_RSSI);
        if (listener != null) {
            listener.onReadRemoteRssi(rssi, status);
        }
    }

    @Override
    public boolean onPreCalled(Object object, Method method, Object[] args) {
        mWorkerHandler.obtainMessage(MSG_GATT_RESPONSE,
                new ProxyBulk(object, method, args)).sendToTarget();
        return false;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_SCHEDULE_NEXT:
                onScheduleNext((BleRequest) msg.obj);
                break;

            case MSG_GATT_RESPONSE:
                ProxyBulk.safeInvoke(msg.obj);
                break;
        }

        return true;
    }

    private Context getContext() {
        return BluetoothService.getContext();
    }
}
