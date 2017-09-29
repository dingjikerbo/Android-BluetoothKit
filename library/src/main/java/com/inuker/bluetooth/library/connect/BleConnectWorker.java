package com.inuker.bluetooth.library.connect;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.RuntimeChecker;
import com.inuker.bluetooth.library.connect.listener.GattResponseListener;
import com.inuker.bluetooth.library.connect.listener.IBluetoothGattResponse;
import com.inuker.bluetooth.library.connect.listener.ReadCharacterListener;
import com.inuker.bluetooth.library.connect.listener.ReadDescriptorListener;
import com.inuker.bluetooth.library.connect.listener.ReadRssiListener;
import com.inuker.bluetooth.library.connect.listener.RequestMtuListener;
import com.inuker.bluetooth.library.connect.listener.ServiceDiscoverListener;
import com.inuker.bluetooth.library.connect.listener.WriteCharacterListener;
import com.inuker.bluetooth.library.connect.listener.WriteDescriptorListener;
import com.inuker.bluetooth.library.connect.response.BluetoothGattResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.inuker.bluetooth.library.utils.Version;
import com.inuker.bluetooth.library.utils.proxy.ProxyBulk;
import com.inuker.bluetooth.library.utils.proxy.ProxyInterceptor;
import com.inuker.bluetooth.library.utils.proxy.ProxyUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Created by dingjikerbo on 16/4/8.
 */
public class BleConnectWorker implements Handler.Callback, IBleConnectWorker, IBluetoothGattResponse, ProxyInterceptor, RuntimeChecker {

    private static final int MSG_GATT_RESPONSE = 0x120;

    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice mBluetoothDevice;

    private GattResponseListener mGattResponseListener;

    private Handler mWorkerHandler;

    private volatile int mConnectStatus;

    private BleGattProfile mBleGattProfile;
    private Map<UUID, Map<UUID, BluetoothGattCharacteristic>> mDeviceProfile;

    private IBluetoothGattResponse mBluetoothGattResponse;

    private RuntimeChecker mRuntimeChecker;

    public BleConnectWorker(String mac, RuntimeChecker runtimeChecker) {
        BluetoothAdapter adapter = BluetoothUtils.getBluetoothAdapter();
        if (adapter != null) {
            mBluetoothDevice = adapter.getRemoteDevice(mac);
        } else {
            throw new IllegalStateException("ble adapter null");
        }

        mRuntimeChecker = runtimeChecker;
        mWorkerHandler = new Handler(Looper.myLooper(), this);
        mDeviceProfile = new HashMap<UUID, Map<UUID, BluetoothGattCharacteristic>>();
        mBluetoothGattResponse = ProxyUtils.getProxy(this, IBluetoothGattResponse.class, this);
    }

    private void refreshServiceProfile() {
        BluetoothLog.v(String.format("refreshServiceProfile for %s", mBluetoothDevice.getAddress()));

        List<BluetoothGattService> services = mBluetoothGatt.getServices();

        Map<UUID, Map<UUID, BluetoothGattCharacteristic>> newProfiles = new HashMap<UUID, Map<UUID, BluetoothGattCharacteristic>>();

        for (BluetoothGattService service : services) {
            UUID serviceUUID = service.getUuid();

            Map<UUID, BluetoothGattCharacteristic> map = newProfiles.get(serviceUUID);

            if (map == null) {
                BluetoothLog.v("Service: " + serviceUUID);
                map = new HashMap<UUID, BluetoothGattCharacteristic>();
                newProfiles.put(service.getUuid(), map);
            }

            List<BluetoothGattCharacteristic> characters = service
                    .getCharacteristics();

            for (BluetoothGattCharacteristic character : characters) {
                UUID characterUUID = character.getUuid();
                BluetoothLog.v("character: uuid = " + characterUUID);
                map.put(character.getUuid(), character);
            }
        }

        mDeviceProfile.clear();
        mDeviceProfile.putAll(newProfiles);
        mBleGattProfile = new BleGattProfile(mDeviceProfile);
    }

