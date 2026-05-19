package com.example.economix_android.network.auth.dto;

public class Verify2faResponse {
    private String accessToken;
    private String refreshToken;
    private UserInfo userInfo;

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }
}
