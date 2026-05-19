package com.example.economix_android.auth.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.economix_android.Vista.menu;
import com.example.economix_android.auth.SessionManager;
import com.example.economix_android.network.auth.dto.RefreshRequest;
import com.example.economix_android.network.auth.dto.RefreshResponse;
import com.example.economix_android.network.repository.auth.AuthRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
        authRepository = new AuthRepository(this);

        String refreshToken = sessionManager.getRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            openLogin();
            return;
        }

        authRepository.refresh(new RefreshRequest(refreshToken), new Callback<>() {
            @Override
            public void onResponse(Call<RefreshResponse> call, Response<RefreshResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String newRefresh = response.body().getRefreshToken() == null
                            ? refreshToken : response.body().getRefreshToken();
                    sessionManager.updateTokens(response.body().getAccessToken(), newRefresh);
                    openHome();
                } else {
                    sessionManager.clearSession();
                    openLogin();
                }
            }

            @Override
            public void onFailure(Call<RefreshResponse> call, Throwable t) {
                sessionManager.clearSession();
                openLogin();
            }
        });
    }

    private void openLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void openHome() {
        Intent intent = new Intent(this, menu.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
