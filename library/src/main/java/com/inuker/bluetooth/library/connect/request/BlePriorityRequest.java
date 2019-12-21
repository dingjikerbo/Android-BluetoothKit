package com.inuker.bluetooth.library.connect.request;

import com.inuker.bluetooth.library.Code;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse;

public class BlePriorityRequest extends BleRequest {

    private int priority;

    public BlePriorityRequest(int priority, BleGeneralResponse response) {
        super(response);
        this.priority = priority;
    }

    @Override
    public void processRequest() {
        switch (getCurrentStatus()) {

            case Constants.STATUS_DEVICE_CONNECTED:
            case Constants.STATUS_DEVICE_SERVICE_READY:
                requestConnectionPriority();
                break;

            default://Constants.STATUS_DEVICE_DISCONNECTED or Others
                onRequestCompleted(Code.REQUEST_FAILED);
                break;
        }
    }

    private void requestConnectionPriority() {
        if (!requestConnectionPriority(priority)) {
            onRequestCompleted(Code.REQUEST_FAILED);
        } else {
            onRequestCompleted(Code.REQUEST_SUCCESS);
        }
    }

}
