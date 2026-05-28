package com.example.economix_android.network.auth.dto;

public class Verify2faRequest {
    private String challengeId;
    private String code;

    public Verify2faRequest(String challengeId, String code) {
        this.challengeId = challengeId;
        this.code = code;
    }

    public String getChallengeId() {
        return challengeId;
    }

    public String getCode() {
        return code;
    }
}
