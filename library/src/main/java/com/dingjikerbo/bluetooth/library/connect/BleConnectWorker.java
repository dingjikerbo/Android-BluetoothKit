package com.dingjikerbo.bluetooth.library.connect;

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
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;

import com.dingjikerbo.bluetooth.library.BluetoothManager;
import com.dingjikerbo.bluetooth.library.WarningException;
import com.dingjikerbo.bluetooth.library.connect.request.BleConnectRequest;
import com.dingjikerbo.bluetooth.library.connect.request.BleDisconnectRequest;
import com.dingjikerbo.bluetooth.library.connect.request.BleNotifyRequest;
import com.dingjikerbo.bluetooth.library.connect.request.BleReadRequest;
import com.dingjikerbo.bluetooth.library.connect.request.BleReadRssiRequest;
import com.dingjikerbo.bluetooth.library.connect.request.BleRequest;
import com.dingjikerbo.bluetooth.library.connect.request.BleUnnotifyRequest;
import com.dingjikerbo.bluetooth.library.connect.request.BleWriteRequest;
import com.dingjikerbo.bluetooth.library.connect.request.Code;
import com.dingjikerbo.bluetooth.library.connect.request.IBleDispatch;
import com.dingjikerbo.bluetooth.library.connect.request.IBleRunner;
import com.dingjikerbo.bluetooth.library.utils.BluetoothConstants;
import com.dingjikerbo.bluetooth.library.utils.BluetoothLog;
import com.dingjikerbo.bluetooth.library.utils.BluetoothUtils;
import com.dingjikerbo.bluetooth.library.utils.ByteUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


