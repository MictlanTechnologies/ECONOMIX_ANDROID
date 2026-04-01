package com.example.economix_android.network.auth;

import android.content.Context;

import com.example.economix_android.network.ApiClient;

public final class AuthServiceFactory {

    private AuthServiceFactory() {
    }

    public static AuthApi getAuthApi(Context context) {
        ApiClient.init(context.getApplicationContext());
        return ApiClient.getAuthApi();
    }
}
