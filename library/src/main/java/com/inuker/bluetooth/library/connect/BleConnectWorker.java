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
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.inuker.bluetooth.library.BluetoothConstants;
import com.inuker.bluetooth.library.BluetoothService;
import com.inuker.bluetooth.library.Code;
import com.inuker.bluetooth.library.connect.request.BleConnectRequest;
import com.inuker.bluetooth.library.connect.request.BleDisconnectRequest;
import com.inuker.bluetooth.library.connect.request.BleNotifyRequest;
import com.inuker.bluetooth.library.connect.request.BleReadRequest;
import com.inuker.bluetooth.library.connect.request.BleReadRssiRequest;
import com.inuker.bluetooth.library.connect.request.BleRequest;
import com.inuker.bluetooth.library.connect.request.BleUnnotifyRequest;
import com.inuker.bluetooth.library.connect.request.BleWriteRequest;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.inuker.bluetooth.library.utils.ByteUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


/**
 * Created by dingjikerbo on 16/4/8.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleConnectWorker {

    private static final int MSG_REQUEST_TIMEOUT = 0x120;
    public static final int MSG_SCHEDULE_NEXT = 0x160;

    /**
     * 这些消息是蓝牙回调中发的，要保证回调都在工作线程里处理
     */
    private static final int MSG_CONNECT_CHANGE = 0x11;
    private static final int MSG_SERVICE_DISCOVER = 0x21;
    private static final int MSG_CHARACTER_READ = 0x41;
    private static final int MSG_CHARACTER_WRITE = 0x51;
    private static final int MSG_CHARACTER_CHANGE = 0x61;
    private static final int MSG_DESCRIPTOR_WRITE = 0x71;
    private static final int MSG_READ_RSSI = 0x81;

    private static final int STATUS_DEVICE_CONNECTED = BluetoothProfile.STATE_CONNECTED;
    private static final int STATUS_DEVICE_CONNECTING = BluetoothProfile.STATE_CONNECTING;
    private static final int STATUS_DEVICE_DISCONNECTING = BluetoothProfile.STATE_DISCONNECTING;
    private static final int STATUS_DEVICE_DISCONNECTED = BluetoothProfile.STATE_DISCONNECTED;
    private static final int STATUS_DEVICE_SERVICE_READY = 0x13;

    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice mBluetoothDevice;

    private IBleDispatch mBleDispatcher;

    private BleRequest mCurrentRequest;

    private Handler mWorkerHandler;

    private volatile int mConnectStatus;

    private Map<UUID, Map<UUID, BluetoothGattCharacteristic>> mDeviceProfile;

    public static BleConnectWorker attch(String mac, IBleDispatch dispatcher) {
        return new BleConnectWorker(mac, dispatcher);
    }

    private BleConnectWorker(String mac, IBleDispatch dispatcher) {
        mBleDispatcher = dispatcher;

        BluetoothAdapter adapter = BluetoothUtils.getBluetoothLeAdapter();
        if (adapter != null) {
            mBluetoothDevice = adapter.getRemoteDevice(mac);
        } else {
            throw new IllegalStateException(
                    "ble adapter null");
        }

        mDeviceProfile = new HashMap<UUID, Map<UUID, BluetoothGattCharacteristic>>();

        mWorkerHandler = new Handler(Looper.myLooper()) {

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                try {
                    processWorkerMessage(msg);
                } catch (Throwable e) {
                    BluetoothLog.e(e);

                    /**
                     * 如果工作线程中有异常则结束当前任务并开始下一个任务
                     */
                    dispatchRequestResult(false);
                }
            }

        };

        mBleDispatcher.notifyHandlerReady(mWorkerHandler);
    }

    private void processWorkerMessage(Message msg) {
//      BluetoothLog.v("BleConnectWorker processWorkerMessage " + getMessage(msg.what));

        switch (msg.what) {
            case MSG_SCHEDULE_NEXT:
                onScheduleNext((BleRequest) msg.obj);
                break;

            case MSG_REQUEST_TIMEOUT:
                onRequestTimeout();
                break;

            case MSG_CONNECT_CHANGE:
                onConnectionStateChange(msg.arg1, msg.arg2);
                break;

            case MSG_SERVICE_DISCOVER:
                onServicesDiscovered(msg.arg1);
                break;

            case MSG_CHARACTER_READ:
                onCharacteristicRead(msg.arg1, (BluetoothGattCharacteristic) msg.obj);
                break;

            case MSG_CHARACTER_WRITE:
                onCharacteristicWrite(msg.arg1, (BluetoothGattCharacteristic) msg.obj);
                break;

            case MSG_CHARACTER_CHANGE:
                Bundle data = msg.getData();
                byte[] value = (data != null ? data.getByteArray(BluetoothConstants.EXTRA_BYTE_VALUE) : null);
                onCharacteristicChanged((BluetoothGattCharacteristic) msg.obj, value);
                break;

            case MSG_DESCRIPTOR_WRITE:
                onDescriptorWrite(msg.arg1, (BluetoothGattDescriptor) msg.obj);
                break;

            case MSG_READ_RSSI:
                onReadRemoteRssi(msg.arg1, msg.arg2);
                break;
        }
    }

    private String getMessage(int msg) {
        switch (msg) {
            case MSG_SCHEDULE_NEXT: return "MSG_SCHEDULE_NEXT";
            case MSG_REQUEST_TIMEOUT: return "MSG_REQUEST_TIMEOUT";
            case MSG_CONNECT_CHANGE: return "MSG_CONNECT_CHANGE";
            case MSG_SERVICE_DISCOVER: return "MSG_SERVICE_DISCOVER";
            case MSG_CHARACTER_READ: return "MSG_CHARACTER_READ";
            case MSG_CHARACTER_WRITE: return "MSG_CHARACTER_WRITE";
            case MSG_CHARACTER_CHANGE: return "MSG_CHARACTER_CHANGE";
            case MSG_DESCRIPTOR_WRITE: return "MSG_DESCRIPTOR_WRITE";
            case MSG_READ_RSSI: return "MSG_READ_RSSI";
            default: return "unknown message";
        }
    }

    private void refreshServiceProfile() {
        List<BluetoothGattService> services = mBluetoothGatt.getServices();

        Map<UUID, Map<UUID, BluetoothGattCharacteristic>> newProfiles = new HashMap<UUID, Map<UUID, BluetoothGattCharacteristic>>();

        for (BluetoothGattService service : services) {

            BluetoothLog.v("Service: " + service.getUuid());

            Map<UUID, BluetoothGattCharacteristic> map = new HashMap<UUID, BluetoothGattCharacteristic>();
            newProfiles.put(service.getUuid(), map);

            List<BluetoothGattCharacteristic> characters = service
                    .getCharacteristics();

            for (BluetoothGattCharacteristic character : characters) {
                BluetoothLog.v("character: uuid = "
                        + character.getUuid());

                map.put(character.getUuid(), character);
            }
        }

        mDeviceProfile.clear();
        mDeviceProfile.putAll(newProfiles);
    }

    private BluetoothGattCharacteristic getCharacter(UUID serviceId,
                                                     UUID characterId) {
        BluetoothGattCharacteristic character = null;

        if (serviceId != null && characterId != null) {
            Map<UUID, BluetoothGattCharacteristic> characters = mDeviceProfile
                    .get(serviceId);
            if (characters != null) {
                character = characters.get(characterId);
            } else {
                BluetoothLog.e(String.format(
                        "getCharacter: service %s not exist", serviceId));
            }
        }

        if (character == null) {
            BluetoothLog.e("getCharacter: return null");
        }

        return character;
    }

    private BluetoothGattCharacteristic getCharacter(BleRequest request) {
        return getCharacter(request.getServiceUUID(),
                request.getCharacterUUID());
    }

    /**
     * 处理连接请求
     */
    private void processConnectRequest(BleConnectRequest request) {
        switch (mConnectStatus) {
            case STATUS_DEVICE_CONNECTED:
                throw new IllegalStateException("status impossible");

            case STATUS_DEVICE_SERVICE_READY:
                setConnectRequestExtra(request);
                dispatchRequestResult(true);
                break;

            default:
                if (mBluetoothGatt == null) {
                    mBluetoothGatt = openNewBluetoothGatt();
                } else {
                    throw new IllegalStateException("status impossible");
                }
        }
    }

    private boolean isServiceReady() {
        return mConnectStatus == STATUS_DEVICE_SERVICE_READY;
    }

    /**
     * 处理请求，根据请求类型进行分发
     *
     * @param request
     */
    private void processRequest(BleRequest request) {
        BluetoothLog.v(String.format(
                "processRequest %s >>> current status = %s",
                request.toString(), getConnectStatusText(mConnectStatus)));

        mCurrentRequest = request;

        startRequestTiming();

        switch (request.getRequestType()) {
            case BleRequest.REQUEST_TYPE_CONNECT:
                processConnectRequest((BleConnectRequest) request);
                break;
            case BleRequest.REQUEST_TYPE_READ:
                processReadRequest((BleReadRequest) request);
                break;
            case BleRequest.REQUEST_TYPE_WRITE:
                processWriteRequest((BleWriteRequest) request);
                break;
            case BleRequest.REQUEST_TYPE_DISCONNECT:
                processDisconnectRequest((BleDisconnectRequest) request);
                break;
            case BleRequest.REQUEST_TYPE_NOTIFY:
                processNotifyRequest((BleNotifyRequest) request);
                break;
            case BleRequest.REQUEST_TYPE_UNNOTIFY:
                processUnnotifyRequest((BleUnnotifyRequest) request);
                break;
            case BleRequest.REQUEST_TYPE_READ_RSSI:
                processReadRssiRequest((BleReadRssiRequest) request);
                break;
            default:
                throw new IllegalArgumentException("unknown request type");
        }
    }

    private void processReadRequest(BleReadRequest request) {
        BluetoothLog.d(String.format(
                "processReadRequest: service = %s, character = %s",
                request.getServiceUUID(), request.getCharacterUUID()));

        if (!isServiceReady()) {
            dispatchRequestResult(false);
        } else {
            BluetoothGattCharacteristic character = getCharacter(request);

            if (character != null) {
                if (!mBluetoothGatt.readCharacteristic(character)) {
                    BluetoothLog.w("readCharacteristic return false");
                    onGattFailed();
                }
            } else {
                BluetoothLog.e("character not found");
                dispatchRequestResult(false);
            }
        }
    }

    private void processWriteRequest(BleWriteRequest request) {
        BluetoothLog.d(String.format("processWriteRequest: service = %s, character = %s, value = 0x%s",
                request.getServiceUUID(),
                request.getCharacterUUID(),
                ByteUtils.byteToString(request.getBytes())));

        if (!isServiceReady()) {
            dispatchRequestResult(false);
        } else {
            BluetoothGattCharacteristic character = getCharacter(request);
            if (character != null) {
                if (request.getBytes() != null) {
                    character.setValue(request.getBytes());
                } else {
                    throw new IllegalArgumentException("bytes to write null");
                }

                if (!mBluetoothGatt.writeCharacteristic(character)) {
                    BluetoothLog.w("writeCharacteristic return false");
                    onGattFailed();
                }
            } else {
                dispatchRequestResult(false);
            }
        }
    }

    /**
     * 处理断开连接请求
     *
     * @param request
     */
    private void processDisconnectRequest(BleDisconnectRequest request) {
        closeBluetoothGatt();
        dispatchRequestResult(true);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private boolean setCharacteristicNotification(BluetoothGatt gatt,
                                                  BluetoothGattCharacteristic characteristic, boolean flag) {
        boolean result = gatt.setCharacteristicNotification(characteristic,
                flag);

        if (result) {
            BluetoothGattDescriptor descriptor = characteristic
                    .getDescriptor(UUID
                            .fromString(BluetoothConstants.CLIENT_CHARACTERISTIC_CONFIG));

            if (descriptor != null) {
                descriptor
                        .setValue(flag ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                                : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            }

            result = mBluetoothGatt.writeDescriptor(descriptor);
        } else {
            BluetoothLog.w("setCharacteristicNotification failed");
        }

        return result;
    }

    private void processNotifyRequest(BleNotifyRequest request) {
        BluetoothLog.d(String.format(
                "processNotifyRequest: service = %s, character = %s",
                request.getServiceUUID(), request.getCharacterUUID()));

        if (!isServiceReady()) {
            BluetoothLog.w(String.format("connect status invalid: %d", mConnectStatus));
            dispatchRequestResult(false);
        } else {
            BluetoothGattCharacteristic character = getCharacter(request);

            if (character != null) {
                if (!setCharacteristicNotification(mBluetoothGatt, character, true)) {
                    BluetoothLog.w("setCharacteristicNotification return false");
                    onGattFailed();
                }
            } else {
                BluetoothLog.e("character not found");
                dispatchRequestResult(false);
            }
        }
    }

    private void processUnnotifyRequest(BleUnnotifyRequest request) {
        BluetoothLog.d(String.format(
                "processUnnotifyRequest: service = %s, character = %s",
                request.getServiceUUID(), request.getCharacterUUID()));

        if (!isServiceReady()) {
            dispatchRequestResult(false);
        } else {
            BluetoothGattCharacteristic character = getCharacter(request);

            if (character != null) {
                if (!setCharacteristicNotification(mBluetoothGatt, character,
                        false)) {
                    BluetoothLog
                            .w("setCharacteristicNotification return false");
                    onGattFailed();
                }
            } else {
                BluetoothLog.e("character not found");
                dispatchRequestResult(false);
            }
        }
    }

    private void processReadRssiRequest(BleReadRssiRequest request) {
        BluetoothLog.d("processReadRssiRequest");

        if (!isServiceReady()) {
            dispatchRequestResult(false);
        } else {
            if (!mBluetoothGatt.readRemoteRssi()) {
                BluetoothLog.w("readRemoteRssi return false");
                onGattFailed();
            }
        }
    }

    /**
     * 任务开始计时，每个任务有超时取消。防止有些任务失败后收不到任何回调， 导致队列后的任务全部阻塞
     */
    private void startRequestTiming() {
        mWorkerHandler.sendEmptyMessageDelayed(MSG_REQUEST_TIMEOUT,
                mCurrentRequest.getTimeoutLimit());
    }

    /**
     * 当任务收到回调后，不管成功还是失败，都会停止计时
     */
    private void stopRequestTiming() {
        mWorkerHandler.removeMessages(MSG_REQUEST_TIMEOUT);
    }

    /**
     * 当worker在任何环节出现任何异常，都会调用本函数通知dispatcher
     * @param result
     */
    private void dispatchRequestResult(boolean result) {
//        BluetoothLog.v("dispatchRequestResult " + result + "\n");

        stopRequestTiming();

        BleRequest request = mCurrentRequest;

        mCurrentRequest = null;

        mBleDispatcher.notifyWorkerResult(request, result);
    }

    private void closeBluetoothGatt() {
        if (mBluetoothGatt != null) {
            BluetoothLog.d("closeBluetoothGatt");

            mBluetoothGatt.close();
            mBluetoothGatt = null;

            mDeviceProfile.clear();

            /**
             * 假如建立连接后discoverService等待回调时超时了，则状态是connected，
             * 但是会closeGatt，所以这里应该重置状态
             */
            setConnectStatus(STATUS_DEVICE_DISCONNECTED);
            broadcastConnectStatus(BluetoothConstants.STATUS_DISCONNECTED);
        }
    }

    private BluetoothGatt openNewBluetoothGatt() {
        BluetoothLog.d("openNewBluetoothGatt");

        closeBluetoothGatt();

        if (mBluetoothGatt == null) {
            mBluetoothGatt = mBluetoothDevice.connectGatt(
                    BluetoothService.getContext(), false, mConnectCallback);
            refreshDeviceCache(mBluetoothGatt);
        }

        return mBluetoothGatt;
    }

    private boolean refreshDeviceCache(BluetoothGatt gatt){
        try {
            if (gatt != null) {
                Method refresh = BluetoothGatt.class.getMethod("refresh");
                if (refresh != null) {
                    refresh.setAccessible(true);
                    return (boolean) refresh.invoke(gatt, new Object[0]);
                }
            }
        } catch (Exception e) {
            BluetoothLog.e(e);
        }
        return false;
    }

    private void setConnectStatus(int status) {
//        BluetoothLog.d(String.format("setConnectStatus %s for %s",
//                TestUtils.getStatus(status), mBluetoothDevice.getAddress()));
        mConnectStatus = status;
    }

    private void setConnectRequestUuidExtra(BleRequest request) {
        if (mDeviceProfile != null && request != null) {
            Set<UUID> set = mDeviceProfile.keySet();
            if (set != null) {
                ArrayList<UUID> uuids = new ArrayList<UUID>(set);
                request.putSerializableExtra(BluetoothConstants.EXTRA_SERVICE_UUID, uuids);
            }
        }
    }

    private void setConnectRequestExtra(BleRequest request) {
        if (request != null && request.isConnectRequest()) {
            setConnectRequestUuidExtra(request);
        }
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

    /**
     * 可能任务出错了，或者连接中断了
     * 都要关掉gatt并启动下一个任务
     */
    private void onGattFailed() {
        closeBluetoothGatt();
        dispatchRequestResult(false);
    }

    private void onRequestTimeout() {
        closeBluetoothGatt();

        if (mCurrentRequest != null) {
            mCurrentRequest.setRequestCode(Code.REQUEST_TIMEDOUT);
            dispatchRequestResult(false);
        } else {
            throw new IllegalStateException("timeout but no request ongoing");
        }
    }

    private void onConnectionStateChange(int status, int newState) {
        if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
            setConnectStatus(STATUS_DEVICE_CONNECTED);
            BluetoothLog.v(String.format("discoverServices"));
            mBluetoothGatt.discoverServices();
        } else {
            if (mCurrentRequest != null && mCurrentRequest.isConnectRequest()) {
                mCurrentRequest.putIntExtra(BluetoothConstants.EXTRA_STATUS, status);
                mCurrentRequest.putIntExtra(BluetoothConstants.EXTRA_STATE, newState);
            }
            onGattFailed();
        }
    }

    private void onServicesDiscovered(int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            setConnectStatus(STATUS_DEVICE_SERVICE_READY);
            broadcastConnectStatus(BluetoothConstants.STATUS_CONNECTED);

            refreshServiceProfile();

            if (mCurrentRequest != null && mCurrentRequest.isConnectRequest()) {
                setConnectRequestExtra(mCurrentRequest);
                dispatchRequestResult(true);
            } else {
                throw new IllegalStateException("onServiceDiscover but not connect request");
            }
        } else {
            onGattFailed();
        }
    }

    private void onCharacteristicRead(int status, BluetoothGattCharacteristic characteristic) {
        byte[] value = ByteUtils.getNonEmptyByte(characteristic.getValue());

        if (mCurrentRequest == null || !mCurrentRequest.isReadRequest()) {
            BluetoothLog.w("onCharacteristicRead: current not read request");
            return;
        }

        if (status == BluetoothGatt.GATT_SUCCESS) {
            mCurrentRequest.putByteArrayExtra(BluetoothConstants.EXTRA_BYTE_VALUE, value);
            dispatchRequestResult(true);
        } else if (isGattErrorOrFailure(status)) {
            onGattFailed();
        } else {
            dispatchRequestResult(false);
        }
    }

    private void onCharacteristicWrite(int status, BluetoothGattCharacteristic characteristic) {
        if (mCurrentRequest == null || !mCurrentRequest.isWriteRequest()) {
            BluetoothLog.w("onCharacteristicWrite: current not write request");
            return;
        }

        if (status == BluetoothGatt.GATT_SUCCESS) {
            mCurrentRequest.putByteArrayExtra(BluetoothConstants.EXTRA_BYTE_VALUE, characteristic.getValue());
            dispatchRequestResult(true);
        } else if (isGattErrorOrFailure(status)) {
            onGattFailed();
        } else {
            dispatchRequestResult(false);
        }
    }

    private void onCharacteristicChanged(BluetoothGattCharacteristic characteristic, byte[] value) {
        value = ByteUtils.getNonEmptyByte(value);

        BluetoothGattService service = characteristic.getService();

        if (service != null) {
            broadcastCharacterChanged(service.getUuid(), characteristic.getUuid(), value);
        }
    }

    private void onDescriptorWrite(int status, BluetoothGattDescriptor descriptor) {
        BluetoothGattCharacteristic descChar = descriptor.getCharacteristic();

        if (mCurrentRequest == null ||
                (!mCurrentRequest.isNotifyRequest() && !mCurrentRequest.isUnnotifyRequest())) {
            BluetoothLog.w("onDescriptorWrite: current not notify/unnotify request");
            return;
        }

        if (status == BluetoothGatt.GATT_SUCCESS) {
            if (getCharacter(mCurrentRequest) == descChar) {
                dispatchRequestResult(true);
            }
        } else if (isGattErrorOrFailure(status)) {
            onGattFailed();
        } else {
            dispatchRequestResult(false);
        }
    }

    private void onReadRemoteRssi(int status, int rssi) {
        if (mCurrentRequest == null || !mCurrentRequest.isReadRssiRequest()) {
            BluetoothLog.w("onReadRemoteRssi: current not readRssi request");
            return;
        }

        if (status == BluetoothGatt.GATT_SUCCESS) {
            mCurrentRequest.putIntExtra(BluetoothConstants.EXTRA_RSSI, rssi);
            dispatchRequestResult(true);
        } else {
            dispatchRequestResult(false);
        }
    }

    private final BluetoothGattCallback mConnectCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            // TODO Auto-generated method stub
            BluetoothLog.d(String.format(
                    "onConnectionStateChange: status = %d, newState = %d",
                    status, newState));

            sendWorkerMessage(MSG_CONNECT_CHANGE, status, newState, null);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            // TODO Auto-generated method stub
            BluetoothLog.d("onServicesDiscovered " + status);

            if (!isServiceReady() && !mWorkerHandler.hasMessages(MSG_SERVICE_DISCOVER)) {
                sendWorkerMessage(MSG_SERVICE_DISCOVER, status, 0, null);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            // TODO Auto-generated method stub
            BluetoothLog.d(String.format(
                    "onCharacteristicRead %s\n>>> status = %d, value = %s",
                    characteristic.getUuid(), status,
                    ByteUtils.byteToString(characteristic.getValue())));

            sendWorkerMessage(MSG_CHARACTER_READ, status, 0, characteristic);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          final BluetoothGattCharacteristic characteristic, final int status) {
            // TODO Auto-generated method stub
            BluetoothLog.d(String.format(
                    "onCharacteristicWrite %s, status = %d",
                    characteristic.getUuid(), status));

            sendWorkerMessage(MSG_CHARACTER_WRITE, status, 0, characteristic);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            // TODO Auto-generated method stub
            BluetoothLog.v(String.format("onCharacteristicChanged: \n>>> uuid = %s\n>>> value = (%s)",
                    characteristic.getUuid(),
                    ByteUtils.byteToString(characteristic.getValue())));

            Bundle data = new Bundle();
            data.putByteArray(BluetoothConstants.EXTRA_BYTE_VALUE, characteristic.getValue());
            sendWorkerMessage(MSG_CHARACTER_CHANGE, 0, 0, characteristic, data);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor descriptor, int status) {
            // TODO Auto-generated method stub
            BluetoothLog.v(String.format("onDescriptorWrite status = %d", status));
            sendWorkerMessage(MSG_DESCRIPTOR_WRITE, status, 0, descriptor);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            BluetoothLog.v(String.format("onReadRemoteRssi rssi = %d, status = %d", rssi, status));
            sendWorkerMessage(MSG_READ_RSSI, status, rssi, null);
        }
    };

    private void sendWorkerMessage(int what, int arg1, int arg2, Object obj) {
        mWorkerHandler.obtainMessage(what, arg1, arg2, obj).sendToTarget();
    }

    private void sendWorkerMessage(int what, int arg1, int arg2, Object obj, Bundle data) {
        Message msg = mWorkerHandler.obtainMessage(what, arg1, arg2, obj);
        msg.setData(data);
        msg.sendToTarget();
    }

    private boolean isGattErrorOrFailure(int status) {
        return status == BluetoothGatt.GATT_FAILURE || status == BluetoothConstants.GATT_ERROR;
    }

    private void broadcastConnectStatus(int status) {
        Intent intent = new Intent(
                BluetoothConstants.ACTION_CONNECT_STATUS_CHANGED);
        intent.putExtra(BluetoothConstants.EXTRA_MAC,
                mBluetoothDevice.getAddress());
        intent.putExtra(BluetoothConstants.EXTRA_STATUS, status);
        BluetoothUtils.sendBroadcast(intent);
    }

    private void broadcastCharacterChanged(UUID service, UUID character,
                                           byte[] value) {
        Intent intent = new Intent(
                BluetoothConstants.ACTION_CHARACTER_CHANGED);
        intent.putExtra(BluetoothConstants.EXTRA_MAC,
                mBluetoothDevice.getAddress());
        intent.putExtra(BluetoothConstants.EXTRA_SERVICE_UUID, service);
        intent.putExtra(BluetoothConstants.EXTRA_CHARACTER_UUID, character);
        intent.putExtra(BluetoothConstants.EXTRA_BYTE_VALUE, value);
        BluetoothUtils.sendBroadcast(intent);
    }

    private String getConnectStatusText(int status) {
        switch (status) {
            case STATUS_DEVICE_SERVICE_READY:
                return "STATUS_DEVICE_SERVICE_READY";
            case STATUS_DEVICE_CONNECTED:
                return "STATUS_DEVICE_CONNECTED";
            case STATUS_DEVICE_DISCONNECTED:
                return "STATUS_DEVICE_DISCONNECTED";
            default:
                return "unknown";
        }
    }
}
