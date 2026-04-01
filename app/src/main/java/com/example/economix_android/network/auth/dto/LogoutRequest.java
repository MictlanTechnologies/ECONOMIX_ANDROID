package com.example.economix_android.network.auth.dto;

public class LogoutRequest {
    private String refreshToken;

    public LogoutRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
