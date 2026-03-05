package com.example.economix_android.network.repository.auth;

import android.content.Context;

import com.example.economix_android.network.auth.AuthApi;
import com.example.economix_android.network.auth.AuthServiceFactory;
import com.example.economix_android.network.auth.dto.LoginRequest;
import com.example.economix_android.network.auth.dto.LoginResponse;
import com.example.economix_android.network.auth.dto.LogoutRequest;
import com.example.economix_android.network.auth.dto.RefreshRequest;
import com.example.economix_android.network.auth.dto.RefreshResponse;
import com.example.economix_android.network.auth.dto.Verify2faRequest;
import com.example.economix_android.network.auth.dto.Verify2faResponse;

import retrofit2.Call;
import retrofit2.Callback;

public class AuthRepository {

    private final AuthApi authApi;

    public AuthRepository(Context context) {
        this.authApi = AuthServiceFactory.getAuthApi(context);
    }

    public void login(LoginRequest request, Callback<LoginResponse> callback) {
        authApi.login(request).enqueue(callback);
    }

    public void verify2fa(Verify2faRequest request, Callback<Verify2faResponse> callback) {
        authApi.verify2fa(request).enqueue(callback);
    }

    public void refresh(RefreshRequest request, Callback<RefreshResponse> callback) {
        authApi.refresh(request).enqueue(callback);
    }

    public void logout(LogoutRequest request, Callback<Void> callback) {
        authApi.logout(request).enqueue(callback);
    }

    public Call<RefreshResponse> refreshSync(RefreshRequest request) {
        return authApi.refresh(request);
    }
}
