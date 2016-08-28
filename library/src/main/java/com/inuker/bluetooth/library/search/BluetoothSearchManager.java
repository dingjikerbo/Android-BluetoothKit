package com.inuker.bluetooth.library.search;

import android.os.Bundle;

import com.inuker.bluetooth.library.IBluetoothConstants;
import com.inuker.bluetooth.library.connect.response.BluetoothResponse;
import com.inuker.bluetooth.library.utils.BluetoothUtils;

/**
 * Created by liwentian on 2016/8/28.
 */
public class BluetoothSearchManager implements IBluetoothConstants {

    public static void search(SearchRequest request, final BluetoothResponse response) {
        if (!BluetoothUtils.isBluetoothEnabled()) {
            return;
        }

        BluetoothSearchRequest requestWrapper = new BluetoothSearchRequest(request);
        BluetoothSearchHelper.getInstance().startSearch(requestWrapper, new BluetoothSearchResponse() {
            @Override
            public void onSearchStarted() {
                response.onSafeResponse(SEARCH_START, null);
            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(EXTRA_SEARCH_RESULT, device);
                response.onSafeResponse(DEVICE_FOUND, null);
            }

            @Override
            public void onSearchStopped() {
                response.onSafeResponse(SEARCH_STOP, null);
            }

            @Override
            public void onSearchCanceled() {
                response.onSafeResponse(SEARCH_CANCEL, null);
            }
        });
    }

    public static void stopSearch() {
        BluetoothSearchHelper.getInstance().stopSearch();
    }
}
