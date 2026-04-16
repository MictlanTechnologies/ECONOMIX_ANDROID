package com.example.economix_android.network.auth.dto;

public class Verify2faRequest {
    private String challengeId;
    private String otpCode;

    public Verify2faRequest(String challengeId, String otpCode) {
        this.challengeId = challengeId;
        this.otpCode = otpCode;
    }

    public String getChallengeId() {
        return challengeId;
    }

    public String getOtpCode() {
        return otpCode;
    }
}
