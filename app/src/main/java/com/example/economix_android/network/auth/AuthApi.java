package com.example.economix_android.network.auth;

import com.example.economix_android.network.auth.dto.LoginRequest;
import com.example.economix_android.network.auth.dto.LoginResponse;
import com.example.economix_android.network.auth.dto.LogoutRequest;
import com.example.economix_android.network.auth.dto.RefreshRequest;
import com.example.economix_android.network.auth.dto.RefreshResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);


    @POST("auth/refresh")
    Call<RefreshResponse> refresh(@Body RefreshRequest request);

    @POST("auth/logout")
    Call<Void> logout(@Body LogoutRequest request);

}
