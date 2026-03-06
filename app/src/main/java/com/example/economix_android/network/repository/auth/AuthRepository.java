package com.example.economix_android.network.repository.auth;

import android.content.Context;

import com.example.economix_android.network.auth.AuthApi;
import com.example.economix_android.network.auth.AuthServiceFactory;
import com.example.economix_android.network.auth.dto.LoginRequest;
import com.example.economix_android.network.auth.dto.LoginResponse;
import com.example.economix_android.network.auth.dto.LogoutRequest;
import com.example.economix_android.network.auth.dto.OtpCodeRequest;
import com.example.economix_android.network.auth.dto.RefreshRequest;
import com.example.economix_android.network.auth.dto.RefreshResponse;
import com.example.economix_android.network.auth.dto.TwoFaSetupResponse;
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

    public void setup2fa(Callback<TwoFaSetupResponse> callback) {
        authApi.setup2fa().enqueue(callback);
    }

    public void enable2fa(OtpCodeRequest request, Callback<Void> callback) {
        authApi.enable2fa(request).enqueue(callback);
    }

    public void disable2fa(OtpCodeRequest request, Callback<Void> callback) {
        authApi.disable2fa(request).enqueue(callback);
    }

    public Call<RefreshResponse> refreshSync(RefreshRequest request) {
        return authApi.refresh(request);
    }
}
