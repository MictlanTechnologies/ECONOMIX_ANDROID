package com.example.economix_android;

import android.app.Application;

import com.example.economix_android.network.ApiClient;

public class EconomixApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ApiClient.init(this);
    }
}
