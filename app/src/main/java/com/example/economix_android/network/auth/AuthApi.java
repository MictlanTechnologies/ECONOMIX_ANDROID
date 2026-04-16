package com.example.economix_android.network.auth;

import com.example.economix_android.network.auth.dto.LoginRequest;
import com.example.economix_android.network.auth.dto.LoginResponse;
import com.example.economix_android.network.auth.dto.LogoutRequest;
import com.example.economix_android.network.auth.dto.OtpCodeRequest;
import com.example.economix_android.network.auth.dto.RefreshRequest;
import com.example.economix_android.network.auth.dto.RefreshResponse;
import com.example.economix_android.network.auth.dto.TwoFaSetupResponse;
import com.example.economix_android.network.auth.dto.TwoFactorToggleResponse;
import com.example.economix_android.network.auth.dto.Verify2faRequest;
import com.example.economix_android.network.auth.dto.Verify2faResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/2fa/verify")
    Call<Verify2faResponse> verify2fa(@Body Verify2faRequest request);

    @POST("auth/refresh")
    Call<RefreshResponse> refresh(@Body RefreshRequest request);

    @POST("auth/logout")
    Call<Void> logout(@Body LogoutRequest request);

    @POST("users/2fa/setup")
    Call<TwoFaSetupResponse> setup2fa();

    @POST("users/2fa/enable")
    Call<TwoFactorToggleResponse> enable2fa(@Body OtpCodeRequest request);

    @POST("users/2fa/disable")
    Call<TwoFactorToggleResponse> disable2fa(@Body OtpCodeRequest request);
}
