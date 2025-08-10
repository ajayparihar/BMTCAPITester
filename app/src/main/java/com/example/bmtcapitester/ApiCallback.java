package com.example.bmtcapitester;

public interface ApiCallback {
    void onSuccess(String response);
    void onError(String error);
}
