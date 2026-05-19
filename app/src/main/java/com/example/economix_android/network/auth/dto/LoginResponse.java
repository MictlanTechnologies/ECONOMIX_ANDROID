package com.example.economix_android.network.auth.dto;

public class LoginResponse {
    private boolean requires2fa;
    private String challengeId;
    private String challengeExpiresAt;
    private String accessToken;
    private String refreshToken;
    private UserInfo userInfo;

    public boolean isRequires2fa() {
        return requires2fa;
    }

    public String getChallengeId() {
        return challengeId;
    }

    public String getChallengeExpiresAt() {
        return challengeExpiresAt;
    }

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
