// IBleResponse.aidl
package com.inuker.bluetooth.library;

// Declare any non-default types here with import statements

interface IBleResponse {
    void onResponse(int code, out Bundle data);
}
