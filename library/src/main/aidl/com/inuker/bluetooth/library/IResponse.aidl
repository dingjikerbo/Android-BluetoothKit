// IBleResponse.aidl
package com.inuker.bluetooth.library;

// Declare any non-default types here with import statements

interface IResponse {
    void onResponse(int code, inout Bundle data);
}