    private BluetoothGattCharacteristic getCharacter(UUID service, UUID character) {
        BluetoothGattCharacteristic characteristic = null;

        if (service != null && character != null) {
            Map<UUID, BluetoothGattCharacteristic> characters = mDeviceProfile.get(service);
            if (characters != null) {
                characteristic = characters.get(character);
            }
        }

        if (characteristic == null) {
            if (mBluetoothGatt != null) {
                BluetoothGattService gattService = mBluetoothGatt.getService(service);
                if (gattService != null) {
                    characteristic = gattService.getCharacteristic(character);
                }
            }
        }

        return characteristic;
    }

    private void setConnectStatus(int status) {
        BluetoothLog.v(String.format("setConnectStatus status = %s", Constants.getStatusText(status)));
        mConnectStatus = status;
    }

    @Override
    public void onConnectionStateChange(int status, int newState) {
        checkRuntime();

        BluetoothLog.v(String.format("onConnectionStateChange for %s: status = %d, newState = %d",
                mBluetoothDevice.getAddress(), status, newState));

        if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
            setConnectStatus(Constants.STATUS_DEVICE_CONNECTED);

            if (mGattResponseListener != null) {
                mGattResponseListener.onConnectStatusChanged(true);
            }
        } else {
            closeGatt();
        }
    }

    @Override
    public void onServicesDiscovered(int status) {
        checkRuntime();

        BluetoothLog.v(String.format("onServicesDiscovered for %s: status = %d",
                mBluetoothDevice.getAddress(), status));

        if (status == BluetoothGatt.GATT_SUCCESS) {
            setConnectStatus(Constants.STATUS_DEVICE_SERVICE_READY);
            broadcastConnectStatus(Constants.STATUS_CONNECTED);
            refreshServiceProfile();
        }

        if (mGattResponseListener != null && mGattResponseListener instanceof ServiceDiscoverListener) {
            ((ServiceDiscoverListener) mGattResponseListener).onServicesDiscovered(status, mBleGattProfile);
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGattCharacteristic characteristic, int status, byte[] value) {
        checkRuntime();

        BluetoothLog.v(String.format(
                "onCharacteristicRead for %s: status = %d, service = 0x%s, character = 0x%s, value = %s",
                mBluetoothDevice.getAddress(),
                status,
                characteristic.getService().getUuid(),
                characteristic.getUuid(),
                ByteUtils.byteToString(value)));

        if (mGattResponseListener != null && mGattResponseListener instanceof ReadCharacterListener) {
            ((ReadCharacterListener) mGattResponseListener).onCharacteristicRead(characteristic, status, value);
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGattCharacteristic characteristic, int status, byte[] value) {
        checkRuntime();

        BluetoothLog.v(String.format(
                "onCharacteristicWrite for %s: status = %d, service = 0x%s, character = 0x%s, value = %s",
                mBluetoothDevice.getAddress(),
                status,
                characteristic.getService().getUuid(),
                characteristic.getUuid(),
                ByteUtils.byteToString(value)));

        if (mGattResponseListener != null && mGattResponseListener instanceof WriteCharacterListener) {
            ((WriteCharacterListener) mGattResponseListener).onCharacteristicWrite(characteristic, status, value);
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGattCharacteristic characteristic, byte[] value) {
        checkRuntime();

        BluetoothLog.v(String.format("onCharacteristicChanged for %s: value = %s, service = 0x%s, character = 0x%s",
                mBluetoothDevice.getAddress(),
                ByteUtils.byteToString(value),
                characteristic.getService().getUuid(),
                characteristic.getUuid()));

        broadcastCharacterChanged(characteristic.getService().getUuid(), characteristic.getUuid(), value);
    }

    @Override
    public void onDescriptorRead(BluetoothGattDescriptor descriptor, int status, byte[] value) {
        checkRuntime();

        BluetoothLog.v(String.format("onDescriptorRead for %s: status = %d, service = 0x%s, character = 0x%s, descriptor = 0x%s",
                mBluetoothDevice.getAddress(),
                status,
                descriptor.getCharacteristic().getService().getUuid(),
                descriptor.getCharacteristic().getUuid(),
                descriptor.getUuid()));

        if (mGattResponseListener != null && mGattResponseListener instanceof ReadDescriptorListener) {
            ((ReadDescriptorListener) mGattResponseListener).onDescriptorRead(descriptor, status, value);
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGattDescriptor descriptor, int status) {
        checkRuntime();

        BluetoothLog.v(String.format("onDescriptorWrite for %s: status = %d, service = 0x%s, character = 0x%s, descriptor = 0x%s",
                mBluetoothDevice.getAddress(),
                status,
                descriptor.getCharacteristic().getService().getUuid(),
                descriptor.getCharacteristic().getUuid(),
                descriptor.getUuid()));

        if (mGattResponseListener != null && mGattResponseListener instanceof WriteDescriptorListener) {
            ((WriteDescriptorListener) mGattResponseListener).onDescriptorWrite(descriptor, status);
        }
    }

    @Override
    public void onReadRemoteRssi(int rssi, int status) {
        checkRuntime();

        BluetoothLog.v(String.format("onReadRemoteRssi for %s, rssi = %d, status = %d",
                mBluetoothDevice.getAddress(), rssi, status));

        if (mGattResponseListener != null && mGattResponseListener instanceof ReadRssiListener) {
            ((ReadRssiListener) mGattResponseListener).onReadRemoteRssi(rssi, status);
        }
    }

    @Override
    public void onMtuChanged(int mtu, int status) {
        checkRuntime();

        BluetoothLog.v(String.format("onMtuChanged for %s, mtu = %d, status = %d",
                mBluetoothDevice.getAddress(), mtu, status));

        if (mGattResponseListener != null && mGattResponseListener instanceof RequestMtuListener) {
            ((RequestMtuListener) mGattResponseListener).onMtuChanged(mtu, status);
        }
    }

    private void broadcastConnectStatus(int status) {
        Intent intent = new Intent(Constants.ACTION_CONNECT_STATUS_CHANGED);
        intent.putExtra(Constants.EXTRA_MAC, mBluetoothDevice.getAddress());
        intent.putExtra(Constants.EXTRA_STATUS, status);
        BluetoothUtils.sendBroadcast(intent);
    }

    private void broadcastCharacterChanged(UUID service, UUID character,
    byte[] value) {
        Intent intent = new Intent(
                Constants.ACTION_CHARACTER_CHANGED);
        intent.putExtra(Constants.EXTRA_MAC,
                mBluetoothDevice.getAddress());
        intent.putExtra(Constants.EXTRA_SERVICE_UUID, service);
        intent.putExtra(Constants.EXTRA_CHARACTER_UUID, character);
        intent.putExtra(Constants.EXTRA_BYTE_VALUE, value);
        BluetoothUtils.sendBroadcast(intent);
    }

    @Override
    public boolean openGatt() {
        checkRuntime();

        BluetoothLog.v(String.format("openGatt for %s", getAddress()));

        if (mBluetoothGatt != null) {
            BluetoothLog.e(String.format("Previous gatt not closed"));
            return true;
        }

        Context context = BluetoothUtils.getContext();
        BluetoothGattCallback callback = new BluetoothGattResponse(mBluetoothGattResponse);

        if (Version.isMarshmallow()) {
            mBluetoothGatt = mBluetoothDevice.connectGatt(context, false, callback, BluetoothDevice.TRANSPORT_LE);
        } else {
            mBluetoothGatt = mBluetoothDevice.connectGatt(context, false, callback);
        }

        if (mBluetoothGatt == null) {
            BluetoothLog.e(String.format("openGatt failed: connectGatt return null!"));
            return false;
        }

        return true;
    }

    private String getAddress() {
        return mBluetoothDevice.getAddress();
    }

    @Override
    public void closeGatt() {
        checkRuntime();

        BluetoothLog.v(String.format("closeGatt for %s", getAddress()));

        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }

        if (mGattResponseListener != null) {
            mGattResponseListener.onConnectStatusChanged(false);
        }

        setConnectStatus(Constants.STATUS_DEVICE_DISCONNECTED);
        broadcastConnectStatus(Constants.STATUS_DISCONNECTED);
    }

    @Override
    public boolean discoverService() {
        checkRuntime();

        BluetoothLog.v(String.format("discoverService for %s", getAddress()));

        if (mBluetoothGatt == null) {
            BluetoothLog.e(String.format("discoverService but gatt is null!"));
            return false;
        }

        if (!mBluetoothGatt.discoverServices()) {
            BluetoothLog.e(String.format("discoverServices failed"));
            return false;
        }

        return true;
    }

    @Override
    public int getCurrentStatus() {
        checkRuntime();
        return mConnectStatus;
    }

    @Override
    public void registerGattResponseListener(GattResponseListener listener) {
        checkRuntime();
        mGattResponseListener = listener;
    }

    @Override
    public void clearGattResponseListener(GattResponseListener listener) {
        checkRuntime();

        if (mGattResponseListener == listener) {
            mGattResponseListener = null;
        }
    }

    @Override
    public boolean refreshDeviceCache() {
        BluetoothLog.v(String.format("refreshDeviceCache for %s", getAddress()));

        checkRuntime();

        if (mBluetoothGatt == null) {
            BluetoothLog.e(String.format("ble gatt null"));
            return false;
        }

        if (!BluetoothUtils.refreshGattCache(mBluetoothGatt)) {
            BluetoothLog.e(String.format("refreshDeviceCache failed"));
            return false;
        }

        return true;
    }

    @Override
    public boolean readCharacteristic(UUID service, UUID character) {
        BluetoothLog.v(String.format("readCharacteristic for %s: service = 0x%s, character = 0x%s",
                mBluetoothDevice.getAddress(), service, character));

        checkRuntime();

        BluetoothGattCharacteristic characteristic = getCharacter(service, character);

        if (characteristic == null) {
            BluetoothLog.e(String.format("characteristic not exist!"));
            return false;
        }

//        if (!isCharacteristicReadable(characteristic)) {
//            BluetoothLog.e(String.format("characteristic not readable!"));
//            return false;
//        }

        if (mBluetoothGatt == null) {
            BluetoothLog.e(String.format("ble gatt null"));
            return false;
        }

        if (!mBluetoothGatt.readCharacteristic(characteristic)) {
            BluetoothLog.e(String.format("readCharacteristic failed"));
            return false;
        }

        return true;
    }

    @Override
    public boolean writeCharacteristic(UUID service, UUID character, byte[] value) {
        BluetoothLog.v(String.format("writeCharacteristic for %s: service = 0x%s, character = 0x%s, value = 0x%s",
                mBluetoothDevice.getAddress(), service, character, ByteUtils.byteToString(value)));

        checkRuntime();

        BluetoothGattCharacteristic characteristic = getCharacter(service, character);

        if (characteristic == null) {
            BluetoothLog.e(String.format("characteristic not exist!"));
            return false;
        }

//        if (!isCharacteristicWritable(characteristic)) {
//            BluetoothLog.e(String.format("characteristic not writable!"));
//            return false;
//        }

        if (mBluetoothGatt == null) {
            BluetoothLog.e(String.format("ble gatt null"));
            return false;
        }

        characteristic.setValue(value != null ? value : ByteUtils.EMPTY_BYTES);

        if (!mBluetoothGatt.writeCharacteristic(characteristic)) {
            BluetoothLog.e(String.format("writeCharacteristic failed"));
            return false;
        }

        return true;
    }

    @Override
    public boolean readDescriptor(UUID service, UUID character, UUID descriptor) {
        BluetoothLog.v(String.format("readDescriptor for %s: service = 0x%s, character = 0x%s, descriptor = 0x%s",
                mBluetoothDevice.getAddress(), service, character, descriptor));

        checkRuntime();

        BluetoothGattCharacteristic characteristic = getCharacter(service, character);

        if (characteristic == null) {
            BluetoothLog.e(String.format("characteristic not exist!"));
            return false;
        }

        BluetoothGattDescriptor gattDescriptor = characteristic.getDescriptor(descriptor);
        if (gattDescriptor == null) {
            BluetoothLog.e(String.format("descriptor not exist"));
            return false;
        }

        if (mBluetoothGatt == null) {
            BluetoothLog.e(String.format("ble gatt null"));
            return false;
        }

        if (!mBluetoothGatt.readDescriptor(gattDescriptor)) {
            BluetoothLog.e(String.format("readDescriptor failed"));
            return false;
        }

        return true;
    }

    @Override
    public boolean writeDescriptor(UUID service, UUID character, UUID descriptor, byte[] value) {
        BluetoothLog.v(String.format("writeDescriptor for %s: service = 0x%s, character = 0x%s, descriptor = 0x%s, value = 0x%s",
                mBluetoothDevice.getAddress(), service, character, descriptor, ByteUtils.byteToString(value)));

        checkRuntime();

        BluetoothGattCharacteristic characteristic = getCharacter(service, character);

        if (characteristic == null) {
            BluetoothLog.e(String.format("characteristic not exist!"));
            return false;
        }

        BluetoothGattDescriptor gattDescriptor = characteristic.getDescriptor(descriptor);
        if (gattDescriptor == null) {
            BluetoothLog.e(String.format("descriptor not exist"));
            return false;
        }

        if (mBluetoothGatt == null) {
            BluetoothLog.e(String.format("ble gatt null"));
            return false;
        }

        gattDescriptor.setValue(value != null ? value : ByteUtils.EMPTY_BYTES);

        if (!mBluetoothGatt.writeDescriptor(gattDescriptor)) {
            BluetoothLog.e(String.format("writeDescriptor failed"));
            return false;
        }

        return true;
    }

    @Override
    public boolean writeCharacteristicWithNoRsp(UUID service, UUID character, byte[] value) {
        BluetoothLog.v(String.format("writeCharacteristicWithNoRsp for %s: service = 0x%s, character = 0x%s, value = 0x%s",
                mBluetoothDevice.getAddress(), service, character, ByteUtils.byteToString(value)));

        checkRuntime();

        BluetoothGattCharacteristic characteristic = getCharacter(service, character);

        if (characteristic == null) {
            BluetoothLog.e(String.format("characteristic not exist!"));
            return false;
        }

//        if (!isCharacteristicNoRspWritable(characteristic)) {
//            BluetoothLog.e(String.format("characteristic not norsp writable!"));
//            return false;
//        }

        if (mBluetoothGatt == null) {
            BluetoothLog.e(String.format("ble gatt null"));
            return false;
        }

        characteristic.setValue(value != null ? value : ByteUtils.EMPTY_BYTES);
        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);

        if (!mBluetoothGatt.writeCharacteristic(characteristic)) {
            BluetoothLog.e(String.format("writeCharacteristic failed"));
            return false;
        }

        return true;
    }

    @Override
    public boolean setCharacteristicNotification(UUID service, UUID character, boolean enable) {
        checkRuntime();

        BluetoothLog.v(String.format("setCharacteristicNotification for %s, service = %s, character = %s, enable = %b",
                getAddress(), service, character, enable));

        BluetoothGattCharacteristic characteristic = getCharacter(service, character);

        if (characteristic == null) {
            BluetoothLog.e(String.format("characteristic not exist!"));
            return false;
        }

//        if (!isCharacteristicNotifyable(characteristic)) {
//            BluetoothLog.e(String.format("characteristic not notifyable!"));
//            return false;
//        }

        if (mBluetoothGatt == null) {
            BluetoothLog.e(String.format("ble gatt null"));
            return false;
        }

        if (!mBluetoothGatt.setCharacteristicNotification(characteristic, enable)) {
            BluetoothLog.e(String.format("setCharacteristicNotification failed"));
            return false;
        }

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(Constants.CLIENT_CHARACTERISTIC_CONFIG);

        if (descriptor == null) {
            BluetoothLog.e(String.format("getDescriptor for notify null!"));
            return false;
        }

        byte[] value = (enable ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);

        if (!descriptor.setValue(value)) {
            BluetoothLog.e(String.format("setValue for notify descriptor failed!"));
            return false;
        }

        if (!mBluetoothGatt.writeDescriptor(descriptor)) {
            BluetoothLog.e(String.format("writeDescriptor for notify failed"));
            return false;
        }

        return true;
    }

    @Override
    public boolean setCharacteristicIndication(UUID service, UUID character, boolean enable) {
        checkRuntime();

        BluetoothLog.v(String.format("setCharacteristicIndication for %s, service = %s, character = %s, enable = %b",
                getAddress(), service, character, enable));

        BluetoothGattCharacteristic characteristic = getCharacter(service, character);

        if (characteristic == null) {
            BluetoothLog.e(String.format("characteristic not exist!"));
            return false;
        }

//        if (!isCharacteristicIndicatable(characteristic)) {
//            BluetoothLog.e(String.format("characteristic not indicatable!"));
//            return false;
//        }

        if (mBluetoothGatt == null) {
            BluetoothLog.e(String.format("ble gatt null"));
            return false;
        }

        if (!mBluetoothGatt.setCharacteristicNotification(characteristic, enable)) {
            BluetoothLog.e(String.format("setCharacteristicIndication failed"));
            return false;
        }

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(Constants.CLIENT_CHARACTERISTIC_CONFIG);

        if (descriptor == null) {
            BluetoothLog.e(String.format("getDescriptor for indicate null!"));
            return false;
        }

        byte[] value = (enable ? BluetoothGattDescriptor.ENABLE_INDICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);

        if (!descriptor.setValue(value)) {
            BluetoothLog.e(String.format("setValue for indicate descriptor failed!"));
            return false;
        }

        if (!mBluetoothGatt.writeDescriptor(descriptor)) {
            BluetoothLog.e(String.format("writeDescriptor for indicate failed"));
            return false;
        }

        return true;
    }

    @Override
    public boolean readRemoteRssi() {
        checkRuntime();

        BluetoothLog.v(String.format("readRemoteRssi for %s", getAddress()));

        if (mBluetoothGatt == null) {
            BluetoothLog.e(String.format("ble gatt null"));
            return false;
        }

        if (!mBluetoothGatt.readRemoteRssi()) {
            BluetoothLog.e(String.format("readRemoteRssi failed"));
            return false;
        }

        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean requestMtu(int mtu) {
        checkRuntime();

        BluetoothLog.v(String.format("requestMtu for %s, mtu = %d", getAddress(), mtu));

        if (mBluetoothGatt == null) {
            BluetoothLog.e(String.format("ble gatt null"));
            return false;
        }

        if (!mBluetoothGatt.requestMtu(mtu)) {
            BluetoothLog.e(String.format("requestMtu failed"));
            return false;
        }
        return true;
    }

    @Override
    public BleGattProfile getGattProfile() {
        return mBleGattProfile;
    }

    private boolean isCharacteristicReadable(BluetoothGattCharacteristic characteristic) {
        return characteristic != null && (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) != 0;
    }

    private boolean isCharacteristicWritable(BluetoothGattCharacteristic characteristic) {
        return characteristic != null && (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) != 0;
    }

    private boolean isCharacteristicNoRspWritable(BluetoothGattCharacteristic characteristic) {
        return characteristic != null && (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0;
    }

    private boolean isCharacteristicNotifyable(BluetoothGattCharacteristic characteristic) {
        return characteristic != null && (characteristic.getProperties()
                & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0;
    }

    private boolean isCharacteristicIndicatable(BluetoothGattCharacteristic characteristic) {
        return characteristic != null && (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GATT_RESPONSE:
                ProxyBulk.safeInvoke(msg.obj);
                break;
        }
        return true;
    }

    @Override
    public boolean onIntercept(Object object, Method method, Object[] args) {
        mWorkerHandler.obtainMessage(MSG_GATT_RESPONSE,
                new ProxyBulk(object, method, args)).sendToTarget();
        return true;
    }

    @Override
    public void checkRuntime() {
        mRuntimeChecker.checkRuntime();
    }
}
