package com.inuker.bluetooth.library.search;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.inuker.bluetooth.library.connect.XmBluetoothDevice;
import com.inuker.bluetooth.library.search.classic.BluetoothClassicSearchTask;
import com.inuker.bluetooth.library.search.le.BluetoothLeSearchTask;
import com.inuker.bluetooth.library.utils.BluetoothConstants;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.BluetoothUtils;

import java.util.ArrayList;
import java.util.List;

public class BluetoothSearchRequest {

    private static final int SCAN_INTERVAL = 100;

    private static final int MSG_START_SEARCH = 0x11;

    private List<BluetoothSearchTask> mSearchTaskList;
    private BluetoothSearchResponse mOuterResponse;

    private BluetoothSearchTask mCurrentTask;

    private Handler mHandler = new Handler(Looper.myLooper()) {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case MSG_START_SEARCH:
                    scheduleNewSearchTask();
                    break;
            }

        }

    };
    private final BluetoothSearchResponse mBluetoothSearchResponse = new BluetoothSearchResponse() {

        @Override
        public void onSearchStarted() {
            // TODO Auto-generated method stub
            BluetoothLog.d(String.format("%s onSearchStarted", mCurrentTask.toString()));
        }

        @Override
        public void onDeviceFounded(XmBluetoothDevice device) {
            // TODO Auto-generated method stub
            notifyDeviceFounded(device);
        }

        @Override
        public void onSearchStopped() {
            // TODO Auto-generated method stub
            mHandler.sendEmptyMessageDelayed(MSG_START_SEARCH, SCAN_INTERVAL);
        }

        @Override
        public void onSearchCanceled() {
            // TODO Auto-generated method stub
            /**
             * 这里什么都不用做
             */
        }

    };

    public BluetoothSearchRequest() {
        mSearchTaskList = new ArrayList<BluetoothSearchTask>();
    }

    public void start() {
        if (mOuterResponse != null) {
            mOuterResponse.onSearchStarted();
        }

        notifyConnectedBluetoothDevices();
        mHandler.obtainMessage(MSG_START_SEARCH).sendToTarget();
    }

    private void scheduleNewSearchTask() {
        if (mSearchTaskList.size() > 0) {
            mCurrentTask = mSearchTaskList.remove(0);
            mCurrentTask.start(mBluetoothSearchResponse);
        } else {
            mCurrentTask = null;

            if (mOuterResponse != null) {
                mOuterResponse.onSearchStopped();
            }
        }
    }

    public void cancel() {
        if (mCurrentTask != null) {
            mCurrentTask.cancel();
            mCurrentTask = null;
        }

        mSearchTaskList.clear();

        if (mOuterResponse != null) {
            mOuterResponse.onSearchCanceled();
        }

        mOuterResponse = null;
    }

    private void notifyConnectedBluetoothDevices() {
        boolean hasBleTask = false;
        boolean hasBscTask = false;

        for (BluetoothSearchTask task : mSearchTaskList) {
            if (task.isBluetoothLeSearch()) {
                hasBleTask = true;
            } else if (task.isBluetoothClassicSearch()) {
                hasBscTask = true;
            } else {
                throw new IllegalArgumentException("unknown search task type!");
            }
        }

        if (hasBleTask) {
            notifyConnectedBluetoothLeDevices();
        }

        if (hasBscTask) {
            notifyBondedBluetoothClassicDevices();
        }
    }

    private void notifyConnectedBluetoothLeDevices() {
        List<BluetoothDevice> devices = BluetoothUtils
                .getConnectedBluetoothLeDevices();

        for (BluetoothDevice device : devices) {
            XmBluetoothDevice xmDevice = new XmBluetoothDevice(device,
                    XmBluetoothDevice.DEVICE_TYPE_BLE);
            xmDevice.isConnected = true;
            notifyDeviceFounded(xmDevice);
        }
    }

    private void notifyBondedBluetoothClassicDevices() {
        List<BluetoothDevice> devices = BluetoothUtils
                .getBondedBluetoothClassicDevices();

        for (BluetoothDevice device : devices) {
            XmBluetoothDevice xmDevice = new XmBluetoothDevice(device,
                    XmBluetoothDevice.DEVICE_TYPE_CLASSIC);
            xmDevice.isConnected = true;
            notifyDeviceFounded(xmDevice);
        }
    }

    private void notifyDeviceFounded(XmBluetoothDevice device) {
        if (mOuterResponse != null) {
            mOuterResponse.onDeviceFounded(device);
        }
    }

    public void addSearchTasks(List<BluetoothSearchTask> tasks) {
        mSearchTaskList.clear();
        mSearchTaskList.addAll(tasks);
    }

    public void setSearchResponse(BluetoothSearchResponse response) {
        mOuterResponse = response;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();

        for (BluetoothSearchTask task : mSearchTaskList) {
            sb.append(task.toString() + ", ");
        }

        return sb.toString();
    }

    public static class Builder {
        private List<BluetoothSearchTask> searchTaskList;
        private BluetoothSearchResponse searchResponse;

        public Builder() {
            searchTaskList = new ArrayList<BluetoothSearchTask>();
        }

        private void addSearchTask(BluetoothSearchTask task) {
            if (task instanceof BluetoothLeSearchTask) {
                if (BluetoothUtils.isBleSupported()) {
                    searchTaskList.add(task);
                }
            } else if (task instanceof BluetoothClassicSearchTask) {
                searchTaskList.add(task);
            }
        }

        public Builder searchBluetoothLeDevice() {
            searchBluetoothLeDevice(BluetoothConstants.DEFAULT_DURATION);
            return this;
        }

        public Builder multiSearchBluetoothLeDevice(int times) {
            for (int i = 0; i < times; i++) {
                searchBluetoothLeDevice();
            }
            return this;
        }

        public Builder searchBluetoothLeDevice(int duration) {
            BluetoothSearchTask search = new BluetoothLeSearchTask();
            search.setSearchDuration(duration);
            addSearchTask(search);
            return this;
        }

        public Builder multiSearchBluetoothLeDevice(int duration, int times) {
            for (int i = 0; i < times; i++) {
                searchBluetoothLeDevice(duration);
            }
            return this;
        }

        public Builder searchBluetoothClassicDevice() {
            searchBluetoothClassicDevice(BluetoothConstants.DEFAULT_DURATION);
            return this;
        }

        public Builder multiSearchBluetoothClassicDevice(int times) {
            for (int i = 0; i < times; i++) {
                searchBluetoothClassicDevice();
            }
            return this;
        }

        public Builder searchBluetoothClassicDevice(int duration) {
            BluetoothSearchTask search = new BluetoothClassicSearchTask();
            search.setSearchDuration(duration);
            addSearchTask(search);
            return this;
        }

        public Builder multiSearchBluetoothClassicDevice(int duration, int times) {
            for (int i = 0; i < times; i++) {
                searchBluetoothClassicDevice(duration);
            }
            return this;
        }

        public Builder setSearchResponse(BluetoothSearchResponse response) {
            this.searchResponse = response;
            return this;
        }

        public BluetoothSearchRequest build() {
            BluetoothSearchRequest group = new BluetoothSearchRequest();
            group.addSearchTasks(searchTaskList);
            group.setSearchResponse(searchResponse);
            return group;
        }

    }
}