/**
 * 直接面向master，所以所有操作处于单线程中，不涉及同步的问题
 * 这是个基础层，不涉及任务出错重试等容错机制，容错可在上层做
 * 本层只做简单的任务超时机制，超时则认为任务失败
 *
 * @author liwentian
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleConnectWorker {

    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice mBluetoothDevice;

    private IBleDispatch mBleDispatcher;

    private BleRequest mCurrentRequest;

    private Handler mWorkerHandler;

    private int mConnectStatus;

    private Map<UUID, Map<UUID, BluetoothGattCharacteristic>> mDeviceProfile;

    private BleConnectWorker(String mac, IBleRunner runner,
                             IBleDispatch dispatcher) {
        mBleDispatcher = dispatcher;

        BluetoothAdapter adapter = BluetoothUtils.getBluetoothLeAdapter();
        if (adapter != null) {
            mBluetoothDevice = adapter.getRemoteDevice(mac);
        } else {
            throw new IllegalStateException(
                    "ble adapter null");
        }

        mDeviceProfile = new HashMap<UUID, Map<UUID, BluetoothGattCharacteristic>>();

        mWorkerHandler = new Handler(runner.getLooper()) {

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                try {
                    processWorkerMessage(msg);
                } catch (WarningException e) {
                    BluetoothLog.w(e);
                } catch (Throwable e) {
                    BluetoothLog.e(e);
                    dispatchRequestResult(BluetoothConstants.FAILED);
                }
            }

        };

        mBleDispatcher.notifyHandlerReady(mWorkerHandler);
    }

    public static BleConnectWorker attch(String mac, IBleRunner runner,
                                         IBleDispatch dispatcher) {
        return new BleConnectWorker(mac, runner, dispatcher);
    }

    private void processWorkerMessage(Message msg) {
        switch (msg.what) {
            /**
             * 处理上层派发下来的新任务
             */
            case BluetoothConstants.MSG_SCHEDULE_NEXT:
                if (mCurrentRequest != null) {
                    throw new IllegalStateException("previous request ongoing");
                } else {
                    processRequest((BleRequest) msg.obj);
                }

                break;

            case BluetoothConstants.MSG_GATT_FAILED:
                /**
                 * 蓝牙出错
                 */
                closeBluetoothGatt();

                if (mCurrentRequest != null) {
                    dispatchRequestResult(BluetoothConstants.FAILED);
                } else {
                    /**
                     * 当前没有任何请求，然而设备主动断开连接了
                     */
                }

                break;

            case BluetoothConstants.MSG_REQUEST_TIMEOUT:

                /**
                 * 如果任务超时说明回调一直没调到，会认为连接出问题了，干脆关掉gatt
                 */
                closeBluetoothGatt();

                if (mCurrentRequest != null) {
                    mCurrentRequest.setRequestCode(Code.REQUEST_TIMEDOUT);
                    dispatchRequestResult(BluetoothConstants.FAILED);
                } else {
                    throw new IllegalStateException("failed or timeout but no request ongoing");
                }

                break;

            case BluetoothConstants.MSG_SERVICE_READY:
                if (mCurrentRequest != null) {
                    if (mCurrentRequest.isConnectRequest()) {
                        dispatchRequestResult(BluetoothConstants.SUCCESS);
                    } else {
                        /**
                         * 如果当前是其它请求，而连接中途断掉了又重连了就可能走到这，虽然概率极低，这什么也不做
                         */
                        throw new WarningException("service ready but not connect request ongoing");
                    }
                } else {
                    /*
                     * 有可能，比如没有任何操作了，但是连接一直在，偶尔会断开，偶尔又重新连上了，这里抛出异常催促上层派发下一个任务
                     */
                    throw new IllegalStateException("service ready but no request ongoing");
                }

                break;

            case BluetoothConstants.MSG_DISCONNECTED:
                closeBluetoothGatt();

                break;
        }
    }

    private void processServiceReady() {
        BluetoothLog.v("processServiceReady");

        setConnectStatus(BluetoothConstants.STATUS_DEVICE_SERVICE_READY);
        broadcastConnectStatus(BluetoothConstants.STATUS_CONNECTED);

        refreshServiceProfile();

        if (mCurrentRequest != null && mCurrentRequest.isConnectRequest()) {
            Set<UUID> set = mDeviceProfile.keySet();
            if (set != null) {
                ArrayList<UUID> uuids = new ArrayList<UUID>(set);
                mCurrentRequest.putExtra(BluetoothConstants.KEY_UUIDS, uuids);
            }

        } else {
            BluetoothLog.w("current not connect request");
        }

        mWorkerHandler.obtainMessage(BluetoothConstants.MSG_SERVICE_READY).sendToTarget();
    }

    private void processDisconnected() {
        BluetoothLog.v("processDisconnected");
        mWorkerHandler.obtainMessage(BluetoothConstants.MSG_DISCONNECTED).sendToTarget();
    }

    private void refreshServiceProfile() {
        BluetoothLog.v("refreshDeviceProfile");

        if (!isServiceReady() || isServiceProfileReady()) {
            return;
        }

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

    private boolean isServiceProfileReady() {
        return !mDeviceProfile.isEmpty();
    }

    private BluetoothGattCharacteristic getCharacter(UUID serviceId,
                                                     UUID characterId) {
        BluetoothLog.v(String.format(
                "getCharacter for service(%s), character(%s)", serviceId,
                characterId));

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
            case BluetoothConstants.STATUS_DEVICE_CONNECTED:
                throw new IllegalStateException("status impossible");

            case BluetoothConstants.STATUS_DEVICE_SERVICE_READY:
                dispatchRequestResult(BluetoothConstants.SUCCESS);
                break;

            default:
                if (mBluetoothGatt == null) {
                    mBluetoothGatt = openNewBluetoothGatt();
                } else {
                    reconnectGatt();
                }
        }
    }

    /**
     * 重新连接
     */
    private void reconnectGatt() {
        BluetoothLog.d("reconnectGatt");

        if (!mBluetoothGatt.connect()) {
            BluetoothLog.w("reconnectGatt failed");
            mBluetoothGatt = openNewBluetoothGatt();
        }
    }

    private boolean isServiceReady() {
        return mConnectStatus == BluetoothConstants.STATUS_DEVICE_SERVICE_READY;
    }

    private void processReadRequest(BleReadRequest request) {
        BluetoothLog.d(String.format(
                "processReadRequest: service = %s, character = %s",
                request.getServiceUUID(), request.getCharacterUUID()));

        if (!isServiceReady()) {
            dispatchRequestResult(BluetoothConstants.FAILED);
        } else {
            BluetoothGattCharacteristic character = getCharacter(request);

            if (character != null) {
                if (!mBluetoothGatt.readCharacteristic(character)) {
                    BluetoothLog.w("readCharacteristic return false");
                    processGattFailed();
                }
            } else {
                BluetoothLog.e("character not found");
                dispatchRequestResult(BluetoothConstants.FAILED);
            }
        }
    }

    private void processWriteRequest(BleWriteRequest request) {
        BluetoothLog
                .d(String
                        .format("processWriteRequest: service = %s, character = %s, value = 0x%s",
                                request.getServiceUUID(),
                                request.getCharacterUUID(),
                                ByteUtils.byteToString(request.getBytes())));

        if (!isServiceReady()) {
            dispatchRequestResult(BluetoothConstants.FAILED);
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
                    processGattFailed();
                }
            } else {
                BluetoothLog.e("character not found");
                dispatchRequestResult(BluetoothConstants.FAILED);
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
        dispatchRequestResult(BluetoothConstants.SUCCESS);
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

            descriptor
                    .setValue(flag ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                            : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);

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
            dispatchRequestResult(BluetoothConstants.FAILED);
        } else {
            BluetoothGattCharacteristic character = getCharacter(request);

            if (character != null) {
                if (!setCharacteristicNotification(mBluetoothGatt, character, true)) {
                    BluetoothLog.w("setCharacteristicNotification return false");
                    processGattFailed();
                }
            } else {
                BluetoothLog.e("character not found");
                dispatchRequestResult(BluetoothConstants.FAILED);
            }
        }
    }

    private void processUnnotifyRequest(BleUnnotifyRequest request) {
        BluetoothLog.d(String.format(
                "processUnnotifyRequest: service = %s, character = %s",
                request.getServiceUUID(), request.getCharacterUUID()));

        if (!isServiceReady()) {
            dispatchRequestResult(BluetoothConstants.FAILED);
        } else {
            BluetoothGattCharacteristic character = getCharacter(request);

            if (character != null) {
                if (!setCharacteristicNotification(mBluetoothGatt, character,
                        false)) {
                    BluetoothLog
                            .w("setCharacteristicNotification return false");
                    processGattFailed();
                }
            } else {
                BluetoothLog.e("character not found");
                dispatchRequestResult(BluetoothConstants.FAILED);
            }
        }
    }

    private void processReadRssiRequest(BleReadRssiRequest request) {
        BluetoothLog.d("processReadRssiRequest");

        if (!isServiceReady()) {
            dispatchRequestResult(BluetoothConstants.FAILED);
        } else {
            if (!mBluetoothGatt.readRemoteRssi()) {
                BluetoothLog.w("readRemoteRssi return false");
                processGattFailed();
            }
        }
    }

    /**
     * 处理请求，根据请求类型进行分发
     *
     * @param request
     */
    private void processRequest(BleRequest request) {
        mCurrentRequest = request;

        startRequestTiming();

        doProcessRequest(request);
    }

    private void doProcessRequest(BleRequest request) {
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

    /**
     * 任务开始计时，每个任务有超时取消。防止有些任务失败后收不到任何回调， 导致队列后的任务全部阻塞
     */
    private void startRequestTiming() {
        mWorkerHandler.sendEmptyMessageDelayed(BluetoothConstants.MSG_REQUEST_TIMEOUT,
                mCurrentRequest.getTimeoutLimit());
    }

    /**
     * 当任务收到回调后，不管成功还是失败，都会停止计时
     */
    private void stopRequestTiming() {
        mWorkerHandler.removeMessages(BluetoothConstants.MSG_REQUEST_TIMEOUT);
    }

    /**
     * 当worker在任何环节出现任何异常，都会调用本函数通知dispatcher
     *
     * @param result
     */
    private void dispatchRequestResult(boolean result) {
        stopRequestTiming();

        mCurrentRequest = null;

        notifyWorkerResult(result);
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
            setConnectStatus(BluetoothConstants.STATUS_DEVICE_DISCONNECTED);

            broadcastConnectStatus(BluetoothConstants.STATUS_DISCONNECTED);
        }
    }

    private BluetoothGatt openNewBluetoothGatt() {
        BluetoothLog.d("openNewBluetoothGatt");

        closeBluetoothGatt();

        if (mBluetoothGatt == null) {
            mBluetoothGatt = mBluetoothDevice.connectGatt(
                    BluetoothManager.getContext(), false, mConnectCallback);
        }

        return mBluetoothGatt;
    }

    private void processGattFailed() {
        BluetoothLog.d("processGattFailed: " + mCurrentRequest);
        mWorkerHandler.obtainMessage(BluetoothConstants.MSG_GATT_FAILED, null).sendToTarget();
    }

    private void setConnectStatus(int status) {
        mConnectStatus = status;
    }

    private void broadcastConnectStatus(int status) {
        Intent intent = new Intent(
                BluetoothConstants.ACTION_CONNECT_STATUS_CHANGED);
        intent.putExtra(BluetoothConstants.KEY_DEVICE_ADDRESS,
                mBluetoothDevice.getAddress());
        intent.putExtra(BluetoothConstants.KEY_CONNECT_STATUS, status);
        LocalBroadcastManager.getInstance(BluetoothManager.getContext()).sendBroadcast(intent);
    }

    private void notifyWorkerResult(boolean result) {
        mBleDispatcher.notifyWorkerResult(result);
    }

    private final BluetoothGattCallback mConnectCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            // TODO Auto-generated method stub
            BluetoothLog.d(String.format(
                    "onConnectionStateChange: status = %d, newState = %d",
                    status, newState));

            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                setConnectStatus(BluetoothConstants.STATUS_DEVICE_CONNECTED);

                if (isServiceProfileReady()) {
                    processServiceReady();
                } else {
                    gatt.discoverServices();
                }

            } else if (isGattErrorOrFailure(status)) {
                processGattFailed();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                processDisconnected();
            } else {
                /**
                 * 什么也不做
                 */
                BluetoothLog.w(">>> strange state");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            // TODO Auto-generated method stub
            BluetoothLog.d("onServicesDiscovered " + status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                processServiceReady();
            } else {
                processGattFailed();
            }

        }

        private boolean isGattErrorOrFailure(int status) {
            return status == BluetoothGatt.GATT_FAILURE || status == BluetoothConstants.GATT_ERROR;
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            // TODO Auto-generated method stub
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (mCurrentRequest != null && mCurrentRequest instanceof BleReadRequest) {
                    mCurrentRequest.putExtra(BluetoothConstants.KEY_BYTES, characteristic.getValue());
                    dispatchRequestResult(BluetoothConstants.SUCCESS);
                } else {
                    BluetoothLog.w("onCharacteristicRead: current request invalid");
                }

            } else if (isGattErrorOrFailure(status)) {
                processGattFailed();
            } else {
                dispatchRequestResult(BluetoothConstants.FAILED);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            // TODO Auto-generated method stub
            BluetoothLog.d(String.format(
                    "onCharacteristicWrite %s, status = %d",
                    characteristic.getUuid(), status));

            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (mCurrentRequest != null && mCurrentRequest instanceof BleWriteRequest) {
                    mCurrentRequest.putExtra(BluetoothConstants.KEY_BYTES, characteristic.getValue());
                    dispatchRequestResult(BluetoothConstants.SUCCESS);
                } else {
                    BluetoothLog.w("current request unknown");
                }

            } else if (isGattErrorOrFailure(status)) {
                processGattFailed();
            } else {
                dispatchRequestResult(BluetoothConstants.FAILED);
            }
        }

        private void broadcastCharacterChanged(UUID service, UUID character,
                                               byte[] value) {
            Intent intent = new Intent(
                    BluetoothConstants.ACTION_CHARACTER_CHANGED);
            intent.putExtra(BluetoothConstants.KEY_DEVICE_ADDRESS,
                    mBluetoothDevice.getAddress());
            intent.putExtra(BluetoothConstants.KEY_SERVICE_UUID, service);
            intent.putExtra(BluetoothConstants.KEY_CHARACTER_UUID, character);
            intent.putExtra(BluetoothConstants.KEY_CHARACTER_VALUE, value);
            LocalBroadcastManager.getInstance(BluetoothManager.getContext()).sendBroadcast(
                    intent);

            BluetoothLog
                    .d(String
                            .format("broadcastCharacterChanged for %s\n>>> service = %s\n>>> character = %s\n>>> value = %s",
                                    mBluetoothDevice.getAddress(), service,
                                    character, ByteUtils.byteToString(value)));
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            // TODO Auto-generated method stub
            byte[] value = ByteUtils.getNonEmptyByte(characteristic.getValue());

            BluetoothLog
                    .d(String
                            .format("onCharacteristicChanged: \n>>> uuid = %s\n>>> value = (%s)",
                                    characteristic.getUuid(),
                                    ByteUtils.byteToString(value)));

            BluetoothGattService service = characteristic.getService();

            if (service != null) {
                broadcastCharacterChanged(service.getUuid(),
                        characteristic.getUuid(), value);
            } else {
                BluetoothLog.e("onCharacteristicChanged: service null");
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor descriptor, int status) {
            // TODO Auto-generated method stub
            BluetoothGattCharacteristic descChar = descriptor
                    .getCharacteristic();

            BluetoothLog.d(String.format("onDescriptorWrite status = %d",
                    status));

            BluetoothLog.v(String.format(
                    ">>> character: %s\n>>> descriptor: %s", descChar.getUuid(),
                    descriptor.getUuid()));

            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (mCurrentRequest != null) {
                    BluetoothGattCharacteristic character = getCharacter(mCurrentRequest);
                    if (character != null && character == descChar) {
                        if (mCurrentRequest instanceof BleNotifyRequest
                                || mCurrentRequest instanceof BleUnnotifyRequest) {
                            dispatchRequestResult(BluetoothConstants.SUCCESS);
                        } else {
                            BluetoothLog
                                    .w("onDescriptorWrite illegal request");
                        }
                    } else {
                        BluetoothLog
                                .w("onDescriptorWrite illegal character");
                    }
                } else {
                    BluetoothLog
                            .e("current request null");
                }
            } else if (isGattErrorOrFailure(status)) {
                processGattFailed();
            } else {
                dispatchRequestResult(BluetoothConstants.FAILED);
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            BluetoothLog.v(String.format("onReadRemoteRssi rssi = %d, status = %d", rssi, status));

            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (mCurrentRequest != null && mCurrentRequest instanceof BleReadRssiRequest) {
                    mCurrentRequest.putExtra(BluetoothConstants.KEY_RSSI, rssi);
                    dispatchRequestResult(BluetoothConstants.SUCCESS);
                } else {
                    BluetoothLog.e("onReadRemoteRssi: current request invalid");
                }
            } else {
                BluetoothLog.w("onReadRemoteRssi failed");
            }
        }
    };
}
