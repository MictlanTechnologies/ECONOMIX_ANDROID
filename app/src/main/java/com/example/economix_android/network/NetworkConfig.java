package com.example.economix_android.network;

import android.content.Context;

public final class NetworkConfig {

    private NetworkConfig() {
    }

    public static String getBaseUrl(Context context) {
        return ServerUrlManager.getBaseUrl(context.getApplicationContext());
    }
}
